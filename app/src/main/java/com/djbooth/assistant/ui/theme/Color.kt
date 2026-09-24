package com.djbooth.assistant.ui.theme

import androidx.compose.ui.graphics.Color

// DJ Booth Dark Base Palette
val DJDarkBackground = Color(0xFF090A0F)
val DJPanelBackground = Color(0xFF131622)
val DJCardSurface = Color(0xFF1D2132)
val DJCardBorder = Color(0xFF2E354F)

// High-Contrast Neon Highlights
val NeonCyan = Color(0xFF00E5FF)
val NeonMagenta = Color(0xFFFF0055)
val NeonEmerald = Color(0xFF00E676)
val NeonAmber = Color(0xFFFFB300)
val NeonPurple = Color(0xFFD500F9)

// Text Colors
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFF9098B5)
val TextMuted = Color(0xFF5C637F)

// Camelot Key Color Mapping
fun getCamelotKeyColor(key: String): Color {
    val clean = key.trim().uppercase()
    val num = clean.filter { it.isDigit() }.toIntOrNull() ?: 8
    return when (num) {
        1 -> Color(0xFF00E5FF) // Cyan
        2 -> Color(0xFF00B0FF) // Light Blue
        3 -> Color(0xFF2979FF) // Blue
        4 -> Color(0xFF651FFF) // Deep Purple
        5 -> Color(0xFFAA00FF) // Purple
        6 -> Color(0xFFF50057) // Pink
        7 -> Color(0xFFFF1744) // Red
        8 -> Color(0xFFFF5252) // Light Red
        9 -> Color(0xFFFF9100) // Orange
        10 -> Color(0xFFFFC400) // Yellow-Orange
        11 -> Color(0xFFFFEA00) // Yellow
        12 -> Color(0xFF76FF03) // Lime Green
        else -> NeonCyan
    }
}
