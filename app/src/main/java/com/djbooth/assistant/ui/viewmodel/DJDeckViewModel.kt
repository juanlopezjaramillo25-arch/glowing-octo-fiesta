package com.djbooth.assistant.ui.viewmodel

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.djbooth.assistant.data.model.QueuedTrack
import com.djbooth.assistant.data.model.RecommendationResult
import com.djbooth.assistant.data.model.SetIntent
import com.djbooth.assistant.data.model.Track
import com.djbooth.assistant.data.scanner.MediaMetadataScanner
import com.djbooth.assistant.domain.CueAudioEngine
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

    private val _playlistQueue = MutableStateFlow<List<QueuedTrack>>(emptyList())
    val playlistQueue: StateFlow<List<QueuedTrack>> = _playlistQueue.asStateFlow()

    private val _crowdEnergy = MutableStateFlow(7)
    val crowdEnergy: StateFlow<Int> = _crowdEnergy.asStateFlow()

    private val _setIntent = MutableStateFlow(SetIntent.SUSTAIN_ENERGY)
    val setIntent: StateFlow<SetIntent> = _setIntent.asStateFlow()

    private val _recommendations = MutableStateFlow<List<RecommendationResult>>(emptyList())
    val recommendations: StateFlow<List<RecommendationResult>> = _recommendations.asStateFlow()

    private val _playbackSeconds = MutableStateFlow(0)
    val playbackSeconds: StateFlow<Int> = _playbackSeconds.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _statusMessage = MutableStateFlow("Importa tu carpeta de música para comenzar.")
    val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

    // MODO CUE SPLIT MONO L/R (ESTÁNDAR DJ APPS)
    private val _isSplitMonoMode = MutableStateFlow(true)
    val isSplitMonoMode: StateFlow<Boolean> = _isSplitMonoMode.asStateFlow()

    private val cueEngine = CueAudioEngine()
    private val _isCuePlaying = MutableStateFlow(false)
    val isCuePlaying: StateFlow<Boolean> = _isCuePlaying.asStateFlow()

    private val _cueTrackId = MutableStateFlow<String?>(null)
    val cueTrackId: StateFlow<String?> = _cueTrackId.asStateFlow()

    private var mediaPlayer: MediaPlayer? = null
    private var syncJob: Job? = null

    fun setCrowdEnergy(energy: Int) {
        _crowdEnergy.value = energy.coerceIn(1, 10)
        recalculateRecommendations()
    }

    fun setSetIntent(intent: SetIntent) {
        _setIntent.value = intent
        recalculateRecommendations()
    }

    fun toggleSplitMonoMode() {
        val newMode = !_isSplitMonoMode.value
        _isSplitMonoMode.value = newMode
        mediaPlayer?.let { player ->
            try {
                if (newMode) {
                    player.setVolume(1.0f, 0.0f) // Izquierdo Master
                } else {
                    player.setVolume(1.0f, 1.0f) // Stereo Normal
                }
            } catch (ignored: Exception) {}
        }
        _statusMessage.value = if (newMode) "Modo DJ Split L/R activado (Izquierda: Master | Derecha: CUE Audífonos)" else "Modo Stereo normal activado"
    }

    fun selectTrack(context: Context, track: Track, startFromSeconds: Int = 0) {
        _currentTrack.value = track
        _statusMessage.value = "Sonando: ${track.title} - ${track.artist}"
        recalculateRecommendations()
        playTrackAudio(context, track, startFromSeconds)
    }

    fun togglePlayback(context: Context) {
        val player = mediaPlayer
        if (player != null) {
            if (player.isPlaying) {
                player.pause()
                _isPlaying.value = false
            } else {
                player.start()
                _isPlaying.value = true
                startSyncJob()
            }
        } else {
            val track = _currentTrack.value
            if (track != null) {
                playTrackAudio(context, track, _playbackSeconds.value)
            }
        }
    }

    fun seekTo(seconds: Int) {
        val track = _currentTrack.value ?: return
        val clampedSec = seconds.coerceIn(0, track.durationSeconds)
        _playbackSeconds.value = clampedSec
        mediaPlayer?.let { player ->
            try {
                player.seekTo(clampedSec * 1000)
            } catch (ignored: Exception) {}
        }
    }

    fun addToQueue(track: Track, startFromPeak: Boolean = false) {
        if (!_playlistQueue.value.any { it.track.id == track.id }) {
            val queued = QueuedTrack(track = track, startFromPeak = startFromPeak)
            val updated = _playlistQueue.value + queued
            _playlistQueue.value = updated
            val startText = if (startFromPeak) "en Peak" else "en Intro"
            _statusMessage.value = "Añadido a la cola auto ($startText): ${track.title}"
        }
    }

    fun toggleQueueTrackPeakMode(trackId: String) {
        val updated = _playlistQueue.value.map { item ->
            if (item.track.id == trackId) {
                item.copy(startFromPeak = !item.startFromPeak)
            } else {
                item
            }
        }
        _playlistQueue.value = updated
    }

    fun removeFromQueue(trackId: String) {
        val updated = _playlistQueue.value.filter { it.track.id != trackId }
        _playlistQueue.value = updated
    }

    fun reorderQueue(fromIndex: Int, toIndex: Int) {
        val list = _playlistQueue.value.toMutableList()
        if (fromIndex in list.indices && toIndex in list.indices) {
            val item = list.removeAt(fromIndex)
            list.add(toIndex, item)
            _playlistQueue.value = list
        }
    }

    fun clearQueue() {
        _playlistQueue.value = emptyList()
    }

    fun playNextFromQueue(context: Context) {
        val queue = _playlistQueue.value
        if (queue.isNotEmpty()) {
            val nextItem = queue[0]
            _playlistQueue.value = queue.drop(1)
            val startSec = if (nextItem.startFromPeak) nextItem.track.peakStartSeconds else 0
            selectTrack(context, nextItem.track, startFromSeconds = startSec)
        }
    }

    fun toggleCuePreview(context: Context, track: Track, startFromPeak: Boolean = false) {
        if (_isCuePlaying.value && _cueTrackId.value == track.id) {
            cueEngine.stopCuePreview()
            _isCuePlaying.value = false
            _cueTrackId.value = null
            _statusMessage.value = "Pre-escucha CUE detenida."
        } else {
            _statusMessage.value = "Pre-escuchando CUE: ${track.title}"
            cueEngine.playCuePreview(context, track, startFromPeak, _isSplitMonoMode.value) { playing ->
                _isCuePlaying.value = playing
                _cueTrackId.value = if (playing) track.id else null
            }
        }
    }

    fun stopCuePreview() {
        cueEngine.stopCuePreview()
        _isCuePlaying.value = false
        _cueTrackId.value = null
    }

    private fun playTrackAudio(context: Context, track: Track, startFromSeconds: Int = 0) {
        stopAudio()
        try {
            val player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
            }

            if (_isSplitMonoMode.value) {
                // Master sale por el Canal Izquierdo (1.0f, 0.0f)
                player.setVolume(1.0f, 0.0f)
            } else {
                player.setVolume(1.0f, 1.0f)
            }

            if (track.filePath != null) {
                player.setDataSource(context, Uri.parse(track.filePath))
                player.prepareAsync()
                player.setOnPreparedListener { mp ->
                    if (startFromSeconds > 0) {
                        mp.seekTo(startFromSeconds * 1000)
                    }
                    mp.start()
                    _isPlaying.value = true
                    _playbackSeconds.value = startFromSeconds
                    startSyncJob()
                }
            } else {
                _isPlaying.value = true
                _playbackSeconds.value = startFromSeconds
                startSyncJobSimulated(track.durationSeconds)
            }

            player.setOnCompletionListener {
                _isPlaying.value = false
                _playbackSeconds.value = 0
                if (_playlistQueue.value.isNotEmpty()) {
                    playNextFromQueue(context)
                }
            }

            mediaPlayer = player
        } catch (e: Exception) {
            e.printStackTrace()
            _isPlaying.value = true
            _playbackSeconds.value = startFromSeconds
            startSyncJobSimulated(track.durationSeconds)
        }
    }

    private fun startSyncJob() {
        syncJob?.cancel()
        syncJob = viewModelScope.launch {
            while (_isPlaying.value) {
                val mp = mediaPlayer
                if (mp != null && mp.isPlaying) {
                    _playbackSeconds.value = mp.currentPosition / 1000
                }
                delay(500)
            }
        }
    }

    private fun startSyncJobSimulated(maxDuration: Int) {
        syncJob?.cancel()
        syncJob = viewModelScope.launch {
            while (_isPlaying.value) {
                delay(1000)
                if (_isPlaying.value) {
                    if (_playbackSeconds.value < maxDuration) {
                        _playbackSeconds.value += 1
                    } else {
                        _playbackSeconds.value = 0
                        _isPlaying.value = false
                    }
                }
            }
        }
    }

    private fun stopAudio() {
        syncJob?.cancel()
        mediaPlayer?.let { player ->
            try {
                if (player.isPlaying) {
                    player.stop()
                }
                player.release()
            } catch (ignored: Exception) {}
        }
        mediaPlayer = null
        _isPlaying.value = false
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
                selectTrack(context, newTracks[0])
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

    override fun onCleared() {
        super.onCleared()
        stopAudio()
        stopCuePreview()
    }
}
