package com.djbooth.assistant.data.scanner

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import com.djbooth.assistant.data.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

object MediaMetadataScanner {

    private val CAMELOT_KEYS = listOf(
        "1A", "1B", "2A", "2B", "3A", "3B", "4A", "4B",
        "5A", "5B", "6A", "6B", "7A", "7B", "8A", "8B",
        "9A", "9B", "10A", "10B", "11A", "11B", "12A", "12B"
    )

    /**
     * Extracts track metadata from Uri using MediaMetadataRetriever and filename heuristics.
     */
    suspend fun scanAudioFile(context: Context, uri: Uri, fallbackName: String = "Track"): Track = withContext(Dispatchers.IO) {
        val retriever = MediaMetadataRetriever()
        var title = fallbackName
        var artist = "Artista Desconocido"
        var durationSeconds = 210
        var bpm = 124.0
        var key = "8A"
        var genre = "Dance"

        try {
            retriever.setDataSource(context, uri)

            val metaTitle = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
            val metaArtist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
            val metaDuration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val metaGenre = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE)

            if (!metaTitle.isNull_or_blank()) title = metaTitle!!
            if (!metaArtist.isNull_or_blank()) artist = metaArtist!!
            if (!metaGenre.isNull_or_blank()) genre = metaGenre!!

            metaDuration?.toLongOrNull()?.let { millis ->
                durationSeconds = (millis / 1000).toInt()
            }

            // Filename / String pattern searching for BPM and Key
            val fullText = "$fallbackName $title $artist"

            val extractedBpm = extractBpmFromText(fullText)
            if (extractedBpm != null) {
                bpm = extractedBpm
            }

            val extractedKey = extractCamelotKeyFromText(fullText)
            if (extractedKey != null) {
                key = extractedKey
            } else {
                // Generar una clave basada en hash del título para evitar valores estáticos
                val keyIndex = ((title.hashCode().toLong() and 0x7FFFFFFF) % CAMELOT_KEYS.size.toLong()).toInt()
                key = CAMELOT_KEYS[keyIndex]
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                retriever.release()
            } catch (ignored: Exception) {}
        }

        val energyLevel = estimateEnergyLevel(bpm, durationSeconds)

        Track(
            id = UUID.randomUUID().toString(),
            title = title.removeSuffix(".mp3").removeSuffix(".wav").removeSuffix(".flac"),
            artist = artist,
            bpm = bpm,
            key = key,
            durationSeconds = durationSeconds,
            energyLevel = energyLevel,
            genre = genre,
            filePath = uri.toString()
        )
    }

    private fun CharSequence?.isNull_or_blank(): Boolean = this == null || this.isBlank()

    private fun extractBpmFromText(text: String): Double? {
        val bpmRegex = Regex("""\b(1[0-5][0-9]|9[0-9])(?:\.[0-9])?\s*(?:bpm)?\b""", RegexOption.IGNORE_CASE)
        val match = bpmRegex.find(text)
        return match?.groupValues?.get(1)?.toDoubleOrNull()
    }

    private fun extractCamelotKeyFromText(text: String): String? {
        val keyRegex = Regex("""\b(1[0-2]|[1-9])([ABab])\b""")
        val match = keyRegex.find(text) ?: return null
        val num = match.groupValues[1]
        val letter = match.groupValues[2].uppercase()
        return "$num$letter"
    }

    private fun estimateEnergyLevel(bpm: Double, durationSeconds: Int): Int {
        val bpmEnergy = ((bpm - 115.0) / 20.0 * 5.0 + 5.0).coerceIn(1.0, 10.0).toInt()
        return bpmEnergy
    }
}
