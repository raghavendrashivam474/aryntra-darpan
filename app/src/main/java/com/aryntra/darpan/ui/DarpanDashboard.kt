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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aryntra.darpan.DeviceSnapshot
import com.aryntra.darpan.battery.BatteryInfoProvider
import com.aryntra.darpan.device.DeviceInfoProvider
import com.aryntra.darpan.network.NetworkInfoProvider
import com.aryntra.darpan.storage.StorageInfoProvider

/**
 * S5 Darpan Dashboard: Primary structured dashboard surface.
 * Consumes real native state from [DeviceInfoProvider], [BatteryInfoProvider],
 * [StorageInfoProvider], and [NetworkInfoProvider] while maintaining pure UI state hoisting
 * for card composables.
 */
@Composable
fun DarpanDashboard(
    modifier: Modifier = Modifier,
    deviceInfoProvider: DeviceInfoProvider = remember { DeviceInfoProvider() },
    storageInfoProvider: StorageInfoProvider = remember { StorageInfoProvider() },
    batteryInfoProvider: BatteryInfoProvider? = null,
    networkInfoProvider: NetworkInfoProvider? = null
) {
    val context = LocalContext.current.applicationContext
    val resolvedBatteryProvider = remember(batteryInfoProvider, context) {
        batteryInfoProvider ?: BatteryInfoProvider(context)
    }
    val resolvedNetworkProvider = remember(networkInfoProvider, context) {
        networkInfoProvider ?: NetworkInfoProvider(context)
    }

    var refreshSequence by remember { mutableIntStateOf(0) }

    // S3, S4 & S5: Real native state populated from Android SDK APIs
    var deviceState by remember { mutableStateOf(deviceInfoProvider.getDeviceInfo()) }
    var batteryState by remember { mutableStateOf(resolvedBatteryProvider.getBatteryInfo()) }
    var storageState by remember { mutableStateOf(storageInfoProvider.getStorageInfo()) }
    var networkState by remember { mutableStateOf(resolvedNetworkProvider.getNetworkInfo()) }

    // Snapshot history state list
    val snapshotHistory = remember {
        mutableStateListOf(
            DeviceSnapshot(
                timestamp = System.currentTimeMillis(),
                sampleLabel = "Initial System Baseline",
                sequenceNumber = 1
            )
        )
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
                                // Re-query native hardware & system states
                                deviceState = deviceInfoProvider.getDeviceInfo()
                                batteryState = resolvedBatteryProvider.getBatteryInfo()
                                storageState = storageInfoProvider.getStorageInfo()
                                networkState = resolvedNetworkProvider.getNetworkInfo()

                                val newSnapshot = DeviceSnapshot(
                                    timestamp = System.currentTimeMillis(),
                                    sampleLabel = "Manual Refresh",
                                    sequenceNumber = snapshotHistory.size + 1
                                )
                                snapshotHistory.add(newSnapshot)
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

            // S3 Device Section (Populated with real Android Build data)
            item {
                DeviceCard(state = deviceState)
            }

            // S4 Battery Section (Populated with real Android BatteryManager data)
            item {
                BatteryCard(state = batteryState)
            }

            // S4 Storage Section (Populated with real StatFs data)
            item {
                StorageCard(state = storageState)
            }

            // S5 Network Section (Populated with real ConnectivityManager data)
            item {
                NetworkCard(state = networkState)
            }

            // S2.7 Snapshot History Section
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
