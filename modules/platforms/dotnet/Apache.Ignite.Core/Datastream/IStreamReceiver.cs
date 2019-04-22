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

namespace Apache.Ignite.Core.Datastream
{
    using System.Collections.Generic;
    using Apache.Ignite.Core.Cache;

    /// <summary>
    /// Updates cache with batch of entries. 
    /// Usually it is enough to configure <see cref="IDataStreamer{K,V}.AllowOverwrite" /> property and appropriate 
    /// internal cache receiver will be chosen automatically. But in some cases custom implementation may help 
    /// to achieve better performance.
    /// </summary>
    public interface IStreamReceiver<TK, TV>
    {
        /// <summary>
        /// Updates cache with batch of entries.
        /// </summary>
        /// <param name="cache">Cache.</param>
        /// <param name="entries">Entries.</param>
        void Receive(ICache<TK, TV> cache, ICollection<ICacheEntry<TK, TV>> entries);
    }
}