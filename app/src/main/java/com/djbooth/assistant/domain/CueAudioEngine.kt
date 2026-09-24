package com.djbooth.assistant.domain

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import com.djbooth.assistant.data.model.Track

class CueAudioEngine {

    private var cuePlayer: MediaPlayer? = null
    var isCuePlaying: Boolean = false
        private set

    var cueTrackId: String? = null
        private set

    fun playCuePreview(
        context: Context,
        track: Track,
        startFromPeak: Boolean = false,
        onStateChange: (Boolean) -> Unit
    ) {
        stopCuePreview()

        try {
            val player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
            }

            // Intentar enrutar a audífonos AUX o Bluetooth si están conectados
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
                val devices = audioManager?.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
                val headphoneDevice = devices?.firstOrNull {
                    it.type == AudioDeviceInfo.TYPE_WIRED_HEADPHONES ||
                    it.type == AudioDeviceInfo.TYPE_WIRED_HEADSET ||
                    it.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP ||
                    it.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO
                }
                if (headphoneDevice != null) {
                    player.setPreferredDevice(headphoneDevice)
                }
            }

            if (track.filePath != null) {
                player.setDataSource(context, Uri.parse(track.filePath))
                player.prepareAsync()
                player.setOnPreparedListener { mp ->
                    if (startFromPeak) {
                        mp.seekTo(track.peakStartSeconds * 1000)
                    }
                    mp.start()
                    isCuePlaying = true
                    cueTrackId = track.id
                    onStateChange(true)
                }
            } else {
                isCuePlaying = false
                cueTrackId = null
                onStateChange(false)
            }

            player.setOnCompletionListener {
                stopCuePreview()
                onStateChange(false)
            }

            cuePlayer = player
        } catch (e: Exception) {
            e.printStackTrace()
            stopCuePreview()
            onStateChange(false)
        }
    }

    fun stopCuePreview() {
        cuePlayer?.let { player ->
            try {
                if (player.isPlaying) {
                    player.stop()
                }
                player.release()
            } catch (ignored: Exception) {}
        }
        cuePlayer = null
        isCuePlaying = false
        cueTrackId = null
    }
}
