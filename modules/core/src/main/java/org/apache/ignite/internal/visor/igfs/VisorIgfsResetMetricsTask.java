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

package org.apache.ignite.internal.visor.igfs;

import org.apache.ignite.IgniteException;
import org.apache.ignite.internal.processors.task.GridInternal;
import org.apache.ignite.internal.processors.task.GridVisorManagementTask;
import org.apache.ignite.internal.util.typedef.internal.S;
import org.apache.ignite.internal.visor.VisorJob;
import org.apache.ignite.internal.visor.VisorOneNodeTask;

/**
 * Resets IGFS metrics.
 */
@GridInternal
@GridVisorManagementTask
public class VisorIgfsResetMetricsTask extends VisorOneNodeTask<VisorIgfsResetMetricsTaskArg, Void> {
    /** */
    private static final long serialVersionUID = 0L;

    /** {@inheritDoc} */
    @Override protected VisorIgfsResetMetricsJob job(VisorIgfsResetMetricsTaskArg arg) {
        return new VisorIgfsResetMetricsJob(arg, debug);
    }

    /**
     * Job that reset IGFS metrics.
     */
    private static class VisorIgfsResetMetricsJob extends VisorJob<VisorIgfsResetMetricsTaskArg, Void> {
        /** */
        private static final long serialVersionUID = 0L;

        /**
         * @param arg IGFS names.
         * @param debug Debug flag.
         */
        private VisorIgfsResetMetricsJob(VisorIgfsResetMetricsTaskArg arg, boolean debug) {
            super(arg, debug);
        }

        /** {@inheritDoc} */
        @Override protected Void run(VisorIgfsResetMetricsTaskArg arg) {
            for (String igfsName : arg.getIgfsNames())
                try {
                    ignite.fileSystem(igfsName).resetMetrics();
                }
                catch (IllegalArgumentException iae) {
                    throw new IgniteException("Failed to reset metrics for IGFS: " + igfsName, iae);
                }

            return null;
        }

        /** {@inheritDoc} */
        @Override public String toString() {
            return S.toString(VisorIgfsResetMetricsJob.class, this);
        }
    }
}