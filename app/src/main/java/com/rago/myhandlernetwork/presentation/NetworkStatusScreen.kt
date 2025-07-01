package com.rago.myhandlernetwork.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rago.myhandlernetwork.data.model.NetworkStatusUiState
import com.rago.myhandlernetwork.data.model.NetworkType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkStatusScreen(
    viewModel: NetworkStatusViewModel = hiltViewModel()
) {
    val state by viewModel.networkStatusUiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Estado de la Red (MVVM)") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (val currentState = state) {
                is NetworkStatusUiState.Initializing -> {
                    CircularProgressIndicator()
                    Text(text = "Inicializando...", modifier = Modifier.padding(top = 8.dp))
                }
                is NetworkStatusUiState.Connected -> {
                    NetworkIndicator(
                        icon = getIconForNetworkType(currentState.networkType),
                        label = "Conectado",
                        description = "Tipo de red: ${getLabelForNetworkType(currentState.networkType)}",
                        color = Color(0xFF388E3C)
                    )
                }
                is NetworkStatusUiState.Disconnected -> {
                    NetworkIndicator(
                        icon = Icons.Default.CloudOff,
                        label = "Desconectado",
                        description = currentState.reason,
                        color = Color(0xFFD32F2F)
                    )
                }
            }
        }
    }
}

@Composable
private fun NetworkIndicator(icon: ImageVector, label: String, description: String, color: Color) {
    Icon(
        imageVector = icon,
        contentDescription = label,
        tint = color,
        modifier = Modifier.size(80.dp)
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = label,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = color
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = description,
        fontSize = 16.sp,
        color = MaterialTheme.colorScheme.onSurface
    )
}

private fun getLabelForNetworkType(type: NetworkType): String {
    return when (type) {
        NetworkType.WIFI -> "WiFi"
        NetworkType.CELLULAR_5G -> "5G"
        NetworkType.CELLULAR_4G -> "4G (LTE)"
        NetworkType.CELLULAR_3G -> "3G"
        NetworkType.CELLULAR_UNKNOWN -> "Celular (Desconocido)"
        NetworkType.UNKNOWN -> "Desconocido"
    }
}

private fun getIconForNetworkType(type: NetworkType): ImageVector {
    return when (type) {
        NetworkType.WIFI -> Icons.Default.Wifi
        NetworkType.CELLULAR_5G -> Icons.Default.SignalCellularAlt
        NetworkType.CELLULAR_4G -> Icons.Default.SignalCellular4Bar
        NetworkType.CELLULAR_3G -> Icons.Default.SignalCellularAlt2Bar
        else -> Icons.Default.SignalCellularConnectedNoInternet0Bar
    }
}