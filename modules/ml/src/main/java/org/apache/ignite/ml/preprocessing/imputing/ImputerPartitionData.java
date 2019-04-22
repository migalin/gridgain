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

package org.apache.ignite.ml.preprocessing.imputing;

import java.util.Map;

/**
 * Partition data used in imputing preprocessor.
 *
 * @see ImputerTrainer
 * @see ImputerPreprocessor
 */
public class ImputerPartitionData implements AutoCloseable {
    /** Sum of values in partition. */
    private double[] sums;

    /** Count of values in partition. */
    private int[] counts;

    /** Most frequent values. */
    private Map<Double, Integer>[] valuesByFreq;

    /**
     * Constructs a new instance of imputing partition data.
     *
     */
    public ImputerPartitionData() {
    }

    /**
     * Gets the array of sums of values in partition for each feature in the dataset.
     *
     * @return The sums.
     */
    public double[] sums() {
        return sums;
    }

    /**
     * Sets the array of sums of values in partition for each feature in the dataset.
     *
     * @param sums The given value.
     *
     * @return The partition data.
     */
    public ImputerPartitionData withSums(double[] sums) {
        this.sums = sums;
        return this;
    }

    /**
     * Sets the array of amounts of values in partition for each feature in the dataset.
     *
     * @param counts The given value.
     *
     * @return The partition data.
     */
    public ImputerPartitionData withCounts(int[] counts) {
        this.counts = counts;
        return this;
    }

    /**
     * Gets the array of amounts of values in partition for each feature in the dataset.
     *
     * @return The counts.
     */
    public int[] counts() {
        return counts;
    }

    /**
     * Gets the array of maps of frequencies by value in partition for each feature in the dataset.
     *
     * @return The frequencies.
     */
    public Map<Double, Integer>[] valuesByFrequency() {
        return valuesByFreq;
    }

    /**
     * Sets the array of maps of frequencies by value in partition for each feature in the dataset.
     *
     * @param valuesByFreq The given value.
     * @return The partition data.
     */
    public ImputerPartitionData withValuesByFrequency(Map<Double, Integer>[] valuesByFreq) {
        this.valuesByFreq = valuesByFreq;
        return this;
    }

    /** */
    @Override public void close() {
        // Do nothing, GC will clean up.
    }
}
