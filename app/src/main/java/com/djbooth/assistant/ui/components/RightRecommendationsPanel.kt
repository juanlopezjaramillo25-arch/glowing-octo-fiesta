package com.djbooth.assistant.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.djbooth.assistant.data.model.QueuedTrack
import com.djbooth.assistant.data.model.RecommendationResult
import com.djbooth.assistant.data.model.Track
import com.djbooth.assistant.ui.theme.DJCardSurface
import com.djbooth.assistant.ui.theme.DJPanelBackground
import com.djbooth.assistant.ui.theme.NeonAmber
import com.djbooth.assistant.ui.theme.NeonCyan
import com.djbooth.assistant.ui.theme.NeonEmerald
import com.djbooth.assistant.ui.theme.NeonMagenta
import com.djbooth.assistant.ui.theme.TextPrimary
import com.djbooth.assistant.ui.theme.TextSecondary

@Composable
fun RightRecommendationsPanel(
    recommendations: List<RecommendationResult>,
    playlistQueue: List<QueuedTrack>,
    isCuePlaying: Boolean,
    cueTrackId: String?,
    onSelectTrack: (Track) -> Unit,
    onSelectTrackFromPeak: (Track) -> Unit,
    onAddToQueue: (Track, Boolean) -> Unit,
    onToggleCuePreview: (Track, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(12.dp))
            .background(DJPanelBackground)
            .border(1.dp, Color(0xFF2B3148), RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Sugerencias",
                        tint = NeonEmerald,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Text(
                        text = "TOP 3 RECOMENDACIONES",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (playlistQueue.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(NeonMagenta.copy(alpha = 0.2f))
                                .border(1.dp, NeonMagenta, RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Cola: ${playlistQueue.size} temas",
                                color = NeonMagenta,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(NeonEmerald.copy(alpha = 0.2f))
                            .border(1.dp, NeonEmerald, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "±5% BPM & Camelot",
                            color = NeonEmerald,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (recommendations.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.QueueMusic,
                            contentDescription = "Sin datos",
                            tint = TextSecondary,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Importa canciones locales para calcular sugerencias",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(recommendations) { index, item ->
                        val isThisCuePlaying = isCuePlaying && cueTrackId == item.track.id
                        RecommendationCard(
                            rank = index + 1,
                            result = item,
                            isInQueue = playlistQueue.any { it.track.id == item.track.id },
                            isCuePlaying = isThisCuePlaying,
                            onLoadToDeck = { onSelectTrack(item.track) },
                            onLoadFromPeak = { onSelectTrackFromPeak(item.track) },
                            onAddToQueueIntro = { onAddToQueue(item.track, false) },
                            onAddToQueuePeak = { onAddToQueue(item.track, true) },
                            onToggleCue = { onToggleCuePreview(item.track, false) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecommendationCard(
    rank: Int,
    result: RecommendationResult,
    isInQueue: Boolean,
    isCuePlaying: Boolean,
    onLoadToDeck: () -> Unit,
    onLoadFromPeak: () -> Unit,
    onAddToQueueIntro: () -> Unit,
    onAddToQueuePeak: () -> Unit,
    onToggleCue: () -> Unit
) {
    val rankColor = when (rank) {
        1 -> NeonEmerald
        2 -> NeonCyan
        3 -> NeonAmber
        else -> TextSecondary
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(DJCardSurface)
            .border(1.dp, if (isCuePlaying) NeonMagenta else rankColor.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(rankColor.copy(alpha = 0.25f))
                            .border(1.dp, rankColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "#$rank",
                            color = rankColor,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = result.track.title,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = result.track.artist,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(rankColor.copy(alpha = 0.2f))
                        .border(1.5.dp, rankColor, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "${result.overallScorePercent}% MATCH",
                        color = rankColor,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Badges
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                CamelotKeyBadge(key = result.track.key, showMusicalKey = false)

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF262C40))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "${result.track.bpm.toInt()} BPM (${String.format("%+.1f", result.bpmDiffPercent)}%)",
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(NeonCyan.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = result.harmonicCompatibilityLabel,
                        color = NeonCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Punto de mezcla
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF141724))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = "Punto Mezcla",
                    tint = NeonMagenta,
                    modifier = Modifier.size(14.dp).padding(end = 4.dp)
                )
                Text(
                    text = "PUNTO DE MEZCLA: ",
                    color = NeonMagenta,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
                Text(
                    text = result.suggestedMixPointText,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // BOTONES DE ACCIÓN COMPLETO: Cargar Deck | Peak | CUE Audífonos | + Cola (Intro/Peak)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // CUE PRE-ESCUCHA AUDÍFONOS (AUX/BLUETOOTH)
                Button(
                    onClick = onToggleCue,
                    colors = ButtonDefaults.buttonColors(containerColor = if (isCuePlaying) NeonMagenta else Color(0xFF2B3148)),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Headphones,
                        contentDescription = "CUE",
                        tint = if (isCuePlaying) Color.White else NeonCyan,
                        modifier = Modifier.size(12.dp).padding(end = 2.dp)
                    )
                    Text(
                        text = if (isCuePlaying) "CUE ON" else "CUE",
                        color = if (isCuePlaying) Color.White else TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }

                // Cargar a Deck Intro
                Button(
                    onClick = onLoadToDeck,
                    colors = ButtonDefaults.buttonColors(containerColor = rankColor),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(28.dp).weight(1f)
                ) {
                    Text(
                        text = "Deck",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }

                // Iniciar en Peak (Drop)
                OutlinedButton(
                    onClick = onLoadFromPeak,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonAmber),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(28.dp).weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Peak",
                        tint = NeonAmber,
                        modifier = Modifier.size(11.dp).padding(end = 2.dp)
                    )
                    Text(
                        text = "Peak",
                        color = NeonAmber,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }

                // AÑADIR A COLA INTRO
                Button(
                    onClick = onAddToQueueIntro,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Cola",
                        tint = Color.Black,
                        modifier = Modifier.size(11.dp)
                    )
                    Text(text = "Intro", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                }

                // AÑADIR A COLA PEAK
                Button(
                    onClick = onAddToQueuePeak,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonAmber),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Cola Peak",
                        tint = Color.Black,
                        modifier = Modifier.size(11.dp)
                    )
                    Text(text = "Peak", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                }
            }
        }
    }
}
