package com.djbooth.assistant.data.model

data class QueuedTrack(
    val track: Track,
    val startFromPeak: Boolean = false
) {
    val startPositionLabel: String
        get() = if (startFromPeak) "Peak (${track.formattedPeakStart})" else "Intro (00:00)"
}
