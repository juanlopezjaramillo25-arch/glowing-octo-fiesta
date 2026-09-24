package com.djbooth.assistant.domain

data class HarmonicMatch(
    val score: Double, // 0.0 to 1.0
    val label: String,
    val isCompatible: Boolean
)

object CamelotEngine {

    private val CAMELOT_TO_MUSICAL = mapOf(
        "1A" to "Abm", "1B" to "B",
        "2A" to "Ebm", "2B" to "F#",
        "3A" to "Bbm", "3B" to "Db",
        "4A" to "Fm",  "4B" to "Ab",
        "5A" to "Cm",  "5B" to "Eb",
        "6A" to "Gm",  "6B" to "Bb",
        "7A" to "Dm",  "7B" to "F",
        "8A" to "Am",  "8B" to "C",
        "9A" to "Em",  "9B" to "G",
        "10A" to "Bm", "10B" to "D",
        "11A" to "F#m", "11B" to "A",
        "12A" to "C#m", "12B" to "E"
    )

    fun getMusicalKeyName(camelotKey: String): String {
        val cleanKey = camelotKey.trim().uppercase()
        return CAMELOT_TO_MUSICAL[cleanKey] ?: cleanKey
    }

    /**
     * Evaluates harmonic compatibility between current key and candidate key.
     */
    fun evaluateCompatibility(currentKey: String, candidateKey: String): HarmonicMatch {
        val current = parseKey(currentKey)
        val candidate = parseKey(candidateKey)

        if (current == null || candidate == null) {
            return HarmonicMatch(0.5, "Tonalidad desconocida", true)
        }

        val (cNum, cLetter) = current
        val (candNum, candLetter) = candidate

        val sameLetter = cLetter == candLetter
        val diffNum = (candNum - cNum + 12) % 12

        return when {
            // 1. Misma tonalidad (Perfect Match)
            diffNum == 0 && sameLetter -> {
                HarmonicMatch(1.0, "Match Perfecto (Misma tonalidad)", true)
            }
            // 2. Subida de Energía (+1 en la Rueda)
            diffNum == 1 && sameLetter -> {
                HarmonicMatch(0.95, "Subida de Energía (+1 Key)", true)
            }
            // 3. Transición Suave / Bajada (-1 en la Rueda)
            diffNum == 11 && sameLetter -> {
                HarmonicMatch(0.90, "Transición Suave (-1 Key)", true)
            }
            // 4. Relativo Mayor / Menor (Mismo número, cambio A <-> B)
            diffNum == 0 && !sameLetter -> {
                HarmonicMatch(0.85, "Relativo Mayor/Menor (Armonía directa)", true)
            }
            // 5. Diagonal Boost (+1 con cambio A/B)
            diffNum == 1 && !sameLetter -> {
                HarmonicMatch(0.75, "Modulación Diagonal Boost", true)
            }
            // 6. Diagonal Drop (-1 con cambio A/B)
            diffNum == 11 && !sameLetter -> {
                HarmonicMatch(0.70, "Modulación Diagonal Drop", true)
            }
            // 7. Salto de +2 (Energía de Impulso)
            diffNum == 2 && sameLetter -> {
                HarmonicMatch(0.65, "Salto de +2 Semitonos (Energía extra)", true)
            }
            // 8. No compatible directamente
            else -> {
                HarmonicMatch(0.25, "Incompatible armónicamente", false)
            }
        }
    }

    private fun parseKey(keyStr: String): Pair<Int, Char>? {
        val clean = keyStr.trim().uppercase()
        val regex = Regex("""^([1-9]|1[0-2])([AB])$""")
        val match = regex.find(clean) ?: return null
        val num = match.groupValues[1].toInt()
        val letter = match.groupValues[2][0]
        return Pair(num, letter)
    }
}
