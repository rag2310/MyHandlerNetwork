package com.rago.myhandlernetwork.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rago.myhandlernetwork.data.model.NetworkStatusUiState
import com.rago.myhandlernetwork.data.utils.INetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class NetworkStatusViewModel @Inject constructor(
    networkMonitor: INetworkMonitor
) : ViewModel() {

    val networkStatusUiState: StateFlow<NetworkStatusUiState> =
        networkMonitor.observeNetworkStatus()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = NetworkStatusUiState.Initializing
            )
}