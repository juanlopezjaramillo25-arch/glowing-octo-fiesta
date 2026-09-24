package com.djbooth.assistant.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.djbooth.assistant.ui.components.BottomLibraryBar
import com.djbooth.assistant.ui.components.LeftNowPlayingPanel
import com.djbooth.assistant.ui.components.RightRecommendationsPanel
import com.djbooth.assistant.ui.components.TopEnergyPanel
import com.djbooth.assistant.ui.theme.DJDarkBackground
import com.djbooth.assistant.ui.viewmodel.DJDeckViewModel

@Composable
fun DJMainScreen(viewModel: DJDeckViewModel) {
    val context = LocalContext.current

    val library by viewModel.library.collectAsState()
    val currentTrack by viewModel.currentTrack.collectAsState()
    val crowdEnergy by viewModel.crowdEnergy.collectAsState()
    val setIntent by viewModel.setIntent.collectAsState()
    val recommendations by viewModel.recommendations.collectAsState()
    val playbackSeconds by viewModel.playbackSeconds.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DJDarkBackground)
            .padding(12.dp)
    ) {
        // 1. PANEL SUPERIOR (Control de Energía)
        TopEnergyPanel(
            crowdEnergy = crowdEnergy,
            onEnergyChange = { viewModel.setCrowdEnergy(it) },
            currentIntent = setIntent,
            onIntentChange = { viewModel.setSetIntent(it) }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 2. FILA CENTRAL DIVIDIDA (Panel Izquierdo: Sonando | Panel Derecho: Recomendaciones)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            // PANEL IZQUIERDO: Canción Sonando Actual (45% ancho)
            LeftNowPlayingPanel(
                currentTrack = currentTrack,
                library = library,
                playbackSeconds = playbackSeconds,
                isPlaying = isPlaying,
                onTogglePlay = { viewModel.togglePlayback(context) },
                onSelectTrack = { viewModel.selectTrack(context, it) },
                modifier = Modifier.weight(0.45f)
            )

            Spacer(modifier = Modifier.width(10.dp))

            // PANEL DERECHO: Sugerencias Inteligentes (55% ancho)
            RightRecommendationsPanel(
                recommendations = recommendations,
                onSelectTrack = { viewModel.selectTrack(context, it) },
                modifier = Modifier.weight(0.55f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 3. PANEL INFERIOR (Gestión de Biblioteca)
        BottomLibraryBar(
            totalTracksCount = library.size,
            isScanning = isScanning,
            statusMessage = statusMessage,
            onImportAudioFiles = { uris -> viewModel.scanLocalFiles(context, uris) },
            onReloadDemo = { viewModel.loadDemoLibrary() }
        )
    }
}
