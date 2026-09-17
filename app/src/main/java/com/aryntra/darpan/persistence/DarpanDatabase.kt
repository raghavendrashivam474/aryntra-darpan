package com.aryntra.darpan.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SnapshotEntity::class], version = 1, exportSchema = false)
abstract class DarpanDatabase : RoomDatabase() {

    abstract fun snapshotDao(): SnapshotDao

    companion object {
        @Volatile
        private var INSTANCE: DarpanDatabase? = null

        fun getDatabase(context: Context): DarpanDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DarpanDatabase::class.java,
                    "darpan.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
