package com.aryntra.darpan.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aryntra.darpan.snapshot.DeviceSnapshot
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * UI State representations for Dashboard cards.
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
        get() = if (totalStorageGb > 0) {
            (usedStorageGb.toFloat() / totalStorageGb.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }
}

data class NetworkUiState(
    val connectionType: String = "Wi-Fi",
    val isConnected: Boolean = true,
    val statusText: String = "Connected"
)

/**
 * Common Card Container applying rounded corners, border outline, and internal padding.
 */
@Composable
fun SectionContainer(
    title: String,
    modifier: Modifier = Modifier,
    trailingContent: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                trailingContent?.invoke()
            }
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}

/**
 * Helper row for key-value telemetry pairs.
 */
@Composable
fun MetricRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    trailingLabel: String? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.outline
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (trailingLabel != null) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = trailingLabel,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
fun DeviceCard(
    state: DeviceUiState,
    modifier: Modifier = Modifier
) {
    SectionContainer(title = "Device", modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            MetricRow(label = "Model", value = state.deviceName)
            MetricRow(label = "Manufacturer", value = state.manufacturer)
            MetricRow(label = "OS Version", value = state.androidVersion)
        }
    }
}

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
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = state.chargingStatus,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            LinearProgressIndicator(
                progress = { state.levelFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                strokeCap = StrokeCap.Round
            )
        }
    }
}

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
                    text = "${state.usedStorageGb} GB used",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${state.availableStorageGb} GB free / ${state.totalStorageGb} GB total",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            LinearProgressIndicator(
                progress = { state.usedFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                strokeCap = StrokeCap.Round
            )
        }
    }
}

@Composable
fun NetworkCard(
    state: NetworkUiState,
    modifier: Modifier = Modifier
) {
    val indicatorColor = if (state.isConnected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.error
    }

    SectionContainer(title = "Network", modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            MetricRow(label = "Interface", value = state.connectionType)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Status",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.outline
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(color = indicatorColor, shape = CircleShape)
                    )
                    Text(
                        text = state.statusText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * S6 Snapshot Row Item with rich telemetry details.
 */
@Composable
fun SnapshotRow(
    snapshot: DeviceSnapshot,
    modifier: Modifier = Modifier
) {
    val formattedTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        .format(Date(snapshot.timestamp))
    val labelText = snapshot.sampleLabel ?: "Observation"

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "#${snapshot.sequenceNumber} \u2022 $labelText",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${snapshot.batteryLevel}% \u2022 ${snapshot.usedStorageGb}GB used \u2022 ${snapshot.networkConnectionType}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.outline
            )
        }
        Text(
            text = formattedTime,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

/**
 * S6 Snapshot History Card (with empty state handling & persistence badge)
 */
@Composable
fun SnapshotHistorySection(
    snapshots: List<DeviceSnapshot>,
    modifier: Modifier = Modifier
) {
    SectionContainer(
        title = "Persistent History",
        modifier = modifier,
        trailingContent = {
            if (snapshots.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = "${snapshots.size} saved",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    ) {
        if (snapshots.isEmpty()) {
            Text(
                text = "No snapshots recorded yet. Tap refresh to capture state.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Show most recent first (up to 5 in the preview list)
                snapshots.asReversed().take(5).forEach { snapshot ->
                    SnapshotRow(snapshot = snapshot)
                }
            }
        }
    }
}
