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

package org.apache.ignite.spi.discovery.zk;

import org.apache.ignite.internal.IgniteClientReconnectCacheTest;
import org.apache.ignite.internal.processors.cache.distributed.near.GridCachePartitionedMultiNodeFullApiSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.near.GridCachePartitionedNodeRestartTest;
import org.apache.ignite.internal.processors.cache.distributed.replicated.GridCacheReplicatedMultiNodeFullApiSelfTest;
import org.apache.ignite.util.GridCommandHandlerTest;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Regular Ignite tests executed with {@link org.apache.ignite.spi.discovery.zk.ZookeeperDiscoverySpi}.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    GridCachePartitionedNodeRestartTest.class,
    IgniteCacheEntryListenerWithZkDiscoAtomicTest.class,
    IgniteClientReconnectCacheTest.class,
    GridCachePartitionedMultiNodeFullApiSelfTest.class,
    GridCacheReplicatedMultiNodeFullApiSelfTest.class,
    GridCommandHandlerTest.class
})
public class ZookeeperDiscoverySpiTestSuite2  {
    /**
     * @throws Exception Thrown in case of the failure.
     */
    @BeforeClass
    public static void init() throws Exception {
        ZookeeperDiscoverySpiTestConfigurator.initTestSuite();
    }
}
