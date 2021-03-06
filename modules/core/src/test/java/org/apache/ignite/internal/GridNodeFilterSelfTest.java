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

package org.apache.ignite.internal;

import java.util.Collection;
import java.util.UUID;
import org.apache.ignite.Ignite;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.apache.ignite.testframework.junits.common.GridCommonTest;
import org.junit.Test;

/**
 * Node filter test.
 */
@GridCommonTest(group = "Kernal Self")
public class GridNodeFilterSelfTest extends GridCommonAbstractTest {
    /** Grid instance. */
    private static Ignite ignite;

    /** Remote instance. */
    private static Ignite rmtIgnite;

    /** */
    public GridNodeFilterSelfTest() {
        super(false);
    }

    /** {@inheritDoc} */
    @Override protected void beforeTestsStarted() throws Exception {
        ignite = startGrid(1);

        rmtIgnite = startGrid(2);
        startGrid(3);
    }

    /** {@inheritDoc} */
    @Override protected void afterTestsStopped() throws Exception {
        ignite = null;

        rmtIgnite = null;
    }

    /**
     * @throws Exception If failed.
     */
    @Test
    public void testSynchronousExecute() throws Exception {
        UUID nodeId = ignite.cluster().localNode().id();

        UUID rmtNodeId = rmtIgnite.cluster().localNode().id();

        Collection<ClusterNode> locNodes = ignite.cluster().forNodeId(nodeId).nodes();

        assert locNodes.size() == 1;
        assert locNodes.iterator().next().id().equals(nodeId);

        Collection<ClusterNode> rmtNodes = ignite.cluster().forNodeId(rmtNodeId).nodes();

        assert rmtNodes.size() == 1;
        assert rmtNodes.iterator().next().id().equals(rmtNodeId);
    }
}
