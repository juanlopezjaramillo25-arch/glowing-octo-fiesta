package com.djbooth.assistant.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.djbooth.assistant.data.model.RecommendationResult
import com.djbooth.assistant.data.model.SetIntent
import com.djbooth.assistant.data.model.Track
import com.djbooth.assistant.data.scanner.DemoLibraryProvider
import com.djbooth.assistant.data.scanner.MediaMetadataScanner
import com.djbooth.assistant.domain.RecommendationEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DJDeckViewModel : ViewModel() {

    private val _library = MutableStateFlow<List<Track>>(emptyList())
    val library: StateFlow<List<Track>> = _library.asStateFlow()

    private val _currentTrack = MutableStateFlow<Track?>(null)
    val currentTrack: StateFlow<Track?> = _currentTrack.asStateFlow()

    private val _crowdEnergy = MutableStateFlow(7)
    val crowdEnergy: StateFlow<Int> = _crowdEnergy.asStateFlow()

    private val _setIntent = MutableStateFlow(SetIntent.SUSTAIN_ENERGY)
    val setIntent: StateFlow<SetIntent> = _setIntent.asStateFlow()

    private val _recommendations = MutableStateFlow<List<RecommendationResult>>(emptyList())
    val recommendations: StateFlow<List<RecommendationResult>> = _recommendations.asStateFlow()

    private val _playbackSeconds = MutableStateFlow(0)
    val playbackSeconds: StateFlow<Int> = _playbackSeconds.asStateFlow()

    private val _isPlaying = MutableStateFlow(true)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _statusMessage = MutableStateFlow("Biblioteca cargada con éxito.")
    val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

    private var playbackJob: Job? = null

    init {
        loadDemoLibrary()
        startTimer()
    }

    fun loadDemoLibrary() {
        val demos = DemoLibraryProvider.getDemoTracks()
        _library.value = demos
        if (_currentTrack.value == null && demos.isNotEmpty()) {
            _currentTrack.value = demos[0]
            _playbackSeconds.value = 120 // Simular que va a mitad del tema (02:00)
        }
        recalculateRecommendations()
    }

    fun setCrowdEnergy(energy: Int) {
        _crowdEnergy.value = energy.coerceIn(1, 10)
        recalculateRecommendations()
    }

    fun setSetIntent(intent: SetIntent) {
        _setIntent.value = intent
        recalculateRecommendations()
    }

    fun selectTrack(track: Track) {
        _currentTrack.value = track
        _playbackSeconds.value = 0
        _statusMessage.value = "Sonando ahora: ${track.title} - ${track.artist}"
        recalculateRecommendations()
    }

    fun togglePlayback() {
        _isPlaying.value = !_isPlaying.value
    }

    fun scanLocalFiles(context: Context, uris: List<Uri>) {
        viewModelScope.launch {
            _isScanning.value = true
            _statusMessage.value = "Analizando ${uris.size} archivos de audio..."

            val newTracks = mutableListOf<Track>()
            uris.forEachIndexed { index, uri ->
                val track = MediaMetadataScanner.scanAudioFile(
                    context = context,
                    uri = uri,
                    fallbackName = uri.lastPathSegment ?: "Track #${index + 1}"
                )
                newTracks.add(track)
            }

            val updatedList = _library.value + newTracks
            _library.value = updatedList
            _isScanning.value = false
            _statusMessage.value = "¡${newTracks.size} temas importados! Total en biblioteca: ${updatedList.size}"

            if (_currentTrack.value == null && newTracks.isNotEmpty()) {
                selectTrack(newTracks[0])
            } else {
                recalculateRecommendations()
            }
        }
    }

    private fun recalculateRecommendations() {
        val current = _currentTrack.value ?: return
        val top = RecommendationEngine.getTopRecommendations(
            currentTrack = current,
            library = _library.value,
            crowdEnergy = _crowdEnergy.value,
            setIntent = _setIntent.value,
            topCount = 3
        )
        _recommendations.value = top
    }

    private fun startTimer() {
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                if (_isPlaying.value) {
                    val current = _currentTrack.value
                    if (current != null) {
                        if (_playbackSeconds.value < current.durationSeconds) {
                            _playbackSeconds.value += 1
                        } else {
                            // Reiniciar o pasar al siguiente si terminó
                            _playbackSeconds.value = 0
                        }
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        playbackJob?.cancel()
    }
}
