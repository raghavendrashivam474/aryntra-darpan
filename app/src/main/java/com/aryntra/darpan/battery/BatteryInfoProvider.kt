package com.aryntra.darpan.battery

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.aryntra.darpan.ui.BatteryUiState

/**
 * Provider responsible for retrieving the host device's real battery levels,
 * charging state, and status from the Android SDK, mapping them directly
 * to [BatteryUiState].
 */
class BatteryInfoProvider(private val context: Context) {

    /**
     * Queries the system and returns the current [BatteryUiState].
     */
    fun getBatteryInfo(): BatteryUiState {
        return try {
            val appContext = context.applicationContext
            val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val batteryStatus: Intent? = appContext.registerReceiver(null, filter)

            val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
            
            val calculatedPercent = if (level >= 0 && scale > 0) {
                (level * 100 / scale.toFloat()).toInt()
            } else {
                -1
            }

            // Bound percentage to valid range, defaulting to 50 if invalid/unavailable
            val levelPercentage = if (calculatedPercent in 0..100) calculatedPercent else 50

            val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
            val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL

            val chargingStatus = when (status) {
                BatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
                BatteryManager.BATTERY_STATUS_DISCHARGING -> "Discharging"
                BatteryManager.BATTERY_STATUS_FULL -> "Full"
                BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Not charging"
                else -> "Unknown"
            }

            BatteryUiState(
                levelPercentage = levelPercentage,
                isCharging = isCharging,
                chargingStatus = chargingStatus
            )
        } catch (e: Exception) {
            // Graceful fallback to avoid application crash if intent is blocked or fails
            BatteryUiState(
                levelPercentage = 50,
                isCharging = false,
                chargingStatus = "Unknown"
            )
        }
    }
}
