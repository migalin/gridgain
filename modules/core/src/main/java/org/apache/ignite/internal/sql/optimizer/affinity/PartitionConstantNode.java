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

package org.apache.ignite.internal.sql.optimizer.affinity;

import org.apache.ignite.internal.util.typedef.internal.S;

/**
 * Node with constant partition.
 */
public class PartitionConstantNode extends PartitionSingleNode {
    /** Partition. */
    private final int part;

    /**
     * Constructor.
     *
     * @param tbl Table.
     * @param part Partition.
     */
    public PartitionConstantNode(PartitionTable tbl, int part) {
        super(tbl);

        this.part = part;
    }

    /** {@inheritDoc} */
    @Override public Integer applySingle(PartitionClientContext cliCtx, Object... args) {
        return part;
    }

    /** {@inheritDoc} */
    @Override public boolean constant() {
        return true;
    }

    /** {@inheritDoc} */
    @Override public int value() {
        return part;
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return S.toString(PartitionConstantNode.class, this);
    }
}
