package com.example.peitoinfinity.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.peitoinfinity.presentation.components.ExerciseExclusionDialog
import com.example.peitoinfinity.presentation.components.LoadingIndicator
import com.example.peitoinfinity.presentation.components.PeitoTopBar
import com.example.peitoinfinity.presentation.plan.PlanDayDetailViewModel
import com.example.peitoinfinity.ui.theme.PeitoDimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanDayDetailScreen(
    dayId: Long,
    onBackClick: () -> Unit,
    onStartWorkout: (Long) -> Unit,
    viewModel: PlanDayDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val allExercises by viewModel.allExercises.collectAsStateWithLifecycle()

    if (uiState.isRegenerating) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            LoadingIndicator(messages = listOf("Regenerando treino do dia..."))
        }
        return
    }

    Scaffold(
        topBar = {
            PeitoTopBar(
                title = uiState.planDay?.dayName ?: "Detalhes do Treino",
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        },
        bottomBar = {
            if (uiState.planDay != null && uiState.exercises.isNotEmpty()) {
                Surface(
                    tonalElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(PeitoDimens.paddingMd),
                        horizontalArrangement = Arrangement.spacedBy(PeitoDimens.paddingSm)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.setShowExclusionDialog(true) },
                            modifier = Modifier
                                .weight(1f)
                                .height(PeitoDimens.buttonHeight),
                            shape = MaterialTheme.shapes.large
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Regenerar")
                        }

                        Button(
                            onClick = { viewModel.startWorkout(onStartWorkout) },
                            modifier = Modifier
                                .weight(1.5f)
                                .height(PeitoDimens.buttonHeight),
                            shape = MaterialTheme.shapes.large
                        ) {
                            Text("Iniciar Treino")
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.planDay == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Dia de treino não encontrado.")
                }
            } else {
                val day = uiState.planDay!!
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = PeitoDimens.paddingMd,
                        end = PeitoDimens.paddingMd,
                        top = PeitoDimens.paddingMd,
                        bottom = 80.dp // Espaço extra para não cobrir o bottomBar
                    ),
                    verticalArrangement = Arrangement.spacedBy(PeitoDimens.paddingMd)
                ) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(PeitoDimens.paddingMd)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Foco do Treino",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    SuggestionChip(
                                        onClick = {},
                                        label = { Text("~${day.estimatedDurationMinutes} min") }
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    day.focusMuscleGroups.forEach { muscle ->
                                        SuggestionChip(
                                            onClick = {},
                                            label = { Text(muscle.displayName) }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    items(uiState.exercises, key = { it.id }) { planExercise ->
                        val exercise = planExercise.exercise
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(PeitoDimens.paddingMd)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = exercise?.name ?: "Exercício",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(modifier = Modifier.width(PeitoDimens.paddingSm))
                                    Text(
                                        text = "${planExercise.sets} × ${planExercise.reps}",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Descanso: ${planExercise.restSeconds}s",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    exercise?.equipment?.let { equip ->
                                        Text(
                                            text = "•  Equipamento: ${equip.displayName}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                planExercise.notes?.let { note ->
                                    if (note.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(PeitoDimens.paddingSm))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.Top,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Info,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = note,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontStyle = FontStyle.Italic,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            uiState.error?.let { err ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(PeitoDimens.paddingMd)
                ) {
                    Text(err)
                }
            }

            if (uiState.showExclusionDialog) {
                ExerciseExclusionDialog(
                    allExercises = allExercises,
                    initiallyExcludedIds = uiState.excludedExercises,
                    onDismiss = { viewModel.setShowExclusionDialog(false) },
                    onConfirm = viewModel::regenerateDay
                )
            }
        }
    }
}
