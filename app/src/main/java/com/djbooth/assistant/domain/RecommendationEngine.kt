package com.djbooth.assistant.domain

import com.djbooth.assistant.data.model.RecommendationResult
import com.djbooth.assistant.data.model.SetIntent
import com.djbooth.assistant.data.model.Track
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

object RecommendationEngine {

    /**
     * Recommends the TOP 3 tracks from the library matching current playing track,
     * crowd energy (1..10) and set intent.
     */
    fun getTopRecommendations(
        currentTrack: Track,
        library: List<Track>,
        crowdEnergy: Int,
        setIntent: SetIntent,
        topCount: Int = 3
    ): List<RecommendationResult> {
        if (library.isEmpty()) return emptyList()

        // 1. Calcular energía objetivo
        val targetEnergy = (crowdEnergy + setIntent.energyOffset).coerceIn(1, 10)

        val candidates = library.filter { it.id != currentTrack.id }

        val scoredResults = candidates.map { candidate ->
            // A. Evaluación Armónica
            val harmonicMatch = CamelotEngine.evaluateCompatibility(currentTrack.key, candidate.key)

            // B. Evaluación de Tempo (BPM)
            val bpmDiffRatio = abs(candidate.bpm - currentTrack.bpm) / max(1.0, currentTrack.bpm)
            val bpmDiffPercent = bpmDiffRatio * 100.0

            // Max variación tolerable ideal: +- 5%
            val tempoScore = when {
                bpmDiffPercent <= 2.0 -> 1.0
                bpmDiffPercent <= 5.0 -> 0.85
                bpmDiffPercent <= 8.0 -> 0.60
                bpmDiffPercent <= 12.0 -> 0.35
                else -> 0.10
            }

            // C. Evaluación de Nivel de Energía
            val energyDiff = abs(candidate.energyLevel - targetEnergy)
            val energyScore = max(0.0, 1.0 - (energyDiff / 9.0))

            // D. Score Total Ponderado (40% Armonía, 35% Tempo, 25% Energía)
            val totalScore = (harmonicMatch.score * 0.40) +
                             (tempoScore * 0.35) +
                             (energyScore * 0.25)

            val mixPointInfo = MixPointCalculator.calculateOutroMixPoint(currentTrack)

            RecommendationResult(
                track = candidate,
                overallScorePercent = (totalScore * 100).roundToInt(),
                harmonicMatchPercent = (harmonicMatch.score * 100).roundToInt(),
                tempoMatchPercent = (tempoScore * 100).roundToInt(),
                energyMatchPercent = (energyScore * 100).roundToInt(),
                harmonicCompatibilityLabel = harmonicMatch.label,
                bpmDiffPercent = bpmDiffPercent,
                suggestedMixPointSeconds = mixPointInfo.mixPointSeconds,
                suggestedMixPointText = mixPointInfo.mixPointText,
                phraseBars = mixPointInfo.phraseBars
            )
        }

        // Ordenar descendentemente por score total y tomar TOP 3
        return scoredResults
            .sortedByDescending { it.overallScorePercent }
            .take(topCount)
    }
}
