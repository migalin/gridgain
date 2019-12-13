/*
 * Copyright 2019 GridGain Systems, Inc. and Contributors.
 *
 * Licensed under the GridGain Community Edition License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.gridgain.com/products/software/community-edition/gridgain-community-edition-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.internal.processors.cache.checker.objects;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.ignite.internal.processors.cache.verify.RepairMeta;
import org.apache.ignite.internal.util.typedef.T2;

/** Result of {@code RepairResultTask}. */
public class RepairResult {

    /** Keys to repair with corresponding values and versions per nodes. */
    private Map<PartitionKeyVersion, Map<UUID, VersionedValue>> keysToRepair = new HashMap<>();

    /** Repaired keys. */
    // TODO: 05.12.19 I don't like idea of tuple here.
    private Map<T2<PartitionKeyVersion, RepairMeta>, Map<UUID, VersionedValue>> repairedKeys =
        new HashMap<>();

    /**
     * Default constructor.
     */
    public RepairResult() {
    }

    /**
     * Constructor.
     *
     * @param keysToRepair Keys to repair within next recheck-repair iteration.
     * @param repairedKeys Repaired keys.
     */
    @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType") public RepairResult(
        Map<PartitionKeyVersion, Map<UUID, VersionedValue>> keysToRepair,
        Map<T2<PartitionKeyVersion, RepairMeta>, Map<UUID, VersionedValue>> repairedKeys) {
        this.keysToRepair = keysToRepair;
        this.repairedKeys = repairedKeys;
    }

    /**
     * @return Keys to repair.
     */
    @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
    public Map<PartitionKeyVersion, Map<UUID, VersionedValue>> keysToRepair() {
        return keysToRepair;
    }

    /**
     * @return Repaired keys.
     */
    @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
    public Map<T2<PartitionKeyVersion, RepairMeta>, Map<UUID, VersionedValue>> repairedKeys() {
        return repairedKeys;
    }
}
