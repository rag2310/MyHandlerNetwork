package com.rago.myhandlernetwork.data.utils

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.telephony.TelephonyManager
import androidx.annotation.RequiresPermission
import com.rago.myhandlernetwork.data.model.NetworkStatusUiState
import com.rago.myhandlernetwork.data.model.NetworkType
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

class NetworkMonitor @Inject constructor(
    private val context: Context
): INetworkMonitor {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    override fun observeNetworkStatus(): Flow<NetworkStatusUiState> {
        return callbackFlow {
            trySend(getCurrentNetworkState())

            val networkCallback = object : ConnectivityManager.NetworkCallback() {
                @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
                override fun onAvailable(network: Network) {
                    trySend(getCurrentNetworkState())
                }

                @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
                override fun onLost(network: Network) {
                    launch { // Usar un scope adecuado
                        kotlinx.coroutines.delay(1000)
                        trySend(getCurrentNetworkState())
                    }
                }

                @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
                override fun onCapabilitiesChanged(network: Network, caps: NetworkCapabilities) {
                    trySend(getCurrentNetworkState(caps))
                }
            }

            connectivityManager.registerDefaultNetworkCallback(networkCallback)

            awaitClose {
                connectivityManager.unregisterNetworkCallback(networkCallback)
            }
        }.distinctUntilChanged()
    }

    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    private fun getCurrentNetworkState(caps: NetworkCapabilities? = null): NetworkStatusUiState {
        val capabilities = caps ?: connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

        if (capabilities == null) {
            return NetworkStatusUiState.Disconnected("No hay una red activa.")
        }

        val hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        val isValidated = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)


        if (!hasInternet || !isValidated){
            return NetworkStatusUiState.Disconnected("Conectado, pero sin acceso a Internet.")
        }

        return if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            NetworkStatusUiState.Connected(NetworkType.WIFI)
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
            NetworkStatusUiState.Connected(getCellularNetworkType())
        } else {
            NetworkStatusUiState.Connected(NetworkType.UNKNOWN)
        }
    }

    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    @Suppress("DEPRECATION")
    private fun getCellularNetworkType(): NetworkType {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return when (telephonyManager.dataNetworkType) {
            TelephonyManager.NETWORK_TYPE_NR -> NetworkType.CELLULAR_5G
            TelephonyManager.NETWORK_TYPE_LTE -> NetworkType.CELLULAR_4G
            TelephonyManager.NETWORK_TYPE_HSPA,
            TelephonyManager.NETWORK_TYPE_HSPAP,
            TelephonyManager.NETWORK_TYPE_UMTS -> NetworkType.CELLULAR_3G
            else -> NetworkType.CELLULAR_UNKNOWN
        }
    }
}