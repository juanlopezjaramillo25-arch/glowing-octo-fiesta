package com.djbooth.assistant.domain

import com.djbooth.assistant.data.model.Track
import kotlin.math.max
import kotlin.math.roundToInt

object MixPointCalculator {

    data class MixPointInfo(
        val mixPointSeconds: Int,
        val formattedMixPoint: String,
        val phraseBars: Int,
        val phraseDurationSeconds: Int,
        val mixPointText: String
    )

    fun calculateOutroMixPoint(currentTrack: Track, phraseBars: Int = 16): MixPointInfo {
        val bpm = max(60.0, currentTrack.bpm)
        val duration = max(30, currentTrack.durationSeconds)

        // 1 compás = 4 tiempos (beats)
        // Duración de 1 beat = 60.0 / BPM
        val secondsPerBeat = 60.0 / bpm
        val secondsPerBar = secondsPerBeat * 4.0
        val phraseDuration = (secondsPerBar * phraseBars).roundToInt()

        // El punto de mezcla se calcula al inicio de la frase del Outro (ej: 82% a 88% de la canción)
        val calculatedOutroStart = duration - phraseDuration
        val percentageOutroStart = (duration * 0.84).roundToInt()

        // Usamos el punto que dé un Outro cómodo para la mezcla de frase
        val finalMixPointSeconds = if (calculatedOutroStart > duration * 0.60) {
            calculatedOutroStart
        } else {
            percentageOutroStart
        }

        val mixMin = finalMixPointSeconds / 60
        val mixSec = finalMixPointSeconds % 60
        val formattedTime = String.format("%02d:%02d", mixMin, mixSec)

        val displayText = "Mezclar en min $formattedTime / Outro de $phraseBars compases"

        return MixPointInfo(
            mixPointSeconds = finalMixPointSeconds,
            formattedMixPoint = formattedTime,
            phraseBars = phraseBars,
            phraseDurationSeconds = phraseDuration,
            mixPointText = displayText
        )
    }
}
