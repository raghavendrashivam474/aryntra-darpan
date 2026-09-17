package com.aryntra.darpan.persistence

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aryntra.darpan.snapshot.DeviceSnapshot

@Entity(tableName = "snapshots")
data class SnapshotEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long,
    val sampleLabel: String?,
    val sequenceNumber: Int,
    val deviceName: String,
    val manufacturer: String,
    val androidVersion: String,
    val batteryLevel: Int,
    val isBatteryCharging: Boolean,
    val batteryChargingStatus: String,
    val totalStorageGb: Int,
    val usedStorageGb: Int,
    val availableStorageGb: Int,
    val networkConnectionType: String,
    val isNetworkConnected: Boolean,
    val networkStatusText: String
) {
    fun toDomain(): DeviceSnapshot = DeviceSnapshot(
        id = id,
        timestamp = timestamp,
        sampleLabel = sampleLabel,
        sequenceNumber = sequenceNumber,
        deviceName = deviceName,
        manufacturer = manufacturer,
        androidVersion = androidVersion,
        batteryLevel = batteryLevel,
        isBatteryCharging = isBatteryCharging,
        batteryChargingStatus = batteryChargingStatus,
        totalStorageGb = totalStorageGb,
        usedStorageGb = usedStorageGb,
        availableStorageGb = availableStorageGb,
        networkConnectionType = networkConnectionType,
        isNetworkConnected = isNetworkConnected,
        networkStatusText = networkStatusText
    )

    companion object {
        fun fromDomain(domain: DeviceSnapshot): SnapshotEntity = SnapshotEntity(
            id = domain.id,
            timestamp = domain.timestamp,
            sampleLabel = domain.sampleLabel,
            sequenceNumber = domain.sequenceNumber,
            deviceName = domain.deviceName,
            manufacturer = domain.manufacturer,
            androidVersion = domain.androidVersion,
            batteryLevel = domain.batteryLevel,
            isBatteryCharging = domain.isBatteryCharging,
            batteryChargingStatus = domain.batteryChargingStatus,
            totalStorageGb = domain.totalStorageGb,
            usedStorageGb = domain.usedStorageGb,
            availableStorageGb = domain.availableStorageGb,
            networkConnectionType = domain.networkConnectionType,
            isNetworkConnected = domain.isNetworkConnected,
            networkStatusText = domain.networkStatusText
        )
    }
}
