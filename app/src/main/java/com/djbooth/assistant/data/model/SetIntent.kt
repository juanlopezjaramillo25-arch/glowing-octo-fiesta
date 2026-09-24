package com.djbooth.assistant.data.model

enum class SetIntent(
    val title: String,
    val description: String,
    val energyOffset: Int
) {
    SUSTAIN_ENERGY(
        title = "Sostener Energía",
        description = "Mantiene el nivel del público uniforme",
        energyOffset = 0
    ),
    BOOST_PEAK(
        title = "Subir Intensidad / Peak",
        description = "Eleva la energía hacia un punto alto",
        energyOffset = 2
    ),
    COOL_DOWN(
        title = "Bajar / Descanso",
        description = "Transición suave hacia un tempo/energía menor",
        energyOffset = -2
    )
}
