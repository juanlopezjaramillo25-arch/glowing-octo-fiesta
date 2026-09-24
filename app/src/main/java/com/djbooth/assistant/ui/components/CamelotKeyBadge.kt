package com.djbooth.assistant.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.djbooth.assistant.domain.CamelotEngine
import com.djbooth.assistant.ui.theme.getCamelotKeyColor

@Composable
fun CamelotKeyBadge(
    key: String,
    modifier: Modifier = Modifier,
    showMusicalKey: Boolean = true
) {
    val keyColor = getCamelotKeyColor(key)
    val musicalKey = CamelotEngine.getMusicalKeyName(key)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(keyColor.copy(alpha = 0.20f))
            .border(1.5.dp, keyColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = key,
                color = keyColor,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                fontSize = 15.sp
            )
            if (showMusicalKey && musicalKey != key) {
                Text(
                    text = " ($musicalKey)",
                    color = Color.White.copy(alpha = 0.85f),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}
