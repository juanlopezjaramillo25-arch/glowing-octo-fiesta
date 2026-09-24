package com.djbooth.assistant.data.model

data class RecommendationResult(
    val track: Track,
    val overallScorePercent: Int, // 0..100
    val harmonicMatchPercent: Int,
    val tempoMatchPercent: Int,
    val energyMatchPercent: Int,
    val harmonicCompatibilityLabel: String,
    val bpmDiffPercent: Double,
    val suggestedMixPointSeconds: Int,
    val suggestedMixPointText: String,
    val phraseBars: Int = 32
)
