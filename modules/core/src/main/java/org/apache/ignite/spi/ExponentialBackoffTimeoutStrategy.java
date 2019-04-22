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

package org.apache.ignite.spi;

import org.apache.ignite.internal.util.typedef.internal.S;
import org.apache.ignite.internal.util.typedef.internal.U;

/**
 * Strategy which incorporates retriable network operation, handling of totalTimeout logic.
 * It increases startTimeout based on exponential backoff algorithm.
 *
 * If failure detection is enabled it relies on totalTimeout
 * otherwise implements exponential backoff totalTimeout logic based on startTimeout, maxTimeout and retryCnt.
 */
public class ExponentialBackoffTimeoutStrategy implements TimeoutStrategy {
    /** Default backoff coefficient to calculate next timeout based on backoff strategy. */
    private static final double DLFT_BACKOFF_COEFF = 2.0;

    /** Max timeout of the next try, ms. */
    private final long maxTimeout;

    /** Total timeout, ms. */
    private final long totalTimeout;

    /** Timestamp of operation start to check totalTimeout. */
    private final long start;

    /** Current calculated timeout, ms. */
    private long currTimeout;

    /**
     * Compute expected max backoff timeout based on initTimeout, maxTimeout and reconCnt and backoff coefficient.
     *
     * @param initTimeout Initial timeout.
     * @param maxTimeout Max Timeout per retry.
     * @param reconCnt Reconnection count.
     * @return Calculated total backoff timeout.
     */
    public static long totalBackoffTimeout(
            long initTimeout,
            long maxTimeout,
            long reconCnt
    ) {
        long totalBackoffTimeout = initTimeout;

        for (int i = 1; i < reconCnt && totalBackoffTimeout < maxTimeout; i++)
            totalBackoffTimeout += backoffTimeout(totalBackoffTimeout, maxTimeout);

        return totalBackoffTimeout;
    }

    /**
     *
     * @param timeout Timeout.
     * @param maxTimeout Maximum startTimeout for backoff function.
     * @return Next exponential backoff timeout.
     */
    public static long backoffTimeout(long timeout, long maxTimeout) {
        return (long) Math.min(timeout * DLFT_BACKOFF_COEFF, maxTimeout);
    }

    /**
     *
     * @param totalTimeout Total startTimeout.
     * @param startTimeout Initial connection timeout.
     * @param maxTimeout Max connection Timeout.
     *
     */
    public ExponentialBackoffTimeoutStrategy(
        long totalTimeout,
        long startTimeout,
        long maxTimeout
    ) {
        this.totalTimeout = totalTimeout;

        this.maxTimeout = maxTimeout;

        currTimeout = startTimeout;

        start = U.currentTimeMillis();
    }

    /** {@inheritDoc} */
    @Override public long nextTimeout(long timeout) throws IgniteSpiOperationTimeoutException {
        long remainingTime = remainingTime(U.currentTimeMillis());

        if (remainingTime <= 0)
            throw new IgniteSpiOperationTimeoutException("Operation timed out [timeoutStrategy= " +this +"]");

        /*
            If timeout is zero that means we need return current verified timeout and calculate next timeout.
            In case of non zero we just reverify previously calculated value not to breach totalTimeout.
         */
        if (timeout == 0) {
            long prevTimeout = currTimeout;

            currTimeout = backoffTimeout(currTimeout, maxTimeout);

            return Math.min(prevTimeout, remainingTime);
        } else
            return Math.min(timeout, remainingTime);
    }

    /**
     * Returns remaining time for current totalTimeout chunk.
     *
     * @param curTs Current timestamp.
     * @return Time to wait in millis.
     */
    public long remainingTime(long curTs) {
        return totalTimeout - (curTs - start);
    }

    /** {@inheritDoc} */
    @Override public boolean checkTimeout(long timeInFut) {
        return remainingTime(U.currentTimeMillis() + timeInFut) <= 0;
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return S.toString(ExponentialBackoffTimeoutStrategy.class, this);
    }
}
