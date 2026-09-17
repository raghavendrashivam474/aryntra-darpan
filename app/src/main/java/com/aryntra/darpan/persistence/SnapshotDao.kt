package com.aryntra.darpan.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SnapshotDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(snapshot: SnapshotEntity): Long

    @Query("SELECT * FROM snapshots ORDER BY id DESC LIMIT :limit")
    suspend fun getRecent(limit: Int): List<SnapshotEntity>

    @Query("SELECT * FROM snapshots ORDER BY id ASC")
    suspend fun getAll(): List<SnapshotEntity>

    @Query("SELECT COUNT(*) FROM snapshots")
    suspend fun getCount(): Int

    @Query("DELETE FROM snapshots")
    suspend fun clearAll()
}
