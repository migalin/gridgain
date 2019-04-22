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

package org.apache.ignite.ml.tree.data;

import java.io.Serializable;
import java.util.Iterator;
import org.apache.ignite.ml.dataset.PartitionDataBuilder;
import org.apache.ignite.ml.dataset.UpstreamEntry;
import org.apache.ignite.ml.environment.LearningEnvironment;
import org.apache.ignite.ml.preprocessing.Preprocessor;
import org.apache.ignite.ml.structures.LabeledVector;

/**
 * A partition {@code data} builder that makes {@link DecisionTreeData}.
 *
 * @param <K> Type of a key in <tt>upstream</tt> data.
 * @param <V> Type of a value in <tt>upstream</tt> data.
 * @param <C> Type of a partition <tt>context</tt>.
 */
public class DecisionTreeDataBuilder<K, V, C extends Serializable>
    implements PartitionDataBuilder<K, V, C, DecisionTreeData> {
    /** */
    private static final long serialVersionUID = 3678784980215216039L;

    /** Extractor of features and labels from an {@code upstream} data. */
    private final Preprocessor<K, V> preprocessor;

    /** Build index. */
    private final boolean buildIdx;

    /**
     * Constructs a new instance of decision tree data builder.
     *
     * @param preprocessor Extractor of features and labels from an {@code upstream} data..
     * @param buildIdx Build index.
     */
    public DecisionTreeDataBuilder(Preprocessor<K, V> preprocessor, boolean buildIdx) {
        this.preprocessor = preprocessor;
        this.buildIdx = buildIdx;
    }

    /** {@inheritDoc} */
    @Override public DecisionTreeData build(
        LearningEnvironment envBuilder,
        Iterator<UpstreamEntry<K, V>> upstreamData,
        long upstreamDataSize,
        C ctx) {
        double[][] features = new double[Math.toIntExact(upstreamDataSize)][];
        double[] labels = new double[Math.toIntExact(upstreamDataSize)];

        int ptr = 0;
        while (upstreamData.hasNext()) {
            UpstreamEntry<K, V> entry = upstreamData.next();

            LabeledVector<Double> featsAndLbl = preprocessor.apply(entry.getKey(), entry.getValue());
            features[ptr] = featsAndLbl.features().asArray();

            labels[ptr] = featsAndLbl.label();

            ptr++;
        }

        return new DecisionTreeData(features, labels, buildIdx);
    }
}
