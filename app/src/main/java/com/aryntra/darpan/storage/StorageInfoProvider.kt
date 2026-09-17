package com.aryntra.darpan.storage

import android.os.Environment
import android.os.StatFs
import com.aryntra.darpan.ui.StorageUiState
import java.io.File

/**
 * Provider responsible for retrieving user-accessible partition capacity details
 * using StatFs, performing precise Gigabyte translations, and mapping to [StorageUiState].
 */
class StorageInfoProvider {

    /**
     * Inspects internal system partition and returns current [StorageUiState].
     */
    fun getStorageInfo(): StorageUiState {
        return try {
            val path: File = Environment.getDataDirectory()
            val stat = StatFs(path.path)

            val blockSize = stat.blockSizeLong
            val totalBlocks = stat.blockCountLong
            val availableBlocks = stat.availableBlocksLong

            val totalBytes = totalBlocks * blockSize
            val availableBytes = availableBlocks * blockSize
            val usedBytes = totalBytes - availableBytes

            val bytesInGb = 1024L * 1024L * 1024L

            val totalStorageGb = (totalBytes / bytesInGb).toInt()
            val availableStorageGb = (availableBytes / bytesInGb).toInt()
            val usedStorageGb = (usedBytes / bytesInGb).toInt()

            // Guard rails to prevent division by zero or invalid constraints
            val safeTotal = if (totalStorageGb > 0) totalStorageGb else 1
            val safeAvailable = availableStorageGb.coerceIn(0, safeTotal)
            val safeUsed = usedStorageGb.coerceIn(0, safeTotal)

            StorageUiState(
                totalStorageGb = safeTotal,
                usedStorageGb = safeUsed,
                availableStorageGb = safeAvailable
            )
        } catch (e: Exception) {
            // Graceful fallback on standard emulators or restricted environments
            StorageUiState(
                totalStorageGb = 1,
                usedStorageGb = 0,
                availableStorageGb = 1
            )
        }
    }
}
