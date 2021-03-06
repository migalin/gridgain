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

package org.apache.ignite.visor.plugin;

import ro.fortsoft.pf4j.Plugin;
import ro.fortsoft.pf4j.PluginException;
import ro.fortsoft.pf4j.PluginWrapper;

/**
 * Base class for Visor plugins.
 */
public abstract class VisorPlugin extends Plugin {
    /**
     * Constructor to be used by plugin manager for plugin instantiation.
     * Your plugins have to provide constructor with this exact signature to
     * be successfully loaded by manager.
     *
     * @param wrapper A wrapper over plugin instance.
     */
    protected VisorPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    /**
     * @return Plugin name.
     */
    public abstract String name();

    /** {@inheritDoc} */
    @Override public void start() throws PluginException {
        log.info("Plugin Started: " + name());
    }

    /** {@inheritDoc} */
    @Override public void stop() throws PluginException {
        log.info("Plugin stopped: " + name());
    }
}