package com.djbooth.assistant.data.model

import kotlin.math.roundToInt

data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val bpm: Double,
    val key: String, // Camelot key e.g. "8A", "11B", "4A"
    val durationSeconds: Int,
    val energyLevel: Int, // 1..10
    val genre: String = "Electronic",
    val album: String = "Single",
    val filePath: String? = null
) {
    val formattedDuration: String
        get() {
            val minutes = durationSeconds / 60
            val seconds = durationSeconds % 60
            return String.format("%02d:%02d", minutes, seconds)
        }

    // Punto de Peak / Drop calculado (aproximadamente al 22% de la canción, ~30-45s)
    val peakStartSeconds: Int
        get() {
            val calculated = (durationSeconds * 0.22).roundToInt()
            return calculated.coerceIn(15, 60)
        }

    val formattedPeakStart: String
        get() {
            val min = peakStartSeconds / 60
            val sec = peakStartSeconds % 60
            return String.format("%02d:%02d", min, sec)
        }
}
