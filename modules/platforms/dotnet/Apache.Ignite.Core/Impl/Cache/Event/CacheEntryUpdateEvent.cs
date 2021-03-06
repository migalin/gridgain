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

namespace Apache.Ignite.Core.Impl.Cache.Event
{
    using Apache.Ignite.Core.Cache.Event;

    /// <summary>
    /// Cache entry update event.
    /// </summary>
    internal class CacheEntryUpdateEvent<TK, TV> : ICacheEntryEvent<TK, TV>
    {
        /** Key.*/
        private readonly TK _key;

        /** Value.*/
        private readonly TV _val;

        /** Old value.*/
        private readonly TV _oldVal;

        /// <summary>
        /// Constructor.
        /// </summary>
        /// <param name="key">Key.</param>
        /// <param name="oldVal">Old value.</param>
        /// <param name="val">Value.</param>
        public CacheEntryUpdateEvent(TK key, TV oldVal, TV val)
        {
            _key = key;
            _oldVal = oldVal;
            _val = val;
        }

        /** <inheritdoc /> */
        public TK Key
        {
            get { return _key; }
        }

        /** <inheritdoc /> */
        public TV Value
        {
            get { return _val; }
        }

        /** <inheritdoc /> */
        public TV OldValue
        {
            get { return _oldVal; }
        }

        /** <inheritdoc /> */
        public bool HasValue
        {
            get { return true; }
        }

        /** <inheritdoc /> */
        public bool HasOldValue
        {
            get { return true; }
        }

        /** <inheritdoc /> */
        public CacheEntryEventType EventType
        {
            get { return CacheEntryEventType.Updated; }
        }
    }
}
