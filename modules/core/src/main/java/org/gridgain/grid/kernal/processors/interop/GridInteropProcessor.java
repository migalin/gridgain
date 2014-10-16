/* @java.file.header */

/*  _________        _____ __________________        _____
 *  __  ____/___________(_)______  /__  ____/______ ____(_)_______
 *  _  / __  __  ___/__  / _  __  / _  / __  _  __ `/__  / __  __ \
 *  / /_/ /  _  /    _  /  / /_/ /  / /_/ /  / /_/ / _  /  _  / / /
 *  \____/   /_/     /_/   \_,__/   \____/   \__,_/  /_/   /_/ /_/
 */

package org.gridgain.grid.kernal.processors.interop;

import org.gridgain.grid.*;
import org.gridgain.grid.kernal.processors.*;
import org.jetbrains.annotations.*;

/**
 * Interop processor.
 */
public interface GridInteropProcessor extends GridProcessor {
    /**
     * @return Grid name.
     */
    public String gridName();

    /**
     * Gets native wrapper for default Grid projection.
     *
     * @return Native compute wrapper.
     * @throws GridException If failed.
     */
    public GridInteropTarget projection() throws GridException;

    /**
     * Gets native wrapper for cache with the given name.
     *
     * @param name Cache name ({@code null} for default cache).
     * @return Native cache wrapper.
     * @throws GridException If failed.
     */
    public GridInteropTarget cache(@Nullable String name) throws GridException;

    /**
     * Stops grid.
     *
     * @param cancel Cancel flag.
     */
    public void close(boolean cancel);

    /**
     * Write /Net-specific configuration to the stream.
     *
     * @param stream Stream pointer.
     * @param arr Data pointer.
     * @param cap Capacity.
     * @throws GridException If failed.
     */
    public void dotNetConfiguration(long stream, long arr, int cap) throws GridException;
}
