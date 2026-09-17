package com.aryntra.darpan.persistence

import com.aryntra.darpan.snapshot.DeviceSnapshot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SnapshotMappingTest {

    @Test
    fun domainToEntity_mapsAllFieldsAccurately() {
        val domain = DeviceSnapshot(
            id = 42L,
            timestamp = 1711234567890L,
            sampleLabel = "Manual Refresh",
            sequenceNumber = 5,
            deviceName = "Pixel 8 Pro",
            manufacturer = "Google",
            androidVersion = "Android 14 (API 34)",
            batteryLevel = 88,
            isBatteryCharging = true,
            batteryChargingStatus = "Charging",
            totalStorageGb = 256,
            usedStorageGb = 100,
            availableStorageGb = 156,
            networkConnectionType = "Wi-Fi",
            isNetworkConnected = true,
            networkStatusText = "Connected"
        )

        val entity = SnapshotEntity.fromDomain(domain)

        assertEquals(42L, entity.id)
        assertEquals(1711234567890L, entity.timestamp)
        assertEquals("Manual Refresh", entity.sampleLabel)
        assertEquals(5, entity.sequenceNumber)
        assertEquals("Pixel 8 Pro", entity.deviceName)
        assertEquals("Google", entity.manufacturer)
        assertEquals("Android 14 (API 34)", entity.androidVersion)
        assertEquals(88, entity.batteryLevel)
        assertTrue(entity.isBatteryCharging)
        assertEquals("Charging", entity.batteryChargingStatus)
        assertEquals(256, entity.totalStorageGb)
        assertEquals(100, entity.usedStorageGb)
        assertEquals(156, entity.availableStorageGb)
        assertEquals("Wi-Fi", entity.networkConnectionType)
        assertTrue(entity.isNetworkConnected)
        assertEquals("Connected", entity.networkStatusText)
    }

    @Test
    fun entityToDomain_mapsAllFieldsAccurately() {
        val entity = SnapshotEntity(
            id = 101L,
            timestamp = 1719999999999L,
            sampleLabel = "Initial System Baseline",
            sequenceNumber = 1,
            deviceName = "Galaxy S24",
            manufacturer = "Samsung",
            androidVersion = "Android 14 (API 34)",
            batteryLevel = 45,
            isBatteryCharging = false,
            batteryChargingStatus = "Discharging",
            totalStorageGb = 512,
            usedStorageGb = 200,
            availableStorageGb = 312,
            networkConnectionType = "Cellular",
            isNetworkConnected = true,
            networkStatusText = "Connected"
        )

        val domain = entity.toDomain()

        assertEquals(101L, domain.id)
        assertEquals(1719999999999L, domain.timestamp)
        assertEquals("Initial System Baseline", domain.sampleLabel)
        assertEquals(1, domain.sequenceNumber)
        assertEquals("Galaxy S24", domain.deviceName)
        assertEquals("Samsung", domain.manufacturer)
        assertEquals("Android 14 (API 34)", domain.androidVersion)
        assertEquals(45, domain.batteryLevel)
        assertFalse(domain.isBatteryCharging)
        assertEquals("Discharging", domain.batteryChargingStatus)
        assertEquals(512, domain.totalStorageGb)
        assertEquals(200, domain.usedStorageGb)
        assertEquals(312, domain.availableStorageGb)
        assertEquals("Cellular", domain.networkConnectionType)
        assertTrue(domain.isNetworkConnected)
        assertEquals("Connected", domain.networkStatusText)
    }

    @Test
    fun bidirectionalMapping_preservesEquality() {
        val original = DeviceSnapshot(
            id = 7L,
            timestamp = 1700000000000L,
            sampleLabel = "Test Baseline",
            sequenceNumber = 2,
            deviceName = "Nord CE 3",
            manufacturer = "OnePlus",
            androidVersion = "Android 13 (API 33)",
            batteryLevel = 99,
            isBatteryCharging = true,
            batteryChargingStatus = "Full",
            totalStorageGb = 128,
            usedStorageGb = 50,
            availableStorageGb = 78,
            networkConnectionType = "Wi-Fi",
            isNetworkConnected = true,
            networkStatusText = "Connected"
        )

        val mappedEntity = SnapshotEntity.fromDomain(original)
        val restoredDomain = mappedEntity.toDomain()

        assertEquals(original, restoredDomain)
    }

    @Test
    fun defaultConstructor_hasSafeFallbackValues() {
        val snapshot = DeviceSnapshot(timestamp = 1000L)

        assertNotNull(snapshot)
        assertEquals(0L, snapshot.id)
        assertEquals(1000L, snapshot.timestamp)
        assertEquals(null, snapshot.sampleLabel)
        assertEquals(0, snapshot.sequenceNumber)
        assertEquals("Unknown Device", snapshot.deviceName)
        assertEquals("Unknown", snapshot.manufacturer)
        assertEquals("Unknown", snapshot.androidVersion)
        assertEquals(0, snapshot.batteryLevel)
        assertFalse(snapshot.isBatteryCharging)
        assertEquals("Disconnected", snapshot.networkStatusText)
    }
}
