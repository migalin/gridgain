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

package org.apache.ignite.internal.processors.rest.handlers.cache;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.apache.ignite.internal.processors.rest.GridRestResponse;
import org.apache.ignite.internal.util.typedef.internal.S;
import org.apache.ignite.internal.util.typedef.internal.U;

/**
 * Adds affinity node ID to cache responses.
 */
public class GridCacheRestResponse extends GridRestResponse {
    /** */
    private static final long serialVersionUID = 0L;

    /** Affinity node ID. */
    private String affinityNodeId;

    /**
     * @return Affinity node ID.
     */
    public String getAffinityNodeId() {
        return affinityNodeId;
    }

    /**
     * @param affinityNodeId Affinity node ID.
     */
    public void setAffinityNodeId(String affinityNodeId) {
        this.affinityNodeId = affinityNodeId;
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return S.toString(GridCacheRestResponse.class, this, super.toString());
    }

    /** {@inheritDoc} */
    @Override public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);

        U.writeString(out, affinityNodeId);
    }

    /** {@inheritDoc} */
    @Override public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);

        affinityNodeId = U.readString(in);
    }
}