package com.aryntra.darpan.snapshot

/**
 * Domain model representing a complete point-in-time observation of the host device.
 * Holds immutable telemetry across all hardware and OS subsystems.
 */
data class DeviceSnapshot(
    val id: Long = 0,
    val timestamp: Long,
    val sampleLabel: String? = null,
    val sequenceNumber: Int = 0,
    val deviceName: String = "Unknown Device",
    val manufacturer: String = "Unknown",
    val androidVersion: String = "Unknown",
    val batteryLevel: Int = 0,
    val isBatteryCharging: Boolean = false,
    val batteryChargingStatus: String = "Unknown",
    val totalStorageGb: Int = 0,
    val usedStorageGb: Int = 0,
    val availableStorageGb: Int = 0,
    val networkConnectionType: String = "None",
    val isNetworkConnected: Boolean = false,
    val networkStatusText: String = "Disconnected"
)
