/*
 * Copyright 2020 GridGain Systems, Inc. and Contributors.
 *
 * Licensed under the GridGain Community Edition License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.gridgain.com/products/software/community-edition/gridgain-community-edition-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.internal.processors.cache.transactions;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteSystemProperties;
import org.apache.ignite.IgniteTransactions;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheWriteSynchronizationMode;
import org.apache.ignite.cache.affinity.rendezvous.RendezvousAffinityFunction;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.internal.IgniteEx;
import org.apache.ignite.internal.IgniteInternalFuture;
import org.apache.ignite.internal.TestRecordingCommunicationSpi;
import org.apache.ignite.internal.binary.BinaryMarshaller;
import org.apache.ignite.internal.processors.cache.GridCacheEntryEx;
import org.apache.ignite.internal.processors.cache.distributed.dht.GridDhtTxFinishRequest;
import org.apache.ignite.internal.processors.cache.distributed.dht.GridDhtTxFinishResponse;
import org.apache.ignite.internal.processors.cache.distributed.dht.GridDhtTxPrepareResponse;
import org.apache.ignite.internal.processors.cache.distributed.near.GridNearLockRequest;
import org.apache.ignite.internal.processors.cache.distributed.near.GridNearTxFinishResponse;
import org.apache.ignite.internal.processors.cache.distributed.near.GridNearTxPrepareResponse;
import org.apache.ignite.internal.util.IgniteUtils;
import org.apache.ignite.internal.util.future.GridCompoundFuture;
import org.apache.ignite.internal.util.typedef.G;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.lang.IgniteBiPredicate;
import org.apache.ignite.plugin.extensions.communication.Message;
import org.apache.ignite.testframework.GridTestUtils;
import org.apache.ignite.testframework.junits.WithSystemProperty;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.apache.ignite.transactions.Transaction;
import org.apache.ignite.transactions.TransactionConcurrency;
import org.apache.ignite.transactions.TransactionIsolation;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.apache.ignite.IgniteSystemProperties.IGNITE_DUMP_TX_COLLISIONS_INTERVAL;
import static org.apache.ignite.IgniteSystemProperties.IGNITE_LONG_OPERATIONS_DUMP_TIMEOUT;
import static org.apache.ignite.transactions.TransactionConcurrency.PESSIMISTIC;
import static org.apache.ignite.transactions.TransactionIsolation.READ_COMMITTED;

/** */
public class TxWithKeyContentionSelfTest extends GridCommonAbstractTest {
    /** */
    private boolean client;

    /** {@inheritDoc} */
    @Override protected IgniteConfiguration getConfiguration(String name) throws Exception {
        IgniteConfiguration cfg = super.getConfiguration(name);

        cfg.setConsistentId("NODE_" + name.substring(name.length() - 1));

        if (client)
            cfg.setClientMode(true);

        cfg.setDataStorageConfiguration(
            new DataStorageConfiguration()
                .setDefaultDataRegionConfiguration(
                    new DataRegionConfiguration()
                        .setPersistenceEnabled(true) // todo !!!
                        .setMaxSize(DataStorageConfiguration.DFLT_DATA_REGION_INITIAL_SIZE)
                )
        );

        TestRecordingCommunicationSpi commSpi = new TestRecordingCommunicationSpi();

        cfg.setCommunicationSpi(commSpi);

        cfg.setCacheConfiguration(getCacheConfiguration(DEFAULT_CACHE_NAME));

        if (client){
            cfg.setConsistentId("Client");

            cfg.setClientMode(client);
        }

        return cfg;
    }

    /** */
    protected TransactionConcurrency getConcurrency() {
       return PESSIMISTIC;
    }

    /** */
    protected TransactionIsolation getIsolation() {
        return READ_COMMITTED;
    }

    /** */
    protected CacheConfiguration<?, ?> getCacheConfiguration(String name) {
        return
            new CacheConfiguration<>(name)
                .setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL)
                .setWriteSynchronizationMode(CacheWriteSynchronizationMode.FULL_SYNC)
                .setAffinity(new RendezvousAffinityFunction(false, 16))
                .setBackups(2);
    }

    /** {@inheritDoc} */
    @Override protected void beforeTest() throws Exception {
        super.beforeTest();

        stopAllGrids();

        cleanPersistenceDir();
    }

    /** {@inheritDoc} */
    @Override protected void afterTest() throws Exception {
        super.afterTest();

        stopAllGrids();

        cleanPersistenceDir();
    }

    /** */
    @Test
    @WithSystemProperty(key = IGNITE_DUMP_TX_COLLISIONS_INTERVAL, value = "30_000")
    public void testA() throws Exception {
        Ignite ig = startGridsMultiThreaded(3);

        int contCnt = 100;

        ig.cluster().active(true);

        client = true;

        Ignite cl = startGrid();

        IgniteTransactions txMgr = cl.transactions();

        IgniteCache<Integer, Integer> cache = ig.cache(DEFAULT_CACHE_NAME);

        IgniteCache<Integer, Integer> cache0 = cl.cache(DEFAULT_CACHE_NAME);

        for (int i = 0 ; i < 4; ++i)
            cache.put(i, i);

        final Integer keyId1 = backupKey(cache);

        final Integer keyId2 = primaryKey(cache);

        CountDownLatch startOneKeyTx = new CountDownLatch(1);

        CountDownLatch neadLockReq = new CountDownLatch(contCnt);

        for (Ignite ig0 : G.allGrids()) {
/*            if (ig0.configuration().isClientMode())
                continue;*/

            TestRecordingCommunicationSpi commSpi0 =
                (TestRecordingCommunicationSpi)ig0.configuration().getCommunicationSpi();

            commSpi0.blockMessages(new IgniteBiPredicate<ClusterNode, Message>() {
                @Override public boolean apply(ClusterNode node, Message msg) {
                    System.err.println("!!! " + msg);

/*                    if (msg instanceof GridNearTxFinishResponse)
                        return true;

                    if (msg instanceof GridDhtTxFinishRequest) {
                        latch.countDown();

                        return false;
                    }*/

                    if (msg instanceof GridDhtTxFinishRequest)
                        return true;

                    if (msg instanceof GridNearTxPrepareResponse)
                        startOneKeyTx.countDown();

                    if (msg instanceof GridNearLockRequest)
                        neadLockReq.countDown();

                    return false;
                }
            });
        }

        IgniteInternalFuture f = GridTestUtils.runAsync(() -> {
            try (Transaction tx2 = txMgr.txStart(getConcurrency(), getIsolation())) {
                //cache0.put(keyId2, 0);
                cache0.put(keyId1, 0);
                tx2.commit();
            }
        });

        startOneKeyTx.await();

        GridCompoundFuture<?, ?> finishFut = new GridCompoundFuture<>();

        for (int i = 1; i < contCnt; ++i) {
            int finalI = i;
            IgniteInternalFuture f0 = GridTestUtils.runAsync(() -> {
                try (Transaction tx = txMgr.txStart(getConcurrency(), getIsolation())) {

                    cache0.put(keyId1, finalI);
                    //cache0.put(keyId2, finalI);

                    tx.commit();
                }
            });

            finishFut.add(f0);
        }

        finishFut.markInitialized();

        neadLockReq.await();

        U.sleep(500);

        for (Ignite ig0 : G.allGrids()) {
/*            if (ig0.configuration().isClientMode())
                continue;*/

            TestRecordingCommunicationSpi commSpi0 =
                (TestRecordingCommunicationSpi)ig0.configuration().getCommunicationSpi();

            commSpi0.stopBlock();
        }

        IgniteTxManager txManager = ((IgniteEx) ig).context().cache().context().tm();

        IgniteTxManager.KeyCollisionsDetector<GridCacheEntryEx, Integer> detector =
            U.field(txManager, "keyCollisionsInfo");

        U.invoke(IgniteTxManager.KeyCollisionsDetector.class, detector, "collectInfo", null, null);

        U.sleep(1000);

        System.err.println("**************************8");

        U.invoke(IgniteTxManager.KeyCollisionsDetector.class, detector, "collectInfo", null, null);

        f.get();

        finishFut.get();
    }
}
