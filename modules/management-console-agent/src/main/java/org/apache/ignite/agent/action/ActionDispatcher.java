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

package org.apache.ignite.agent.action;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.ignite.IgniteException;
import org.apache.ignite.agent.dto.action.Request;
import org.apache.ignite.internal.GridKernalContext;
import org.apache.ignite.lang.IgniteFuture;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.apache.ignite.agent.action.annotation.ActionControllerAnnotationProcessor.getActions;
import static org.apache.ignite.agent.utils.AgentUtils.completeFutureWithException;
import static org.apache.ignite.agent.utils.AgentUtils.completeIgniteFuture;

/**
 * Action dispatcher.
 */
public class ActionDispatcher implements AutoCloseable {
    /** Context. */
    private final GridKernalContext ctx;

    /** Controllers. */
    private final Map<Class, Object> controllers = new ConcurrentHashMap<>();

    /** Thread pool. */
    private final ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    /**
     * @param ctx Context.
     */
    public ActionDispatcher(GridKernalContext ctx) {
        this.ctx = ctx;
    }

    /**
     * Find the controller with appropriate method and invoke it.
     *
     * @param req Request.
     * @return Completable future with action result.
     */
    public CompletableFuture<CompletableFuture> dispatch(Request req) {
        String act = req.getAction();

        ActionMethod mtd = getActions().get(act);

        if (mtd == null)
            throw new IgniteException("Failed to find action method");

        return CompletableFuture.supplyAsync(() -> handleRequest(mtd, req), pool);
    }

    /**
     *  Find appropriate action for request and invoke it.
     *
     * @param mtd Method.
     * @param req Request.
     */
    private CompletableFuture handleRequest(ActionMethod mtd, Request req) {
        try {
            Class<?> ctrlCls = mtd.getControllerClass();
            if (!controllers.containsKey(ctrlCls))
                controllers.put(ctrlCls, ctrlCls.getConstructor(GridKernalContext.class).newInstance(ctx));

            return invoke(mtd.getMethod(), controllers.get(ctrlCls), req.getArgument());
        }
        catch (InvocationTargetException e) {
            return completeFutureWithException(e.getTargetException());
        }
        catch (Exception e) {
            return completeFutureWithException(e);
        }
    }

    /**
     * Invoke action method.
     *
     * @param mtd Method.
     * @param controller Controller.
     * @param arg Argument.
     */
    private CompletableFuture invoke(Method mtd, Object controller, Object arg) throws Exception {
        Object res = arg == null ? mtd.invoke(controller) : mtd.invoke(controller, arg);

        if (res instanceof Void)
            return completedFuture(null);

        if (res instanceof CompletableFuture)
            return (CompletableFuture) res;

        if (res instanceof IgniteFuture)
           return completeIgniteFuture((IgniteFuture<?>) res);

        return completedFuture(res);
    }

    /** {@inheritDoc} */
    @Override public void close() {
        pool.shutdown();
    }
}
