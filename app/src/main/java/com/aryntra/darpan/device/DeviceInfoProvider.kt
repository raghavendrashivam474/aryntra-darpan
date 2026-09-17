package com.aryntra.darpan.device

import android.os.Build
import com.aryntra.darpan.ui.DeviceUiState
import java.util.Locale

/**
 * Provider responsible for querying Android SDK Build APIs
 * and mapping hardware/OS identity data into UI state models.
 */
class DeviceInfoProvider {

    /**
     * Inspects the host Android system and returns the current [DeviceUiState].
     */
    fun getDeviceInfo(): DeviceUiState {
        val rawManufacturer = Build.MANUFACTURER.orEmpty().trim()
        val rawModel = Build.MODEL.orEmpty().trim()
        val rawRelease = Build.VERSION.RELEASE.orEmpty().trim()
        val sdkInt = Build.VERSION.SDK_INT

        val formattedManufacturer = formatManufacturer(rawManufacturer)
        val formattedDeviceName = formatDeviceName(formattedManufacturer, rawModel)
        val formattedAndroidVersion = formatAndroidVersion(rawRelease, sdkInt)

        return DeviceUiState(
            deviceName = formattedDeviceName,
            manufacturer = formattedManufacturer,
            androidVersion = formattedAndroidVersion
        )
    }

    private fun formatManufacturer(manufacturer: String): String {
        if (manufacturer.isBlank() || manufacturer.equals("unknown", ignoreCase = true)) {
            return "Unknown"
        }
        return manufacturer.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
        }
    }

    private fun formatDeviceName(manufacturer: String, model: String): String {
        if (model.isBlank() || model.equals("unknown", ignoreCase = true)) {
            return "Unknown Device"
        }
        return model
    }

    private fun formatAndroidVersion(release: String, sdkInt: Int): String {
        val versionNumber = if (release.isNotBlank()) release else "Unknown"
        return "Android $versionNumber (API $sdkInt)"
    }
}