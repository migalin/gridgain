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

package org.apache.ignite.internal.processors.platform.cache;

import org.apache.ignite.configuration.NearCacheConfiguration;
import org.apache.ignite.internal.binary.BinaryRawWriterEx;
import org.apache.ignite.internal.processors.cache.GridCacheContext;
import org.apache.ignite.internal.processors.platform.PlatformAbstractPredicate;
import org.apache.ignite.internal.processors.platform.PlatformContext;
import org.apache.ignite.internal.processors.platform.memory.PlatformMemory;
import org.apache.ignite.internal.processors.platform.memory.PlatformOutputStream;

/**
 * Interop filter. Delegates apply to native platform.
 */
public class PlatformCacheEntryFilterImpl extends PlatformAbstractPredicate implements PlatformCacheEntryFilter {
    /** */
    private static final long serialVersionUID = 0L;

    /** */
    private transient boolean platfromNearEnabled;

    /**
     * {@link java.io.Externalizable} support.
     */
    public PlatformCacheEntryFilterImpl() {
        super();
    }

    /**
     * Constructor.
     *
     * @param pred .Net binary predicate.
     * @param ptr Pointer to predicate in the native platform.
     * @param ctx Kernal context.
     */
    public PlatformCacheEntryFilterImpl(Object pred, long ptr, PlatformContext ctx) {
        super(pred, ptr, ctx);

        assert pred != null;
    }

    /** {@inheritDoc} */
    @Override public boolean apply(Object k, Object v) {
        try (PlatformMemory mem = ctx.memory().allocate()) {
            PlatformOutputStream out = mem.output();

            BinaryRawWriterEx writer = ctx.writer(out);

            writer.writeLong(ptr);

            writer.writeObject(k);

            if (platfromNearEnabled) {
                // TODO: Put value in thread local inside platform context,
                // so it can be requested only when near entry does not exist for the key.
            } else {
                writer.writeObject(v);
            }

            out.synchronize();

            return ctx.gateway().cacheEntryFilterApply(mem.pointer()) != 0;
        }
    }

    /** {@inheritDoc} */
    @Override public void onClose() {
        if (ptr == 0)
            return;

        assert ctx != null;

        ctx.gateway().cacheEntryFilterDestroy(ptr);

        ptr = 0;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("rawtypes")
    @Override public void cacheContext(GridCacheContext cctx) {
        if (ptr != 0)
            return;

        ctx = cctx.kernalContext().platform().context();

        NearCacheConfiguration nearCfg = cctx.config().getNearConfiguration();
        platfromNearEnabled = nearCfg != null && nearCfg.getPlatformNearConfiguration() != null;

        try (PlatformMemory mem = ctx.memory().allocate()) {
            PlatformOutputStream out = mem.output();

            BinaryRawWriterEx writer = ctx.writer(out);

            writer.writeObject(pred);

            writer.writeInt(cctx.cacheId());

            out.synchronize();

            ptr = ctx.gateway().cacheEntryFilterCreate(mem.pointer());
        }
    }
}
