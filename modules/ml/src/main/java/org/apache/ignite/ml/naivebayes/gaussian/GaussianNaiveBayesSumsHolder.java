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
package org.apache.ignite.ml.naivebayes.gaussian;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.apache.ignite.ml.math.util.MapUtil;

/** Service class is used to calculate means and variances. */
class GaussianNaiveBayesSumsHolder implements Serializable, AutoCloseable {
    /** Serial version uid. */
    private static final long serialVersionUID = 1L;
    /** Sum of all values for all features for each label */
    Map<Double, double[]> featureSumsPerLbl = new HashMap<>();
    /** Sum of all squared values for all features for each label */
    Map<Double, double[]> featureSquaredSumsPerLbl = new HashMap<>();
    /** Rows count for each label */
    Map<Double, Integer> featureCountersPerLbl = new HashMap<>();

    /** Merge to current */
    GaussianNaiveBayesSumsHolder merge(GaussianNaiveBayesSumsHolder other) {
        featureSumsPerLbl = MapUtil.mergeMaps(featureSumsPerLbl, other.featureSumsPerLbl, this::sum, HashMap::new);
        featureSquaredSumsPerLbl = MapUtil.mergeMaps(featureSquaredSumsPerLbl, other.featureSquaredSumsPerLbl, this::sum, HashMap::new);
        featureCountersPerLbl = MapUtil.mergeMaps(featureCountersPerLbl, other.featureCountersPerLbl, (i1, i2) -> i1 + i2, HashMap::new);
        return this;
    }

    /** In-place operation. Sums {@code arr2} to {@code arr1} element to element. */
    private double[] sum(double[] arr1, double[] arr2) {
        for (int i = 0; i < arr1.length; i++)
            arr1[i] += arr2[i];

        return arr1;
    }

    /** */
    @Override public void close() {
        // Do nothing, GC will clean up.
    }
}
