package com.djbooth.assistant.ui.viewmodel

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.djbooth.assistant.data.model.RecommendationResult
import com.djbooth.assistant.data.model.SetIntent
import com.djbooth.assistant.data.model.Track
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

    private val _playlistQueue = MutableStateFlow<List<Track>>(emptyList())
    val playlistQueue: StateFlow<List<Track>> = _playlistQueue.asStateFlow()

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

    fun addToQueue(track: Track) {
        if (!_playlistQueue.value.any { it.id == track.id }) {
            val updated = _playlistQueue.value + track
            _playlistQueue.value = updated
            _statusMessage.value = "Añadido a la cola auto: ${track.title}"
        }
    }

    fun removeFromQueue(track: Track) {
        val updated = _playlistQueue.value.filter { it.id != track.id }
        _playlistQueue.value = updated
    }

    private fun playNextFromQueue(context: Context) {
        val queue = _playlistQueue.value
        if (queue.isNotEmpty()) {
            val nextTrack = queue[0]
            _playlistQueue.value = queue.drop(1)
            selectTrack(context, nextTrack, startFromSeconds = 0)
        }
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
                // Auto-reproducir siguiente tema de la cola si existe
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
    }
}
