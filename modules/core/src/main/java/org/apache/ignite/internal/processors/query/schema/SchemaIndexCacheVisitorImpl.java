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

package org.apache.ignite.internal.processors.query.schema;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.ignite.internal.processors.cache.GridCacheContext;
import org.apache.ignite.internal.processors.cache.distributed.dht.topology.GridDhtLocalPartition;
import org.apache.ignite.internal.processors.cache.distributed.near.GridNearCacheAdapter;
import org.apache.ignite.internal.util.future.GridCompoundFuture;
import org.apache.ignite.internal.util.typedef.internal.S;
import org.apache.ignite.internal.util.worker.GridWorker;
import org.apache.ignite.internal.util.worker.GridWorkerFuture;

import static java.util.Objects.nonNull;

/**
 * Visitor who create/rebuild indexes in parallel by partition for a given cache.
 */
public class SchemaIndexCacheVisitorImpl implements SchemaIndexCacheVisitor {
    /** Cache context. */
    private final GridCacheContext cctx;

    /** Cancellation token. */
    private final SchemaIndexOperationCancellationToken cancel;

    /** Thread pool for create/rebuild index. */
    private final ExecutorService execSvc;

    /** Future for create/rebuild index. */
    protected final GridCompoundFuture<Void, Void> compoundFut;

    /**
     * Constructor.
     *
     * @param cctx Cache context.
     * @param cancel Cancellation token.
     * @param execSvc Thread pool for create/rebuild index.
     * @param compoundFut Future for create/rebuild index.
     */
    public SchemaIndexCacheVisitorImpl(
        GridCacheContext cctx,
        SchemaIndexOperationCancellationToken cancel,
        ExecutorService execSvc,
        GridCompoundFuture<Void, Void> compoundFut
    ) {
        assert nonNull(cctx);
        assert nonNull(execSvc);
        assert nonNull(compoundFut);

        if (cctx.isNear())
            cctx = ((GridNearCacheAdapter)cctx.cache()).dht().context();

        this.cctx = cctx;
        this.execSvc = execSvc;
        this.compoundFut = compoundFut;

        this.cancel = cancel;
    }

    /** {@inheritDoc} */
    @Override public void visit(SchemaIndexCacheVisitorClosure clo) {
        assert nonNull(clo);

        List<GridDhtLocalPartition> locParts = cctx.topology().localPartitions();

        cctx.group().metrics().setIndexBuildCountPartitionsLeft(locParts.size());

        if (locParts.isEmpty()) {
            compoundFut.onDone();

            return;
        }

        AtomicBoolean stop = new AtomicBoolean();

        for (GridDhtLocalPartition locPart : locParts) {
            GridWorkerFuture<Void> workerFut = new GridWorkerFuture<>();

            GridWorker worker = new SchemaIndexCachePartitionWorker(cctx, locPart, stop, cancel, clo, workerFut);

            workerFut.setWorker(worker);
            compoundFut.add(workerFut);

            execSvc.execute(worker);
        }
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return S.toString(SchemaIndexCacheVisitorImpl.class, this);
    }
}
