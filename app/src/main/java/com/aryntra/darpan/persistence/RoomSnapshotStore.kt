package com.aryntra.darpan.persistence

import android.content.Context
import android.util.Log
import com.aryntra.darpan.snapshot.DeviceSnapshot
import com.aryntra.darpan.snapshot.SnapshotStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Room and SQLite backed implementation of [SnapshotStore].
 * Performs all disk I/O on [Dispatchers.IO] and catches exceptions gracefully
 * to guarantee that database errors never crash the UI or live telemetry.
 */
class RoomSnapshotStore(
    private val dao: SnapshotDao
) : SnapshotStore {

    constructor(context: Context) : this(
        DarpanDatabase.getDatabase(context).snapshotDao()
    )

    private val tag = "RoomSnapshotStore"

    override suspend fun save(snapshot: DeviceSnapshot): Long = withContext(Dispatchers.IO) {
        try {
            val entity = SnapshotEntity.fromDomain(snapshot)
            dao.insert(entity)
        } catch (e: Exception) {
            Log.e(tag, "Failed to persist snapshot", e)
            -1L
        }
    }

    override suspend fun getRecent(limit: Int): List<DeviceSnapshot> = withContext(Dispatchers.IO) {
        try {
            dao.getRecent(limit).map { it.toDomain() }
        } catch (e: Exception) {
            Log.e(tag, "Failed to retrieve recent snapshots", e)
            emptyList()
        }
    }

    override suspend fun getAll(): List<DeviceSnapshot> = withContext(Dispatchers.IO) {
        try {
            dao.getAll().map { it.toDomain() }
        } catch (e: Exception) {
            Log.e(tag, "Failed to retrieve all snapshots", e)
            emptyList()
        }
    }

    override suspend fun getCount(): Int = withContext(Dispatchers.IO) {
        try {
            dao.getCount()
        } catch (e: Exception) {
            Log.e(tag, "Failed to query snapshot count", e)
            0
        }
    }

    override suspend fun clear(): Unit = withContext(Dispatchers.IO) {
        try {
            dao.clearAll()
        } catch (e: Exception) {
            Log.e(tag, "Failed to clear snapshots", e)
        }
    }
}
