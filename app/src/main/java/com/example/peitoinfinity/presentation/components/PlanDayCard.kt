package com.example.peitoinfinity.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.peitoinfinity.domain.model.PlanDay
import com.example.peitoinfinity.ui.theme.PeitoDimens
import com.example.peitoinfinity.ui.theme.toColor

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlanDayCard(
    day: PlanDay,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isCompleted: Boolean = false
) {
    // Gradiente baseado nos grupos musculares em foco
    val focusColors = day.focusMuscleGroups.take(2).map { it.toColor().copy(alpha = 0.12f) }
    val backgroundBrush = when (focusColors.size) {
        2 -> Brush.linearGradient(colors = listOf(focusColors[0], focusColors[1], MaterialTheme.colorScheme.surface))
        1 -> Brush.linearGradient(colors = listOf(focusColors[0], MaterialTheme.colorScheme.surface))
        else -> Brush.linearGradient(colors = listOf(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.surface))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = PeitoDimens.cardElevation)
    ) {
        Box(
            modifier = Modifier
                .background(brush = backgroundBrush)
                .padding(PeitoDimens.paddingMd)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = day.dayName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (isCompleted) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Concluído",
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Duração estimada: ${day.estimatedDurationMinutes} min",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        day.focusMuscleGroups.forEach { muscle ->
                            val chipColor = muscle.toColor()
                            SuggestionChip(
                                onClick = {},
                                label = {
                                    Text(
                                        text = muscle.displayName,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = chipColor.copy(alpha = 0.15f),
                                    labelColor = chipColor
                                ),
                                border = null
                            )
                        }
                    }
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Ver Detalhes",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
