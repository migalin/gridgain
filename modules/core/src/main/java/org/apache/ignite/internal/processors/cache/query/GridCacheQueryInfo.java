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

package org.apache.ignite.internal.processors.cache.query;

import java.util.UUID;
import org.apache.ignite.internal.util.typedef.internal.S;
import org.apache.ignite.lang.IgniteClosure;
import org.apache.ignite.lang.IgniteReducer;
import org.jetbrains.annotations.Nullable;

/**
 * Query information (local or distributed).
 */
class GridCacheQueryInfo {
    /** */
    private boolean loc;

    /** */
    private IgniteClosure<?, ?> trans;

    /** */
    private IgniteReducer<Object, Object> rdc;

    /** */
    private GridCacheQueryAdapter<?> qry;

    /** */
    private GridCacheLocalQueryFuture<?, ?, ?> locFut;

    /** */
    private UUID sndId;

    /** */
    private long reqId;

    /** */
    private boolean incMeta;

    /** */
    private boolean all;

    /** */
    private Object[] args;

    /** */
    private long reqSendTimestamp;

    /** */
    private long reqReceiveTimestamp;

    /**
     * @param loc {@code true} if local query.
     * @param trans Transforming closure.
     * @param rdc Reducer.
     * @param qry Query base.
     * @param locFut Query future in case of local query.
     * @param sndId Sender node id.
     * @param reqId Request id in case of distributed query.
     * @param incMeta Include meta data or not.
     * @param all Whether to load all pages.
     * @param reqSendTimestamp Request send timestamp.
     * @param reqReceiveTimestamp Request receive timestamp.
     * @param args Arguments.
     */
    GridCacheQueryInfo(
        boolean loc,
        IgniteClosure<?, ?> trans,
        IgniteReducer<Object, Object> rdc,
        GridCacheQueryAdapter<?> qry,
        GridCacheLocalQueryFuture<?, ?, ?> locFut,
        UUID sndId,
        long reqId,
        boolean incMeta,
        boolean all,
        long reqSendTimestamp,
        long reqReceiveTimestamp,
        Object[] args
    ) {
        this.loc = loc;
        this.trans = trans;
        this.rdc = rdc;
        this.qry = qry;
        this.locFut = locFut;
        this.sndId = sndId;
        this.reqId = reqId;
        this.incMeta = incMeta;
        this.all = all;
        this.reqSendTimestamp = reqSendTimestamp;
        this.reqReceiveTimestamp = reqReceiveTimestamp;
        this.args = args;
    }

    /**
     * @return Local or not.
     */
    boolean local() {
        return loc;
    }

    /**
     * @return Id of sender node.
     */
    @Nullable UUID senderId() {
        return sndId;
    }

    /**
     * @return Query.
     */
    GridCacheQueryAdapter<?> query() {
        return qry;
    }

    /**
     * @return Transformer.
     */
    IgniteClosure<?, ?> transformer() {
        return trans;
    }

    /**
     * @return Reducer.
     */
    IgniteReducer<?, Object> reducer() {
        return rdc;
    }

    /**
     * @return Query future in case of local query.
     */
    @Nullable GridCacheLocalQueryFuture<?, ?, ?> localQueryFuture() {
        return locFut;
    }

    /**
     * @return Request id in case of distributed query.
     */
    long requestId() {
        return reqId;
    }

    /**
     * @return Include meta data or not.
     */
    boolean includeMetaData() {
        return incMeta;
    }

    /**
     * @return Whether to load all pages.
     */
    boolean allPages() {
        return all;
    }

    /**
     * @return Request send timestamp.
     */
    public long reqSendTimestamp() {
        return reqSendTimestamp;
    }

    /**
     * @return Request receive timestamp.
     */
    public long reqReceiveTimestamp() {
        return reqReceiveTimestamp;
    }

    /**
     * @return Arguments.
     */
    Object[] arguments() {
        return args;
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return S.toString(GridCacheQueryInfo.class, this);
    }
}
