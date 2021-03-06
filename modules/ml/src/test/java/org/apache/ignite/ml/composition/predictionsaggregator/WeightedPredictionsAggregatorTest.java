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

package org.apache.ignite.ml.composition.predictionsaggregator;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/** */
public class WeightedPredictionsAggregatorTest {
    /** */
    @Test
    public void testApply1() {
        WeightedPredictionsAggregator aggregator = new WeightedPredictionsAggregator(new double[] {});
        assertEquals(0.0, aggregator.apply(new double[] {}), 0.001);
    }

    /** */
    @Test
    public void testApply2() {
        WeightedPredictionsAggregator aggregator = new WeightedPredictionsAggregator(new double[] {1.0, 0.5, 0.25});
        assertEquals(3.0, aggregator.apply(new double[] {1.0, 2.0, 4.0}), 0.001);
    }

    /** Non-equal weight vector and predictions case */
    @Test(expected = IllegalArgumentException.class)
    public void testIllegalArguments() {
        WeightedPredictionsAggregator aggregator = new WeightedPredictionsAggregator(new double[] {1.0, 0.5, 0.25});
        aggregator.apply(new double[] { });
    }

    /** */
    @Test
    public void testToString() {
        PredictionsAggregator aggr = (PredictionsAggregator)doubles -> null;
        assertTrue(aggr.toString().length() > 0);
        assertTrue(aggr.toString(true).length() > 0);
        assertTrue(aggr.toString(false).length() > 0);

        WeightedPredictionsAggregator aggregator = new WeightedPredictionsAggregator(new double[] {});
        assertTrue(aggregator.toString().length() > 0);
        assertTrue(aggregator.toString(true).length() > 0);
        assertTrue(aggregator.toString(false).length() > 0);
    }

}
