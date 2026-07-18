package com.example.peitoinfinity.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.peitoinfinity.presentation.components.AnimatedCounter
import com.example.peitoinfinity.presentation.components.PeitoTopBar
import com.example.peitoinfinity.presentation.report.ReportViewModel
import com.example.peitoinfinity.presentation.util.ValueFormatter
import com.example.peitoinfinity.ui.theme.PeitoDimens
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    viewModel: ReportViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isCurrent = isCurrentWeek(uiState.currentWeekStart)

    Scaffold(
        topBar = {
            PeitoTopBar(title = "Histórico & Relatórios")
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(PeitoDimens.paddingMd)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.selectPreviousWeek() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Semana anterior"
                    )
                }

                Text(
                    text = if (isCurrent) "Esta semana" else formatWeekRange(uiState.currentWeekStart),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = { viewModel.selectNextWeek() },
                    enabled = !isCurrent
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Próxima semana"
                    )
                }
            }

            Spacer(modifier = Modifier.height(PeitoDimens.paddingMd))

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.report == null || !uiState.report!!.hasData) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(PeitoDimens.paddingLg),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(PeitoDimens.paddingMd))
                    Text(
                        text = "Nenhum treino registrado nesta semana",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(PeitoDimens.paddingXs))
                    Text(
                        text = "Inicie um treino para ver seu progresso aqui!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                val report = uiState.report!!
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(PeitoDimens.paddingSm),
                    verticalArrangement = Arrangement.spacedBy(PeitoDimens.paddingSm),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        StatCard(
                            title = "Tempo Total",
                            targetValue = report.totalTrainingTimeMinutes.toFloat(),
                            format = { ValueFormatter.formatDuration(it.toInt()) },
                            icon = "⏱️"
                        )
                    }
                    item {
                        StatCard(
                            title = "Sessões",
                            targetValue = report.sessionsCount.toFloat(),
                            format = { it.toInt().toString() },
                            icon = "🏋️"
                        )
                    }
                    item {
                        StatCard(
                            title = "Peso Total",
                            targetValue = report.totalWeightKg,
                            format = { ValueFormatter.formatWeight(it) },
                            icon = "💪"
                        )
                    }
                    item {
                        StatCard(
                            title = "Distância",
                            targetValue = report.totalDistanceKm,
                            format = { ValueFormatter.formatDistance(it) },
                            icon = "🏃"
                        )
                    }
                    item {
                        StatCard(
                            title = "Vel. Média",
                            targetValue = report.averageSpeedKmh,
                            format = { ValueFormatter.formatSpeed(it) },
                            icon = "⚡"
                        )
                    }
                    item {
                        StatCard(
                            title = "Desc. Médio",
                            targetValue = report.averageRestSeconds.toFloat(),
                            format = { ValueFormatter.formatRest(it.toInt()) },
                            icon = "⏳"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    targetValue: Float,
    format: (Float) -> String,
    icon: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(110.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(PeitoDimens.paddingMd),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = icon,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            AnimatedCounter(
                targetValue = targetValue,
                format = format,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

private fun formatWeekRange(start: LocalDate): String {
    val end = start.plusDays(6)
    val formatter = DateTimeFormatter.ofPattern("dd/MM")
    return "Semana de ${start.format(formatter)} - ${end.format(formatter)}"
}

private fun isCurrentWeek(start: LocalDate): Boolean {
    val currentStart = getStartOfWeek(LocalDate.now())
    return start.isEqual(currentStart)
}

private fun getStartOfWeek(date: LocalDate): LocalDate {
    var d = date
    while (d.dayOfWeek != DayOfWeek.MONDAY) {
        d = d.minusDays(1)
    }
    return d
}
