package com.aryntra.darpan.storage

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import com.aryntra.darpan.ui.StorageUiState

class StorageInfoProviderTest {

    @Test
    fun getStorageInfo_returnsNonNullState_withSafeDefaults() {
        val provider = StorageInfoProvider()
        val state = provider.getStorageInfo()

        assertNotNull(state)
        assertTrue("Total storage must be positive", state.totalStorageGb >= 0)
        assertTrue("Available storage must not be negative", state.availableStorageGb >= 0)
        assertTrue("Used storage must not be negative", state.usedStorageGb >= 0)
    }

    @Test
    fun storageUiState_fractionCalculation_isBounded() {
        val state = StorageUiState(
            totalStorageGb = 100,
            usedStorageGb = 40,
            availableStorageGb = 60
        )

        assertTrue(state.usedFraction in 0.0f..1.0f)
        assertTrue(state.usedFraction == 0.4f)
    }

    @Test
    fun storageUiState_zeroTotalStorage_doesNotThrow() {
        val zeroState = StorageUiState(
            totalStorageGb = 0,
            usedStorageGb = 0,
            availableStorageGb = 0
        )

        assertTrue(zeroState.usedFraction == 0.0f)
    }
}
