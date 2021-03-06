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

package org.apache.ignite.ml.math.functions;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;

/**
 * Serializable function.
 *
 * @see java.util.function.Function
 */
public interface IgniteFunction<T, R> extends Function<T, R>, Serializable {
    /**
     * {@link IgniteFunction} returning specified constant.
     *
     * @param r Constant to return.
     * @param <T> Type of input.
     * @param <R> Type of output.
     * @return {@link IgniteFunction} returning specified constant.
     */
    // TODO: IGNITE-10653 Maybe we should add toString description to identity and constant.
    public static <T, R> IgniteFunction<T, R> constant(R r) {
        return (IgniteFunction<T, R>)t -> r;
    }

    /**
     * Compose this function and given function.
     *
     * @param after Function to compose with.
     * @param <V> Type of value which result of {@code after} extends.
     * @return Functions composition.
     */
    default <V> IgniteFunction<T, V> andThen(IgniteFunction<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (T t) -> after.apply(apply(t));
    }

    /**
     * Identity function.
     *
     * @param <T> Type of input and output.
     * @return Identity function.
     */
    public static <T> IgniteFunction<T, T> identity() {
        return (IgniteFunction<T, T>)t -> t;
    }
}
