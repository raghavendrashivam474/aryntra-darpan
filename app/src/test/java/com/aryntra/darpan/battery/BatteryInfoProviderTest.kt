package com.aryntra.darpan.battery

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import com.aryntra.darpan.ui.BatteryUiState

class BatteryInfoProviderTest {

    @Test
    fun batteryUiState_defaultValuesAndFraction_areBounded() {
        val state = BatteryUiState(
            levelPercentage = 75,
            isCharging = true,
            chargingStatus = "Charging"
        )

        assertNotNull(state)
        assertTrue(state.levelPercentage in 0..100)
        assertTrue(state.levelFraction in 0.0f..1.0f)
        assertTrue(state.isCharging)
    }

    @Test
    fun batteryUiState_boundsCoercion_handlesEdgeValues() {
        val overflowState = BatteryUiState(levelPercentage = 150)
        assertTrue(overflowState.levelFraction == 1.0f)

        val negativeState = BatteryUiState(levelPercentage = -10)
        assertTrue(negativeState.levelFraction == 0.0f)
    }
}
