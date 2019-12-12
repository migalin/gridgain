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

package org.apache.ignite.util;

import java.util.HashSet;
import java.util.Set;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.internal.IgniteEx;
import org.apache.ignite.internal.util.typedef.internal.U;

import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.of;
import static org.apache.ignite.internal.processors.cache.CacheType.cacheType;

/**
 * It is recommended to extends from this class in case of creating a cluster
 * once before all tests. Otherwise, use
 * {@link GridCommandHandlerClusterPerMethodAbstractTest}
 * */
public abstract class GridCommandHandlerClusterByClassAbstractTest extends GridCommandHandlerAbstractTest {
    /** Number of server nodes. */
    protected static final int SERVER_NODE_CNT = 2;

    /** Coordinator. */
    protected static IgniteEx crd;

    /** Client node. */
    protected static IgniteEx client;

    /** {@inheritDoc} */
    @Override protected void beforeTestsStarted() throws Exception {
        super.beforeTestsStarted();

        log.warning("Threads dump beforeTests: ");

        U.dumpThreads(log);

        crd = startGrids(SERVER_NODE_CNT);

        crd.cluster().baselineAutoAdjustEnabled(false);

        client = startGrid(CLIENT_NODE_NAME_PREFIX);

        crd.cluster().active(true);
    }

    /** {@inheritDoc} */
    @Override protected void afterTestsStopped() throws Exception {
        super.afterTestsStopped();

        stopAllGrids();

        cleanPersistenceDir();

        log.warning("Threads dump afterTests: ");

        U.dumpThreads(log);
    }

    /** {@inheritDoc} */
    @Override protected void afterTest() throws Exception {
        super.afterTest();

        Set<String> cfgCacheNames = of(crd.configuration().getCacheConfiguration())
            .map(CacheConfiguration::getName)
            .filter(cacheName -> cacheType(cacheName).userCache())
            .collect(toSet());

        Set<String> rmvCacheNames = new HashSet<>(crd.cacheNames());
        rmvCacheNames.removeAll(cfgCacheNames);

        crd.destroyCaches(rmvCacheNames);

        cfgCacheNames.stream()
            .map(crd::cache)
            .forEach(IgniteCache::removeAll);
    }
}
