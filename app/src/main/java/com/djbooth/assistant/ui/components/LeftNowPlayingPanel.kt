package com.djbooth.assistant.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.djbooth.assistant.data.model.Track
import com.djbooth.assistant.domain.MixPointCalculator
import com.djbooth.assistant.ui.theme.DJCardSurface
import com.djbooth.assistant.ui.theme.DJPanelBackground
import com.djbooth.assistant.ui.theme.NeonCyan
import com.djbooth.assistant.ui.theme.NeonEmerald
import com.djbooth.assistant.ui.theme.NeonMagenta
import com.djbooth.assistant.ui.theme.TextPrimary
import com.djbooth.assistant.ui.theme.TextSecondary

@Composable
fun LeftNowPlayingPanel(
    currentTrack: Track?,
    library: List<Track>,
    playbackSeconds: Int,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    onSelectTrack: (Track) -> Unit,
    modifier: Modifier = Modifier
) {
    var dropdownExpanded by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "vinyl")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing)
        ),
        label = "vinyl_rotate"
    )

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(12.dp))
            .background(DJPanelBackground)
            .border(1.dp, Color(0xFF2B3148), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header: Selector de pista sonando
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "Deck Sonando",
                            tint = NeonCyan,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                        Text(
                            text = "DECK ACTUAL (NOW PLAYING)",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            letterSpacing = 1.sp
                        )
                    }

                    // Selector Dropdown para elegir cualquier tema
                    Box {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(DJCardSurface)
                                .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                .clickable { dropdownExpanded = true }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Cambiar tema",
                                    color = NeonCyan,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Selector",
                                    tint = NeonCyan
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false },
                            modifier = Modifier.background(DJCardSurface)
                        ) {
                            library.forEach { track ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "${track.title} - ${track.artist} (${track.bpm.toInt()} BPM / ${track.key})",
                                            color = TextPrimary,
                                            fontSize = 13.sp
                                        )
                                    },
                                    onClick = {
                                        onSelectTrack(track)
                                        dropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (currentTrack != null) {
                    val mixPointInfo = remember(currentTrack) {
                        MixPointCalculator.calculateOutroMixPoint(currentTrack)
                    }

                    val remainingSeconds = (currentTrack.durationSeconds - playbackSeconds).coerceAtLeast(0)
                    val elapsedMin = playbackSeconds / 60
                    val elapsedSec = playbackSeconds % 60
                    val remMin = remainingSeconds / 60
                    val remSec = remainingSeconds % 60

                    val isMixPointReached = playbackSeconds >= mixPointInfo.mixPointSeconds

                    // Tarjeta Principal del Tema Sonando
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DJCardSurface)
                            .border(1.dp, if (isMixPointReached) NeonMagenta else Color(0xFF2B3148), RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Vinilo Animado
                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black)
                                    .border(2.dp, NeonCyan, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Album,
                                    contentDescription = "Vinyl",
                                    tint = NeonCyan,
                                    modifier = Modifier
                                        .size(64.dp)
                                        .rotate(if (isPlaying) rotation else 0f)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            // Metadatos principales
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = currentTrack.title,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = currentTrack.artist,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // BPM Badge
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(NeonEmerald.copy(alpha = 0.2f))
                                            .border(1.dp, NeonEmerald, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "${currentTrack.bpm.toInt()} BPM",
                                            color = NeonEmerald,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 13.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    // Key Badge
                                    CamelotKeyBadge(key = currentTrack.key)
                                }
                            }

                            // Botón Play/Pause
                            IconButton(
                                onClick = onTogglePlay,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(NeonCyan)
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play/Pause",
                                    tint = Color.Black,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Temporizadores y Barra de Progreso
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(DJCardSurface)
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = "Tiempo",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = String.format(" TRANSCURRIDO: %02d:%02d", elapsedMin, elapsedSec),
                                    color = TextPrimary,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }

                            Text(
                                text = String.format("RESTANTE: -%02d:%02d", remMin, remSec),
                                color = NeonMagenta,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val progress = (playbackSeconds.toFloat() / currentTrack.durationSeconds.toFloat()).coerceIn(0f, 1f)

                        LinearProgressIndicator(
                            progress = progress,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (isMixPointReached) NeonMagenta else NeonCyan,
                            trackColor = Color(0xFF262B3D)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // ALERTA VISUAL DE PUNTO DE MEZCLA / OUTRO
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isMixPointReached) NeonMagenta.copy(alpha = 0.25f) else Color(0xFF191D2B))
                            .border(
                                width = if (isMixPointReached) 2.dp else 1.dp,
                                color = if (isMixPointReached) NeonMagenta else NeonCyan.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isMixPointReached) Icons.Default.Warning else Icons.Default.GraphicEq,
                                contentDescription = "Mezcla",
                                tint = if (isMixPointReached) NeonMagenta else NeonCyan,
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(end = 8.dp)
                            )

                            Column {
                                Text(
                                    text = if (isMixPointReached) "¡SOLTAR SIGUIENTE TEMA AHORA!" else "PUNTO DE OUTRO CALCULADO",
                                    color = if (isMixPointReached) NeonMagenta else NeonCyan,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = mixPointInfo.mixPointText,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                } else {
                    Text(text = "No hay ningún tema cargado", color = TextSecondary)
                }
            }
        }
    }
}
