package com.aryntra.darpan.network

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import com.aryntra.darpan.ui.NetworkUiState

class NetworkInfoProviderTest {

    @Test
    fun networkUiState_defaultValues_areValid() {
        val state = NetworkUiState()

        assertNotNull(state)
        assertEquals("Wi-Fi", state.connectionType)
        assertTrue(state.isConnected)
        assertEquals("Connected", state.statusText)
    }

    @Test
    fun networkUiState_disconnectedState_representedCorrectly() {
        val state = NetworkUiState(
            connectionType = "None",
            isConnected = false,
            statusText = "Disconnected"
        )

        assertEquals("None", state.connectionType)
        assertFalse(state.isConnected)
        assertEquals("Disconnected", state.statusText)
    }

    @Test
    fun networkUiState_cellularConnected_representedCorrectly() {
        val state = NetworkUiState(
            connectionType = "Cellular",
            isConnected = true,
            statusText = "Connected"
        )

        assertEquals("Cellular", state.connectionType)
        assertTrue(state.isConnected)
        assertEquals("Connected", state.statusText)
    }

    @Test
    fun networkUiState_fallbackUnknown_representedCorrectly() {
        val state = NetworkUiState(
            connectionType = "Unknown",
            isConnected = false,
            statusText = "Unavailable"
        )

        assertEquals("Unknown", state.connectionType)
        assertFalse(state.isConnected)
        assertEquals("Unavailable", state.statusText)
    }
}
