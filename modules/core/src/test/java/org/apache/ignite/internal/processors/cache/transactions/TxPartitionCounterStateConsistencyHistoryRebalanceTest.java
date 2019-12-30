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

package org.apache.ignite.internal.processors.cache.transactions;

import org.apache.ignite.internal.IgniteEx;
import org.apache.ignite.testframework.junits.WithSystemProperty;
import org.junit.Test;

import static org.apache.ignite.IgniteSystemProperties.IGNITE_PDS_WAL_REBALANCE_THRESHOLD;

/**
 * Test partitions consistency in various scenarios when all rebalance is historical.
 */
@WithSystemProperty(key = IGNITE_PDS_WAL_REBALANCE_THRESHOLD, value = "0")
public class TxPartitionCounterStateConsistencyHistoryRebalanceTest extends TxPartitionCounterStateConsistencyTest {
    /**
     */
    @Test
    public void testConsistencyAfterBaselineNodeStopAndRemoval() throws Exception {
        backups = 2;

        final int srvNodes = SERVER_NODES + 1; // Add one non-owner node to test to increase entropy.

        IgniteEx prim = startGrids(srvNodes);

        prim.cluster().active(true);

        for (int p = 0; p < partitions(); p++) {
            prim.cache(DEFAULT_CACHE_NAME).put(p, p);
            prim.cache(DEFAULT_CACHE_NAME).put(p + partitions(), p * 2);
        }

        forceCheckpoint();

        stopGrid(1); // topVer=5,0

        awaitPartitionMapExchange();

        resetBaselineTopology(); // topVer=5,1

        awaitPartitionMapExchange();

        forceCheckpoint(); // Will force GridCacheDataStore.exists=true mode after part store re-creation.

        startGrid(1); // topVer=6,0

        awaitPartitionMapExchange();

        resetBaselineTopology(); // topVer=6,1

        awaitPartitionMapExchange(true, true, null);

        // Create counter difference with evicted partition so it's applicable for historical rebalancing.
        for (int p = 0; p < partitions(); p++)
            prim.cache(DEFAULT_CACHE_NAME).put(p + partitions(), p * 2 + 1);

        stopGrid(1); // topVer=7,0

        awaitPartitionMapExchange();

        resetBaselineTopology(); // topVer=7,1

        awaitPartitionMapExchange();

        assertPartitionsSame(idleVerify(prim, DEFAULT_CACHE_NAME));
    }
}
