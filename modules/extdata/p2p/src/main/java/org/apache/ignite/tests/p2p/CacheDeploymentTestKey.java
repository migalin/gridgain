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

package org.apache.ignite.tests.p2p;

/**
 * Test key for cache deployment tests.
 */
public class CacheDeploymentTestKey {
    /** */
    private String key;

    /**
     * Empty constructor.
     */
    public CacheDeploymentTestKey() {
        // No-op.
    }

    /**
     * @param key Key.
     */
    public CacheDeploymentTestKey(String key) {
        this.key = key;
    }

    /**
     * @param key Key value.
     */
    public void key(String key) {
        this.key = key;
    }

    /** {@inheritDoc} */
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CacheDeploymentTestKey that = (CacheDeploymentTestKey)o;

        return !(key != null ? !key.equals(that.key) : that.key != null);

    }

    /** {@inheritDoc} */
    @Override public int hashCode() {
        return key != null ? key.hashCode() : 0;
    }
}