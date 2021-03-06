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

package org.apache.ignite.internal.processors.platform.client;

import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.IgniteLogger;
import org.apache.ignite.internal.binary.BinaryWriterExImpl;
import org.apache.ignite.internal.processors.odbc.ClientListenerProtocolVersion;
import org.apache.ignite.internal.processors.odbc.ClientListenerRequest;
import org.apache.ignite.internal.processors.odbc.ClientListenerRequestHandler;
import org.apache.ignite.internal.processors.odbc.ClientListenerResponse;
import org.apache.ignite.internal.processors.platform.client.tx.ClientTxAwareRequest;
import org.apache.ignite.internal.processors.platform.client.tx.ClientTxContext;
import org.apache.ignite.plugin.security.SecurityException;

import static org.apache.ignite.internal.processors.platform.client.ClientConnectionContext.VER_1_4_0;

/**
 * Thin client request handler.
 */
public class ClientRequestHandler implements ClientListenerRequestHandler {
    /** Client context. */
    private final ClientConnectionContext ctx;

    /** Protocol version. */
    private final ClientListenerProtocolVersion ver;

    /** Logger. */
    private final IgniteLogger log;

    /**
     * Constructor.
     *
     * @param ctx Kernal context.
     * @param ver Protocol version.
     */
    ClientRequestHandler(ClientConnectionContext ctx, ClientListenerProtocolVersion ver) {
        assert ctx != null;

        this.ctx = ctx;
        this.ver = ver;
        log = ctx.kernalContext().log(getClass());
    }

    /** {@inheritDoc} */
    @Override public ClientListenerResponse handle(ClientListenerRequest req) {
        try {
            if (req instanceof ClientTxAwareRequest) {
                ClientTxAwareRequest req0 = (ClientTxAwareRequest)req;

                if (req0.isTransactional()) {
                    int txId = req0.txId();

                    ClientTxContext txCtx = ctx.txContext(txId);

                    if (txCtx != null) {
                        try {
                            txCtx.acquire(true);

                            return ((ClientRequest)req).process(ctx);
                        }
                        catch (IgniteCheckedException e) {
                            throw new IgniteClientException(ClientStatus.FAILED, e.getMessage(), e);
                        }
                        finally {
                            try {
                                txCtx.release(true);
                            }
                            catch (Exception e) {
                                log.warning("Failed to release client transaction context", e);
                            }
                        }
                    }
                }
            }

            return ((ClientRequest)req).process(ctx);
        }
        catch (SecurityException ex) {
            throw new IgniteClientException(
                ClientStatus.SECURITY_VIOLATION,
                ex.getMessage(),
                ex
            );
        }
    }

    /** {@inheritDoc} */
    @Override public ClientListenerResponse handleException(Exception e, ClientListenerRequest req) {
        assert req != null;
        assert e != null;

        int status = e instanceof IgniteClientException ?
            ((IgniteClientException)e).statusCode() : ClientStatus.FAILED;

        return new ClientResponse(req.requestId(), status, e.getMessage());
    }

    /** {@inheritDoc} */
    @Override public void writeHandshake(BinaryWriterExImpl writer) {
        writer.writeBoolean(true);

        if (ver.compareTo(VER_1_4_0) >= 0) {
            writer.writeUuid(ctx.kernalContext().localNodeId());
        }
    }

    /** {@inheritDoc} */
    @Override public boolean isCancellationCommand(int cmdId) {
        return false;
    }

    /** {@inheritDoc} */
    @Override public boolean isCancellationSupported() {
        return false;
    }

    /** {@inheritDoc} */
    @Override public void registerRequest(long reqId, int cmdType) {
        // No-op.
    }

    /** {@inheritDoc} */
    @Override public void unregisterRequest(long reqId) {
        // No-op.
    }
}
