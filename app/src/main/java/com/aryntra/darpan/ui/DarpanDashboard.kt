package com.aryntra.darpan.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aryntra.darpan.battery.BatteryInfoProvider
import com.aryntra.darpan.device.DeviceInfoProvider
import com.aryntra.darpan.network.NetworkInfoProvider
import com.aryntra.darpan.persistence.RoomSnapshotStore
import com.aryntra.darpan.snapshot.DeviceSnapshot
import com.aryntra.darpan.snapshot.SnapshotStore
import com.aryntra.darpan.storage.StorageInfoProvider
import kotlinx.coroutines.launch

/**
 * S6 Darpan Dashboard: Primary structured dashboard surface.
 * Consumes real native state from [DeviceInfoProvider], [BatteryInfoProvider],
 * [StorageInfoProvider], and [NetworkInfoProvider] while persisting historical snapshots
 * to the local Room database via [SnapshotStore].
 *
 * Hybrid Architecture:
 * - Live State: Pure hot state in RAM for instant rendering.
 * - Historical State: Persisted asynchronously to local SQLite database.
 */
@Composable
fun DarpanDashboard(
    modifier: Modifier = Modifier,
    deviceInfoProvider: DeviceInfoProvider = remember { DeviceInfoProvider() },
    storageInfoProvider: StorageInfoProvider = remember { StorageInfoProvider() },
    batteryInfoProvider: BatteryInfoProvider? = null,
    networkInfoProvider: NetworkInfoProvider? = null,
    snapshotStore: SnapshotStore? = null
) {
    val context = LocalContext.current.applicationContext
    val coroutineScope = rememberCoroutineScope()

    val resolvedBatteryProvider = remember(batteryInfoProvider, context) {
        batteryInfoProvider ?: BatteryInfoProvider(context)
    }
    val resolvedNetworkProvider = remember(networkInfoProvider, context) {
        networkInfoProvider ?: NetworkInfoProvider(context)
    }
    val resolvedSnapshotStore = remember(snapshotStore, context) {
        snapshotStore ?: RoomSnapshotStore(context)
    }

    var refreshSequence by remember { mutableIntStateOf(0) }

    // Live Hot State (RAM)
    var deviceState by remember { mutableStateOf(deviceInfoProvider.getDeviceInfo()) }
    var batteryState by remember { mutableStateOf(resolvedBatteryProvider.getBatteryInfo()) }
    var storageState by remember { mutableStateOf(storageInfoProvider.getStorageInfo()) }
    var networkState by remember { mutableStateOf(resolvedNetworkProvider.getNetworkInfo()) }

    // Persistent Snapshot History State
    val snapshotHistory = remember { mutableStateListOf<DeviceSnapshot>() }

    // Initial load: populate history from local persistent store
    LaunchedEffect(resolvedSnapshotStore) {
        val storedSnapshots = resolvedSnapshotStore.getRecent(10)
        snapshotHistory.clear()
        if (storedSnapshots.isNotEmpty()) {
            // Room returns newest first (DESC), reverse for sequential display
            snapshotHistory.addAll(storedSnapshots.reversed())
        } else {
            // First run on clean device: capture initial baseline snapshot
            val initialSnapshot = DeviceSnapshot(
                timestamp = System.currentTimeMillis(),
                sampleLabel = "Initial System Baseline",
                sequenceNumber = 1,
                deviceName = deviceState.deviceName,
                manufacturer = deviceState.manufacturer,
                androidVersion = deviceState.androidVersion,
                batteryLevel = batteryState.levelPercentage,
                isBatteryCharging = batteryState.isCharging,
                batteryChargingStatus = batteryState.chargingStatus,
                totalStorageGb = storageState.totalStorageGb,
                usedStorageGb = storageState.usedStorageGb,
                availableStorageGb = storageState.availableStorageGb,
                networkConnectionType = networkState.connectionType,
                isNetworkConnected = networkState.isConnected,
                networkStatusText = networkState.statusText
            )
            snapshotHistory.add(initialSnapshot)
            coroutineScope.launch {
                resolvedSnapshotStore.save(initialSnapshot)
            }
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header item: Identity & Subtitle + Refresh Action
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Aryntra Darpan",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Local Device Environment",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }

                        Button(
                            onClick = {
                                refreshSequence++
                                // 1. Re-query native hardware & system states (Hot State in RAM)
                                val newDevice = deviceInfoProvider.getDeviceInfo()
                                val newBattery = resolvedBatteryProvider.getBatteryInfo()
                                val newStorage = storageInfoProvider.getStorageInfo()
                                val newNetwork = resolvedNetworkProvider.getNetworkInfo()

                                deviceState = newDevice
                                batteryState = newBattery
                                storageState = newStorage
                                networkState = newNetwork

                                // 2. Create full telemetry snapshot
                                val newSnapshot = DeviceSnapshot(
                                    timestamp = System.currentTimeMillis(),
                                    sampleLabel = "Manual Refresh",
                                    sequenceNumber = snapshotHistory.size + 1,
                                    deviceName = newDevice.deviceName,
                                    manufacturer = newDevice.manufacturer,
                                    androidVersion = newDevice.androidVersion,
                                    batteryLevel = newBattery.levelPercentage,
                                    isBatteryCharging = newBattery.isCharging,
                                    batteryChargingStatus = newBattery.chargingStatus,
                                    totalStorageGb = newStorage.totalStorageGb,
                                    usedStorageGb = newStorage.usedStorageGb,
                                    availableStorageGb = newStorage.availableStorageGb,
                                    networkConnectionType = newNetwork.connectionType,
                                    isNetworkConnected = newNetwork.isConnected,
                                    networkStatusText = newNetwork.statusText
                                )

                                // 3. Update RAM history immediately
                                snapshotHistory.add(newSnapshot)

                                // 4. Persist to SQLite asynchronously on background thread
                                coroutineScope.launch {
                                    resolvedSnapshotStore.save(newSnapshot)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        ) {
                            Text(
                                text = "Refresh",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // S3 Device Section
            item {
                DeviceCard(state = deviceState)
            }

            // S4 Battery Section
            item {
                BatteryCard(state = batteryState)
            }

            // S4 Storage Section
            item {
                StorageCard(state = storageState)
            }

            // S5 Network Section
            item {
                NetworkCard(state = networkState)
            }

            // S6 Persistent Snapshot History Section
            item {
                SnapshotHistorySection(snapshots = snapshotHistory)
            }

            // Bottom Spacing
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
