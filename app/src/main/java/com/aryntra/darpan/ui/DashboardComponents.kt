package com.aryntra.darpan.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aryntra.darpan.DeviceSnapshot
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * UI State representations for Dashboard cards.
 * S3: DeviceUiState represents real device/OS inspection data.
 */
data class DeviceUiState(
    val deviceName: String = "Unknown Device",
    val manufacturer: String = "Unknown",
    val androidVersion: String = "Unknown"
)

data class BatteryUiState(
    val levelPercentage: Int = 82,
    val isCharging: Boolean = true,
    val chargingStatus: String = "Charging"
) {
    val levelFraction: Float
        get() = (levelPercentage.coerceIn(0, 100)) / 100f
}

data class StorageUiState(
    val totalStorageGb: Int = 128,
    val usedStorageGb: Int = 57,
    val availableStorageGb: Int = 71
) {
    val usedFraction: Float
        get() = if (totalStorageGb > 0) usedStorageGb.toFloat() / totalStorageGb.toFloat() else 0f
}

data class NetworkUiState(
    val connectionType: String = "Wi-Fi",
    val isConnected: Boolean = true,
    val statusText: String = "Connected"
)

/**
 * Reusable Section Card container with clean technical Material 3 styling.
 */
@Composable
fun SectionContainer(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = title.uppercase(Locale.ROOT),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.primary
            )
            content()
        }
    }
}

/**
 * Reusable Key-Value Metric row.
 */
@Composable
fun MetricRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    isMonospace: Boolean = false,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = if (isMonospace) FontFamily.Monospace else FontFamily.Default,
            color = valueColor
        )
    }
}

/**
 * S2.3 & S3 Device Information Section
 */
@Composable
fun DeviceCard(
    state: DeviceUiState,
    modifier: Modifier = Modifier
) {
    SectionContainer(title = "Device", modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            MetricRow(label = "Model", value = state.deviceName)
            MetricRow(label = "Manufacturer", value = state.manufacturer)
            MetricRow(label = "OS Version", value = state.androidVersion, isMonospace = true)
        }
    }
}

/**
 * S2.4 Battery State Section (Mocked in S3, real in S4)
 */
@Composable
fun BatteryCard(
    state: BatteryUiState,
    modifier: Modifier = Modifier
) {
    SectionContainer(title = "Battery", modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${state.levelPercentage}%",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = state.chargingStatus,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (state.isCharging) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            LinearProgressIndicator(
                progress = { state.levelFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

/**
 * S2.5 Storage Section (Mocked in S3, real in S4)
 */
@Composable
fun StorageCard(
    state: StorageUiState,
    modifier: Modifier = Modifier
) {
    SectionContainer(title = "Storage", modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${state.availableStorageGb} GB free",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${state.usedStorageGb} GB / ${state.totalStorageGb} GB",
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            LinearProgressIndicator(
                progress = { state.usedFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = MaterialTheme.colorScheme.tertiary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

/**
 * S2.6 Network Section (Mocked in S3, real in S5)
 */
@Composable
fun NetworkCard(
    state: NetworkUiState,
    modifier: Modifier = Modifier
) {
    SectionContainer(title = "Network", modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            MetricRow(label = "Interface", value = state.connectionType)
            MetricRow(
                label = "Status",
                value = state.statusText,
                valueColor = if (state.isConnected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }
    }
}

/**
 * S2.7 Snapshot Row Item
 */
@Composable
fun SnapshotRow(
    snapshot: DeviceSnapshot,
    modifier: Modifier = Modifier
) {
    val formattedTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        .format(Date(snapshot.timestamp))
    val labelText = snapshot.sampleLabel ?: "Unnamed Snapshot"

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "#${snapshot.sequenceNumber} • $labelText",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = formattedTime,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

/**
 * S2.7 Snapshot History Card (with empty state handling)
 */
@Composable
fun SnapshotHistorySection(
    snapshots: List<DeviceSnapshot>,
    modifier: Modifier = Modifier
) {
    SectionContainer(title = "Recent Snapshots", modifier = modifier) {
        if (snapshots.isEmpty()) {
            Text(
                text = "No snapshots recorded yet. Tap refresh to capture state.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Show most recent first
                snapshots.asReversed().take(5).forEach { snapshot ->
                    SnapshotRow(snapshot = snapshot)
                }
            }
        }
    }
}