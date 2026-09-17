package com.aryntra.darpan.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.aryntra.darpan.ui.NetworkUiState

/**
 * S5 Provider: Responsible for retrieving the host device's active network state,
 * transport type, and connectivity capabilities using native Android [ConnectivityManager].
 * Normalizes framework values safely into [NetworkUiState].
 */
class NetworkInfoProvider(private val context: Context) {

    /**
     * Queries the system [ConnectivityManager] and returns the current [NetworkUiState].
     */
    fun getNetworkInfo(): NetworkUiState {
        return try {
            val connectivityManager = context.applicationContext
                .getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
                ?: return NetworkUiState(
                    connectionType = "None",
                    isConnected = false,
                    statusText = "Disconnected"
                )

            val activeNetwork = connectivityManager.activeNetwork
                ?: return NetworkUiState(
                    connectionType = "None",
                    isConnected = false,
                    statusText = "Disconnected"
                )

            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
                ?: return NetworkUiState(
                    connectionType = "None",
                    isConnected = false,
                    statusText = "Disconnected"
                )

            val isConnected = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            val isValidated = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

            val transport = when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi"
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Cellular"
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> "Bluetooth"
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> "VPN"
                else -> "Unknown"
            }

            val statusText = when {
                !isConnected -> "Disconnected"
                isValidated -> "Connected"
                else -> "Connected (No Internet)"
            }

            NetworkUiState(
                connectionType = transport,
                isConnected = isConnected,
                statusText = statusText
            )
        } catch (e: Exception) {
            // Safe fallback to prevent crashes if system service query fails
            NetworkUiState(
                connectionType = "Unknown",
                isConnected = false,
                statusText = "Unavailable"
            )
        }
    }
}
