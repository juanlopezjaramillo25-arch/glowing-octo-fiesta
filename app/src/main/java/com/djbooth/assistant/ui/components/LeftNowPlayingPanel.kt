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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
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
import com.djbooth.assistant.data.model.QueuedTrack
import com.djbooth.assistant.data.model.Track
import com.djbooth.assistant.domain.MixPointCalculator
import com.djbooth.assistant.ui.theme.DJCardSurface
import com.djbooth.assistant.ui.theme.DJPanelBackground
import com.djbooth.assistant.ui.theme.NeonAmber
import com.djbooth.assistant.ui.theme.NeonCyan
import com.djbooth.assistant.ui.theme.NeonEmerald
import com.djbooth.assistant.ui.theme.NeonMagenta
import com.djbooth.assistant.ui.theme.TextPrimary
import com.djbooth.assistant.ui.theme.TextSecondary
import kotlin.math.roundToInt

@Composable
fun LeftNowPlayingPanel(
    currentTrack: Track?,
    library: List<Track>,
    queue: List<QueuedTrack>,
    playbackSeconds: Int,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    onSelectTrack: (Track) -> Unit,
    onSeekPosition: (Int) -> Unit,
    onJumpToPeak: () -> Unit,
    onOpenQueueDialog: () -> Unit,
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
            .padding(14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CoolDJMascot()
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "NOW PLAYING",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // BOTÓN VER Y GESTIONAR COLA
                        OutlinedButton(
                            onClick = onOpenQueueDialog,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonMagenta),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.QueueMusic,
                                contentDescription = "Cola",
                                tint = NeonMagenta,
                                modifier = Modifier.size(14.dp).padding(end = 2.dp)
                            )
                            Text(
                                text = "COLA (${queue.size})",
                                color = NeonMagenta,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Box {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(DJCardSurface)
                                    .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                    .clickable { dropdownExpanded = true }
                                    .padding(horizontal = 6.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Temas",
                                        color = NeonCyan,
                                        fontSize = 11.sp,
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
                }

                Spacer(modifier = Modifier.height(12.dp))

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

                    // Tarjeta Principal
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DJCardSurface)
                            .border(1.dp, if (isMixPointReached) NeonMagenta else Color(0xFF2B3148), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
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
                                        .size(56.dp)
                                        .rotate(if (isPlaying) rotation else 0f)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = currentTrack.title,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = currentTrack.artist,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 13.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(NeonEmerald.copy(alpha = 0.2f))
                                            .border(1.dp, NeonEmerald, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "${currentTrack.bpm.toInt()} BPM",
                                            color = NeonEmerald,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 12.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    CamelotKeyBadge(key = currentTrack.key)
                                }
                            }

                            IconButton(
                                onClick = onTogglePlay,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(NeonCyan)
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play/Pause",
                                    tint = Color.Black,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Temporizadores + SLIDER TÁCTIL DE BÚSQUEDA
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(DJCardSurface)
                            .padding(10.dp)
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
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = String.format(" %02d:%02d", elapsedMin, elapsedSec),
                                    color = TextPrimary,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }

                            Button(
                                onClick = onJumpToPeak,
                                colors = ButtonDefaults.buttonColors(containerColor = NeonAmber),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.height(26.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = "Peak",
                                    tint = Color.Black,
                                    modifier = Modifier.size(13.dp).padding(end = 2.dp)
                                )
                                Text(
                                    text = "Saltar a Peak (${currentTrack.formattedPeakStart})",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }

                            Text(
                                text = String.format("-%02d:%02d ", remMin, remSec),
                                color = NeonMagenta,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Slider(
                            value = playbackSeconds.toFloat().coerceIn(0f, currentTrack.durationSeconds.toFloat()),
                            onValueChange = { onSeekPosition(it.roundToInt()) },
                            valueRange = 0f..currentTrack.durationSeconds.toFloat(),
                            colors = SliderDefaults.colors(
                                thumbColor = if (isMixPointReached) NeonMagenta else NeonCyan,
                                activeTrackColor = if (isMixPointReached) NeonMagenta else NeonCyan,
                                inactiveTrackColor = Color(0xFF262B3D)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // ALERTA DE PUNTO DE MEZCLA
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
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isMixPointReached) Icons.Default.Warning else Icons.Default.GraphicEq,
                                contentDescription = "Mezcla",
                                tint = if (isMixPointReached) NeonMagenta else NeonCyan,
                                modifier = Modifier
                                    .size(22.dp)
                                    .padding(end = 6.dp)
                            )

                            Column {
                                Text(
                                    text = if (isMixPointReached) "¡SOLTAR SIGUIENTE TEMA AHORA!" else "OUTRO CALCULADO",
                                    color = if (isMixPointReached) NeonMagenta else NeonCyan,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = mixPointInfo.mixPointText,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Importa canciones locales abajo para comenzar",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
