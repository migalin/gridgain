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

package org.apache.ignite.internal.pagemem.wal.record.delta;

import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.internal.pagemem.PageMemory;
import org.apache.ignite.internal.processors.cache.persistence.tree.io.AbstractDataPageIO;
import org.apache.ignite.internal.processors.cache.persistence.tree.io.PageIO;
import org.apache.ignite.internal.util.typedef.internal.S;

/**
 *
 */
public class DataPageRemoveRecord extends PageDeltaRecord {
    /** */
    private int itemId;

    /**
     * @param grpId Cache group ID.
     * @param pageId Page ID.
     * @param itemId Item ID.
     */
    public DataPageRemoveRecord(int grpId, long pageId, int itemId) {
        super(grpId, pageId);

        this.itemId = itemId;
    }

    /**
     * @return Item ID.
     */
    public int itemId() {
        return itemId;
    }

    /** {@inheritDoc} */
    @Override public void applyDelta(PageMemory pageMem, long pageAddr)
        throws IgniteCheckedException {
        AbstractDataPageIO io = PageIO.getPageIO(pageAddr);

        io.removeRow(pageAddr, itemId, pageMem.realPageSize(groupId()));
    }

    /** {@inheritDoc} */
    @Override public RecordType type() {
        return RecordType.DATA_PAGE_REMOVE_RECORD;
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return S.toString(DataPageRemoveRecord.class, this, "super", super.toString());
    }
}
