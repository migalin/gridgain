/*
 * Copyright 2019 GridGain Systems, Inc. and Contributors.
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

package org.apache.ignite.internal.processors.cache.mvcc;

import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.affinity.Affinity;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.transactions.Transaction;
import org.junit.Test;

import static org.apache.ignite.transactions.TransactionConcurrency.PESSIMISTIC;
import static org.apache.ignite.transactions.TransactionIsolation.REPEATABLE_READ;

/** */
public class CacheMvccRemoteTxOnNearNodeStartTest extends CacheMvccAbstractTest {
    /** {@inheritDoc} */
    @Override protected CacheMode cacheMode() {
        return CacheMode.PARTITIONED;
    }

    /**
     * Ensures that remote transaction on near node is started
     * when first request is sent to OWNING partition and second to MOVING partition.
     * @throws Exception if failed.
     */
    @Test
    public void testRemoteTxOnNearNodeIsStartedIfPartitionIsMoving() throws Exception {
        startGridsMultiThreaded(3);

        IgniteCache<Object, Object> cache = grid(0).getOrCreateCache(new CacheConfiguration<>(DEFAULT_CACHE_NAME)
            .setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL_SNAPSHOT)
            .setCacheMode(cacheMode())
            .setBackups(1)
        );

        ArrayList<Integer> keys = new ArrayList<>();

        Affinity<Object> aff = grid(0).affinity(DEFAULT_CACHE_NAME);

        for (int i = 0; i < 100; i++) {
            if (aff.isPrimary(grid(1).localNode(), i) && aff.isBackup(grid(0).localNode(), i)) {
                keys.add(i);
                break;
            }
        }

        for (int i = 0; i < 100; i++) {
            if (aff.isPrimary(grid(1).localNode(), i) && aff.isBackup(grid(2).localNode(), i)) {
                keys.add(i);
                break;
            }
        }

        assert keys.size() == 2;

        stopGrid(2);

        try (Transaction tx = grid(0).transactions().txStart(PESSIMISTIC, REPEATABLE_READ)) {
            cache.putAll(ImmutableMap.of(
                keys.get(0), 0,
                keys.get(1), 1)
            );

            tx.commit();
        }

        // assert transaction was committed without errors
    }
}
