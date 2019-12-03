﻿/*
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

namespace Apache.Ignite.Core.Impl.Client.Cluster
{
    using System.Collections.Generic;
    using Apache.Ignite.Core.Binary;

    /// <summary>
    /// Projection builder that is used for remote nodes filtering.
    /// </summary>
    internal sealed class ClientClusterGroupProjection
    {
        /** */
        private const int Attribute = 1;

        /** */
        private const int ServerNodes = 2;

        /** Filter value mappings. */
        private readonly List<IProjectionItem> _filter;

        /// <summary>
        /// Constructor.
        /// </summary>
        /// <param name="filter">Filter.</param>
        private ClientClusterGroupProjection(List<IProjectionItem> filter)
        {
            _filter = filter;
        }

        /// <summary>
        /// Creates a new projection instance with specified attribute.
        /// </summary>
        /// <param name="name">Attribute name.</param>
        /// <param name="value">Attribute value.</param>
        /// <returns>Projection instance.</returns>
        public ClientClusterGroupProjection ForAttribute(string name, string value)
        {
            _filter.Add(new ForAttributeProjectionItem(name, value));
            return new ClientClusterGroupProjection(_filter);
        }

        /// <summary>
        /// Creates a new projection with server nodes only.
        /// </summary>
        /// <returns>Projection instance.</returns>
        public ClientClusterGroupProjection ForServerNodes(bool value)
        {
            _filter.Add(new ForServerNodesProjectionItem(value));
            return new ClientClusterGroupProjection(_filter);
        }

        /// <summary>
        /// Initializes an empty projection instance.
        /// </summary>
        public static ClientClusterGroupProjection Empty
        {
            get { return new ClientClusterGroupProjection(new List<IProjectionItem>()); }
        }

        /// <summary>
        /// Writes the projection to output buffer.
        /// </summary>
        /// <param name="writer">Binary writer.</param>
        public void Write(IBinaryRawWriter writer)
        {
            if (_filter.Count == 0)
            {
                writer.WriteBoolean(false);
                return;
            }

            writer.WriteBoolean(true);
            writer.WriteInt(_filter.Count);

            foreach (var item in _filter)
            {
                item.Write(writer);
            }
        }

        /// <summary>
        /// Projection item.
        /// </summary>
        private interface IProjectionItem
        {
            /// <summary>
            /// Writes the projection item to output fuffer.
            /// </summary>
            /// <param name="writer">Binary writer.</param>
            void Write(IBinaryRawWriter writer);
        }

        /// <summary>
        /// Represents attribute projection item.
        /// </summary>
        private sealed class ForAttributeProjectionItem : IProjectionItem
        {
            /** */
            private readonly string _key;
            /** */
            private readonly string _value;

            /// <summary>
            /// Constructor.
            /// </summary>
            /// <param name="key">Attribute key.</param>
            /// <param name="value">Attribute value.</param>
            public ForAttributeProjectionItem(string key, string value)
            {
                _key = key;
                _value = value;
            }

            /** <inheritDoc /> */
            public void Write(IBinaryRawWriter writer)
            {
                writer.WriteShort(Attribute);
                writer.WriteString(_key);
                writer.WriteString(_value);
            }
        }

        /// <summary>
        /// Represents server nodes only projection item.
        /// </summary>
        private sealed class ForServerNodesProjectionItem : IProjectionItem
        {
            /** */
            private readonly bool _value;

            /// <summary>
            /// Constructor.
            /// </summary>
            /// <param name="value"><c>True</c> for server nodes only.
            /// <c>False</c> for client nodes only.</param>
            public ForServerNodesProjectionItem(bool value)
            {
                _value = value;
            }

            /** <inheritDoc /> */
            public void Write(IBinaryRawWriter writer)
            {
                writer.WriteShort(ServerNodes);
                writer.WriteBoolean(_value);
            }
        }
    }
}