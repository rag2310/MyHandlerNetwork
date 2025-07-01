package com.rago.myhandlernetwork.data.utils

import com.rago.myhandlernetwork.data.model.NetworkStatusUiState
import kotlinx.coroutines.flow.Flow

interface INetworkMonitor {
    fun observeNetworkStatus(): Flow<NetworkStatusUiState>
}