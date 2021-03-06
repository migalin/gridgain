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

namespace Apache.Ignite.Core.Tests
{
    using System.Collections.Generic;
    using System.Linq;
    using Apache.Ignite.Core.Cache.Affinity;
    using Apache.Ignite.Core.Cache.Configuration;
    using Apache.Ignite.Core.Common;
    using Apache.Ignite.Core.Events;
    using NUnit.Framework;

    /// <summary>
    /// Tests <see cref="IgniteConfiguration.LocalEventListeners" />.
    /// </summary>
    public class EventsTestLocalListeners
    {
        /** Cache name. */
        private const string CacheName = "cache";

        /// <summary>
        /// Tests the rebalance events which occur during node startup.
        /// </summary>
        [Test]
        public void TestRebalanceEvents()
        {
            ICollection<int> cacheRebalanceStopStartEvts = new[]
            {
                EventType.CacheRebalanceStarted,
                EventType.CacheRebalanceStopped
            };
            
            var listener = new Listener<CacheRebalancingEvent>();

            using (IIgnite ignite0 = Ignition.Start(GetConfig(listener, cacheRebalanceStopStartEvts, "TestRebalanceEvents")))
            {
                var cache = ignite0.GetOrCreateCache<int, int>(CacheName);

                for (int i = 0; i < 2000; i++)
                    cache[i] = i;
                
                using (IIgnite ignite1 = Ignition.Start(GetConfig(listener, cacheRebalanceStopStartEvts)))
                {
                    AffinityTopologyVersion afterRebalanceTop =  new AffinityTopologyVersion(2, 1);
                    
                    Assert.True(ignite1.WaitTopology(afterRebalanceTop, CacheName), "Failed to wait topology " + afterRebalanceTop);
                    
                    var events = listener.GetEvents();

                    Assert.AreEqual(2, events.Count);

                    var rebalanceStart = events.First();

                    Assert.AreEqual(CacheName, rebalanceStart.CacheName);
                    Assert.AreEqual(EventType.CacheRebalanceStarted, rebalanceStart.Type);

                    var rebalanceStop = events.Last();

                    Assert.AreEqual(CacheName, rebalanceStop.CacheName);
                    Assert.AreEqual(EventType.CacheRebalanceStopped, rebalanceStop.Type);
                }
            }
        }

        /// <summary>
        /// Tests the unsubscription.
        /// </summary>
        [Test]
        public void TestUnsubscribe()
        {
            var listener = new Listener<CacheEvent>();

            using (var ignite = Ignition.Start(GetConfig(listener, EventType.CacheAll)))
            {
                Assert.AreEqual(0, listener.GetEvents().Count);

                var cache = ignite.GetCache<int, int>(CacheName);
                
                // Put causes 3 events: EntryCreated, ObjectPut, EntryDestroyed.
                cache.Put(1, 1);
                Assert.AreEqual(3, listener.GetEvents().Count);

                // Remove listener from one of the event types.
                var res = ignite.GetEvents().StopLocalListen(listener, EventType.CacheEntryCreated);
                Assert.IsTrue(res);

                cache.Put(2, 2);
                Assert.AreEqual(2, listener.GetEvents().Count);

                // Remove from all event types.
                res = ignite.GetEvents().StopLocalListen(listener);
                Assert.IsTrue(res);

                cache.Put(3, 3);
                Assert.AreEqual(0, listener.GetEvents().Count);

                // Remove when not subscribed.
                res = ignite.GetEvents().StopLocalListen(listener);
                Assert.IsFalse(res);
            }
        }

        /// <summary>
        /// Tests the configuration validation.
        /// </summary>
        [Test]
        public void TestConfigValidation()
        {
            var cfg = new IgniteConfiguration(TestUtils.GetTestConfiguration())
            {
                LocalEventListeners = new LocalEventListener<IEvent>[1]
            };

            // Null collection element.
            var ex = Assert.Throws<IgniteException>(() => Ignition.Start(cfg));
            Assert.AreEqual("LocalEventListeners can't contain nulls.", ex.Message);
            
            // Null listener property.
            cfg.LocalEventListeners = new[] {new LocalEventListener<IEvent>()};
            ex = Assert.Throws<IgniteException>(() => Ignition.Start(cfg));
            Assert.AreEqual("LocalEventListener.Listener can't be null.", ex.Message);

            // Null event types.
            cfg.LocalEventListeners = new[] {new LocalEventListener<IEvent> {Listener = new Listener<IEvent>()}};
            ex = Assert.Throws<IgniteException>(() => Ignition.Start(cfg));
            Assert.AreEqual("LocalEventListener.EventTypes can't be null or empty.", ex.Message);

            // Empty event types.
            cfg.LocalEventListeners = new[]
                {new LocalEventListener<IEvent> {Listener = new Listener<IEvent>(), EventTypes = new int[0]}};
            ex = Assert.Throws<IgniteException>(() => Ignition.Start(cfg));
            Assert.AreEqual("LocalEventListener.EventTypes can't be null or empty.", ex.Message);
        }

        /// <summary>
        /// Gets the configuration.
        /// </summary>
        private static IgniteConfiguration GetConfig<T>(IEventListener<T> listener, ICollection<int> eventTypes,
            string instanceName = null) 
            where T : IEvent
        {
            return new IgniteConfiguration(TestUtils.GetTestConfiguration())
            {
                IgniteInstanceName = instanceName,
                LocalEventListeners = new[]
                {
                    new LocalEventListener<T>
                    {
                        Listener = listener,
                        EventTypes = eventTypes
                    }
                },
                IncludedEventTypes = eventTypes,
                CacheConfiguration = new[] { new CacheConfiguration(CacheName) }
            };
        }

        /// <summary>
        /// Listener.
        /// </summary>
        private class Listener<T> : IEventListener<T> where T : IEvent
        {
            /** Listen action. */
            private readonly List<T> _events = new List<T>();

            /// <summary>
            /// Gets the events.
            /// </summary>
            public ICollection<T> GetEvents()
            {
                lock (_events)
                {
                    var res = _events.ToArray();

                    _events.Clear();

                    return res;
                }
            }

            /** <inheritdoc /> */
            public bool Invoke(T evt)
            {
                lock (_events)
                {
                    _events.Add(evt);
                }

                return true;
            }
        }
    }
}