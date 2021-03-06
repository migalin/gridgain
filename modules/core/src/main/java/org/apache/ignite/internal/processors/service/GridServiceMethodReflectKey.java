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

package org.apache.ignite.internal.processors.service;

import java.util.Arrays;

/**
 * Method reflection key.
 */
class GridServiceMethodReflectKey {
    /** Method name. */
    private final String mtdName;

    /** Argument types. */
    private final Class<?>[] argTypes;

    /** Hash code. */
    private int hash;

    /**
     * @param mtdName Method name.
     * @param argTypes Argument types.
     */
    GridServiceMethodReflectKey(String mtdName, Class<?>[] argTypes) {
        assert mtdName != null;

        this.mtdName = mtdName;
        this.argTypes = argTypes;
    }

    /**
     * @return Method name.
     */
    String methodName() {
        return mtdName;
    }

    /**
     * @return Arg types.
     */
    Class<?>[] argTypes() {
        return argTypes;
    }

    /** {@inheritDoc} */
    @Override public boolean equals(Object o) {
        if (this == o)
            return true;

        GridServiceMethodReflectKey key = (GridServiceMethodReflectKey)o;

        return mtdName.equals(key.mtdName) && Arrays.equals(argTypes, key.argTypes);
    }

    /** {@inheritDoc} */
    @Override public int hashCode() {
        if (hash != 0)
            return hash;

        return hash = 31 * mtdName.hashCode() + Arrays.hashCode(argTypes);
    }
}