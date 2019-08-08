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

package org.apache.ignite.testsuites;

//import org.apache.ignite.ClassPathContentLoggingTest;
//import org.apache.ignite.GridSuppressedExceptionSelfTest;

import org.apache.ignite.OpenCloverOptimizer;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

//import org.apache.ignite.failure.FailureHandlerTriggeredTest;
//import org.apache.ignite.failure.OomFailureHandlerTest;
//import org.apache.ignite.failure.StopNodeFailureHandlerTest;
//import org.apache.ignite.failure.StopNodeOrHaltFailureHandlerTest;
//import org.apache.ignite.internal.*;
//import org.apache.ignite.internal.managers.IgniteDiagnosticMessagesMultipleConnectionsTest;
//import org.apache.ignite.internal.managers.IgniteDiagnosticMessagesTest;
//import org.apache.ignite.internal.managers.discovery.IncompleteDeserializationExceptionTest;
//import org.apache.ignite.internal.pagemem.wal.record.WALRecordTest;
//import org.apache.ignite.internal.processors.DeadLockOnNodeLeftExchangeTest;
//import org.apache.ignite.internal.processors.affinity.*;
//import org.apache.ignite.internal.processors.cache.*;
//import org.apache.ignite.internal.processors.cache.distributed.IgniteRejectConnectOnNodeStopTest;
//import org.apache.ignite.internal.processors.cache.query.continuous.DiscoveryDataDeserializationFailureHanderTest;
//import org.apache.ignite.internal.processors.cache.transactions.AtomicOperationsInTxTest;
//import org.apache.ignite.internal.processors.cache.transactions.TransactionIntegrityWithSystemWorkerDeathTest;
//import org.apache.ignite.internal.processors.closure.GridClosureProcessorRemoteTest;
//import org.apache.ignite.internal.processors.closure.GridClosureProcessorSelfTest;
//import org.apache.ignite.internal.processors.closure.GridClosureSerializationTest;
//import org.apache.ignite.internal.processors.cluster.BaselineAutoAdjustMXBeanTest;
//import org.apache.ignite.internal.processors.configuration.distributed.DistributedConfigurationInMemoryTest;
//import org.apache.ignite.internal.processors.continuous.GridEventConsumeSelfTest;
//import org.apache.ignite.internal.processors.continuous.GridMessageListenSelfTest;
//import org.apache.ignite.internal.processors.database.*;
//import org.apache.ignite.internal.processors.metastorage.DistributedMetaStorageTest;
//import org.apache.ignite.internal.processors.metastorage.persistence.DistributedMetaStorageHistoryCacheTest;
//import org.apache.ignite.internal.processors.odbc.OdbcConfigurationValidationSelfTest;
//import org.apache.ignite.internal.processors.odbc.OdbcEscapeSequenceSelfTest;
//import org.apache.ignite.internal.processors.odbc.SqlListenerUtilsTest;
//import org.apache.ignite.internal.product.GridProductVersionSelfTest;
//import org.apache.ignite.internal.product.IndexingFeatureIsNotAvailableTest;
//import org.apache.ignite.internal.util.BitSetIntSetTest;
//import org.apache.ignite.internal.util.GridCleanerTest;
//import org.apache.ignite.internal.util.nio.IgniteExceptionInNioWorkerSelfTest;
//import org.apache.ignite.marshaller.DynamicProxySerializationMultiJvmSelfTest;
//import org.apache.ignite.marshaller.MarshallerContextSelfTest;
//import org.apache.ignite.messaging.GridMessagingNoPeerClassLoadingSelfTest;
//import org.apache.ignite.messaging.GridMessagingSelfTest;
//import org.apache.ignite.messaging.IgniteMessagingSendAsyncTest;
//import org.apache.ignite.messaging.IgniteMessagingWithClientTest;
//import org.apache.ignite.plugin.PluginNodeValidationTest;
//import org.apache.ignite.plugin.security.SecurityPermissionSetBuilderTest;
//import org.apache.ignite.spi.GridSpiLocalHostInjectionTest;
//import org.apache.ignite.startup.properties.NotStringSystemPropertyTest;
//import org.apache.ignite.testframework.MessageOrderLogListenerTest;
//import org.apache.ignite.testframework.test.*;
//import org.apache.ignite.util.AttributeNodeFilterSelfTest;

/**
 * Basic test suite.
 */
@RunWith(OpenCloverOptimizer.class)
@Suite.SuiteClasses({
    IgniteMarshallerSelfTestSuite.class,
    IgniteLangSelfTestSuite.class,
    IgniteUtilSelfTestSuite.class,

//    IgniteKernalSelfTestSuite.class,
//    IgniteStartUpTestSuite.class,
//    IgniteExternalizableSelfTestSuite.class,
//    IgniteP2PSelfTestSuite.class,
//    IgniteCacheP2pUnmarshallingErrorTestSuite.class,
//    IgniteStreamSelfTestSuite.class,
//
//    IgnitePlatformsTestSuite.class,
//
//    SecurityTestSuite.class,
//
//    GridSelfTest.class,
//    ClusterGroupHostsSelfTest.class,
//    IgniteMessagingWithClientTest.class,
//    IgniteMessagingSendAsyncTest.class,
//
//    ClusterProcessorCheckGlobalStateComputeRequestTest.class,
//    ClusterGroupSelfTest.class,
//    GridMessagingSelfTest.class,
//    GridMessagingNoPeerClassLoadingSelfTest.class,
//
//    GridReleaseTypeSelfTest.class,
//    GridProductVersionSelfTest.class,
//    GridAffinityAssignmentV2Test.class,
//    GridAffinityAssignmentV2TestNoOptimizations.class,
//    GridHistoryAffinityAssignmentTest.class,
//    GridHistoryAffinityAssignmentTestNoOptimization.class,
//    GridAffinityProcessorRendezvousSelfTest.class,
//    GridAffinityProcessorMemoryLeakTest.class,
//    GridClosureProcessorSelfTest.class,
//    GridClosureProcessorRemoteTest.class,
//    GridClosureSerializationTest.class,
//    GridStartStopSelfTest.class,
//    GridProjectionForCachesSelfTest.class,
//    GridProjectionForCachesOnDaemonNodeSelfTest.class,
//    GridSpiLocalHostInjectionTest.class,
//    GridLifecycleBeanSelfTest.class,
//    GridStopWithCancelSelfTest.class,
//    GridReduceSelfTest.class,
//    GridEventConsumeSelfTest.class,
//    GridSuppressedExceptionSelfTest.class,
//    GridLifecycleAwareSelfTest.class,
//    GridMessageListenSelfTest.class,
//    GridFailFastNodeFailureDetectionSelfTest.class,
//    IgniteSlowClientDetectionSelfTest.class,
//    IgniteDaemonNodeMarshallerCacheTest.class,
//    IgniteMarshallerCacheConcurrentReadWriteTest.class,
//    GridNodeMetricsLogSelfTest.class,
//    GridLocalIgniteSerializationTest.class,
//    GridMBeansTest.class,
//    TransactionsMXBeanImplTest.class,
//    SetTxTimeoutOnPartitionMapExchangeTest.class,
//    DiscoveryDataDeserializationFailureHanderTest.class,
//
//    IndexingFeatureIsNotAvailableTest.class,
//
//    IgniteExceptionInNioWorkerSelfTest.class,
//    IgniteLocalNodeMapBeforeStartTest.class,
//    OdbcConfigurationValidationSelfTest.class,
//    OdbcEscapeSequenceSelfTest.class,
//    SqlListenerUtilsTest.class,
//
//    DynamicProxySerializationMultiJvmSelfTest.class,
//
//    MarshallerContextLockingSelfTest.class,
//    MarshallerContextSelfTest.class,
//
//    SecurityPermissionSetBuilderTest.class,
//
//    AttributeNodeFilterSelfTest.class,
//
//    WALRecordTest.class,
//
//    // Basic DB data structures.
//    BPlusTreeSelfTest.class,
//    BPlusTreeFakeReuseSelfTest.class,
//    BPlusTreeReuseSelfTest.class,
//    IndexStorageSelfTest.class,
//    CacheFreeListSelfTest.class,
//    DataRegionMetricsSelfTest.class,
//    SwapPathConstructionSelfTest.class,
//    BitSetIntSetTest.class,
//
//    IgniteMarshallerCacheFSRestoreTest.class,
//    IgniteMarshallerCacheClassNameConflictTest.class,
//    IgniteMarshallerCacheClientRequestsMappingOnMissTest.class,
//
//    IgniteDiagnosticMessagesTest.class,
//    IgniteDiagnosticMessagesMultipleConnectionsTest.class,
//
//    IgniteRejectConnectOnNodeStopTest.class,
//
//    GridCleanerTest.class,
//
//    ClassSetTest.class,
//
//    // Basic failure handlers.
//    FailureHandlerTriggeredTest.class,
//    StopNodeFailureHandlerTest.class,
//    StopNodeOrHaltFailureHandlerTest.class,
//    OomFailureHandlerTest.class,
//    TransactionIntegrityWithSystemWorkerDeathTest.class,
//
//    AtomicOperationsInTxTest.class,
//
//    RebalanceWithDifferentThreadPoolSizeTest.class,
//
//    ListeningTestLoggerTest.class,
//
//    MessageOrderLogListenerTest.class,
//
//    CacheLocalGetSerializationTest.class,
//
//    PluginNodeValidationTest.class,
//
//    // In-memory Distributed MetaStorage.
//    DistributedMetaStorageTest.class,
//    DistributedMetaStorageHistoryCacheTest.class,
//    DistributedConfigurationInMemoryTest.class,
//    BaselineAutoAdjustMXBeanTest.class,
//
//    ConsistentIdImplicitlyExplicitlyTest.class,
//
//    // Tests against configuration variations framework.
//    ParametersTest.class,
//    VariationsIteratorTest.class,
//    NotStringSystemPropertyTest.class,
//    ConfigVariationsExecutionTest.class,
//    ConfigVariationsTestSuiteBuilderTest.class,
//
//    DeadLockOnNodeLeftExchangeTest.class,
//
//    ClassPathContentLoggingTest.class,
//
//    IncompleteDeserializationExceptionTest.class
})
public class IgniteBasicTestSuite {
}
