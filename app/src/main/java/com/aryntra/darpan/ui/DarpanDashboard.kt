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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aryntra.darpan.DeviceSnapshot
import com.aryntra.darpan.device.DeviceInfoProvider

/**
 * S3 Darpan Dashboard: Primary structured dashboard surface.
 * Consumes native device state from [DeviceInfoProvider] while maintaining
 * pure UI state hoisting for card composables.
 */
@Composable
fun DarpanDashboard(
    modifier: Modifier = Modifier,
    deviceInfoProvider: DeviceInfoProvider = remember { DeviceInfoProvider() }
) {
    var refreshSequence by remember { mutableIntStateOf(0) }

    // S3: Real Device state populated from Android SDK Build APIs
    var deviceState by remember { mutableStateOf(deviceInfoProvider.getDeviceInfo()) }

    // S2 Mock states (scheduled for S4 & S5 real data transitions)
    var batteryState by remember { mutableStateOf(BatteryUiState()) }
    var storageState by remember { mutableStateOf(StorageUiState()) }
    var networkState by remember { mutableStateOf(NetworkUiState()) }

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
                                // Re-query native device state
                                deviceState = deviceInfoProvider.getDeviceInfo()

                                // Cycle mock states slightly to illustrate recomposition feedback
                                val mockBatteryLevels = listOf(82, 81, 80, 83)
                                val currentMockLevel = mockBatteryLevels[refreshSequence % mockBatteryLevels.size]
                                batteryState = batteryState.copy(levelPercentage = currentMockLevel)

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

            // S2.4 Battery Section
            item {
                BatteryCard(state = batteryState)
            }

            // S2.5 Storage Section
            item {
                StorageCard(state = storageState)
            }

            // S2.6 Network Section
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