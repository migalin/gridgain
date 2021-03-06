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

package org.apache.ignite.events;

import java.io.File;
import org.apache.ignite.cluster.ClusterNode;
import org.jetbrains.annotations.NotNull;

/**
 * Event indicates the completion of WAL segment file transition to archive.
 */
public class WalSegmentArchivedEvent extends EventAdapter {
    /** */
    private static final long serialVersionUID = 0L;

    /** Absolute WAL segment file index. */
    private long absWalSegmentIdx;

    /** Destination archive file. This file is completed and closed archive segment */
    private final File archiveFile;

    /**
     * Creates WAL segment event
     *
     * @param node Node.
     * @param absWalSegmentIdx Absolute wal segment index.
     * @param archiveFile Archive file.
     */
    public WalSegmentArchivedEvent(
        @NotNull final ClusterNode node,
        final long absWalSegmentIdx,
        final File archiveFile) {
        this(node, absWalSegmentIdx, archiveFile, EventType.EVT_WAL_SEGMENT_ARCHIVED);
    }

    /** */
    protected WalSegmentArchivedEvent(
        @NotNull final ClusterNode node,
        final long absWalSegmentIdx,
        final File archiveFile,
        int evtType) {
        super(node, "", evtType);
        this.absWalSegmentIdx = absWalSegmentIdx;
        this.archiveFile = archiveFile;
    }

    /** @return {@link #archiveFile} */
    public File getArchiveFile() {
        return archiveFile;
    }

    /** @return {@link #absWalSegmentIdx} */
    public long getAbsWalSegmentIdx() {
        return absWalSegmentIdx;
    }
}
