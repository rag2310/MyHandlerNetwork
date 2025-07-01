package com.rago.myhandlernetwork.data.model

sealed interface NetworkStatusUiState {
    object Initializing : NetworkStatusUiState
    data class Connected(val networkType: NetworkType) : NetworkStatusUiState
    data class Disconnected(val reason: String) : NetworkStatusUiState
}

enum class NetworkType {
    WIFI,
    CELLULAR_5G,
    CELLULAR_4G,
    CELLULAR_3G,
    CELLULAR_UNKNOWN,
    UNKNOWN
}