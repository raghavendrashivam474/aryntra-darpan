package com.aryntra.darpan.snapshot

/**
 * Persistence boundary interface for persisting and retrieving device snapshots.
 * Decouples the UI and domain layers from the specific storage mechanism (Room/SQLite).
 */
interface SnapshotStore {
    suspend fun save(snapshot: DeviceSnapshot): Long
    suspend fun getRecent(limit: Int = 10): List<DeviceSnapshot>
    suspend fun getAll(): List<DeviceSnapshot>
    suspend fun getCount(): Int
    suspend fun clear()
}
