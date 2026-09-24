package com.djbooth.assistant.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.window.Dialog
import com.djbooth.assistant.data.model.QueuedTrack
import com.djbooth.assistant.ui.theme.DJCardSurface
import com.djbooth.assistant.ui.theme.DJPanelBackground
import com.djbooth.assistant.ui.theme.NeonAmber
import com.djbooth.assistant.ui.theme.NeonCyan
import com.djbooth.assistant.ui.theme.NeonEmerald
import com.djbooth.assistant.ui.theme.NeonMagenta
import com.djbooth.assistant.ui.theme.TextPrimary
import com.djbooth.assistant.ui.theme.TextSecondary

@Composable
fun QueuePanelDialog(
    queue: List<QueuedTrack>,
    onDismiss: () -> Unit,
    onRemoveFromQueue: (String) -> Unit,
    onTogglePeakMode: (String) -> Unit,
    onReorder: (Int, Int) -> Unit,
    onPlayQueuedNow: (QueuedTrack) -> Unit,
    onClearQueue: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(16.dp))
                .background(DJPanelBackground)
                .border(1.5.dp, NeonCyan, RoundedCornerShape(16.dp))
                .padding(16.dp),
            color = DJPanelBackground
        ) {
            Column {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.QueueMusic,
                            contentDescription = "Cola Auto",
                            tint = NeonCyan,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "COLA AUTO-PLAYLIST (${queue.size})",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            letterSpacing = 1.sp
                        )
                    }

                    Row {
                        if (queue.isNotEmpty()) {
                            OutlinedButton(
                                onClick = onClearQueue,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonMagenta),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text(text = "Vaciar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        IconButton(onClick = onDismiss, modifier = Modifier.size(30.dp)) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Cerrar", tint = TextSecondary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (queue.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "La cola de reproducción está vacía.\nAñade temas desde el panel de recomendaciones.",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp)
                    ) {
                        itemsIndexed(queue) { index, item ->
                            QueuedItemRow(
                                index = index,
                                totalCount = queue.size,
                                item = item,
                                onRemove = { onRemoveFromQueue(item.track.id) },
                                onTogglePeak = { onTogglePeakMode(item.track.id) },
                                onMoveUp = { if (index > 0) onReorder(index, index - 1) },
                                onMoveDown = { if (index < queue.size - 1) onReorder(index, index + 1) },
                                onPlayNow = { onPlayQueuedNow(item) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QueuedItemRow(
    index: Int,
    totalCount: Int,
    item: QueuedTrack,
    onRemove: () -> Unit,
    onTogglePeak: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onPlayNow: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(DJCardSurface)
            .border(1.dp, Color(0xFF2E354F), RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Posición + Info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "#${index + 1}",
                    color = NeonEmerald,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )

                Column {
                    Text(
                        text = item.track.title,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${item.track.artist} • ${item.track.bpm.toInt()} BPM (${item.track.key})",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Opciones: Configuración de inicio (Intro vs Peak) + Reordenar + Eliminar
            Row(verticalAlignment = Alignment.CenterVertically) {
                // TOGGLE GUARDADO EN COLA: Intro vs Peak
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (item.startFromPeak) NeonAmber.copy(alpha = 0.25f) else Color(0xFF262C40))
                        .border(1.dp, if (item.startFromPeak) NeonAmber else Color(0xFF3D4666), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(0.dp)
                    ) {
                        IconButton(
                            onClick = onTogglePeak,
                            modifier = Modifier.size(22.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = "Peak Toggle",
                                tint = if (item.startFromPeak) NeonAmber else TextSecondary
                            )
                        }
                        Text(
                            text = if (item.startFromPeak) "PEAK (${item.track.formattedPeakStart})" else "INTRO (00:00)",
                            color = if (item.startFromPeak) NeonAmber else TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Reordenar
                IconButton(onClick = onMoveUp, enabled = index > 0, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = "Arriba", tint = if (index > 0) TextPrimary else TextSecondary.copy(alpha = 0.3f))
                }
                IconButton(onClick = onMoveDown, enabled = index < totalCount - 1, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = Icons.Default.ArrowDownward, contentDescription = "Abajo", tint = if (index < totalCount - 1) TextPrimary else TextSecondary.copy(alpha = 0.3f))
                }

                // Play Now
                IconButton(onClick = onPlayNow, modifier = Modifier.size(26.dp)) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play Now", tint = NeonCyan)
                }

                // Eliminar de la cola
                IconButton(onClick = onRemove, modifier = Modifier.size(26.dp)) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar", tint = NeonMagenta)
                }
            }
        }
    }
}
