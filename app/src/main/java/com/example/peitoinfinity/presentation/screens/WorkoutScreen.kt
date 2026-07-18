package com.example.peitoinfinity.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.peitoinfinity.domain.model.ExerciseLog
import com.example.peitoinfinity.domain.model.ExerciseType
import com.example.peitoinfinity.presentation.components.AnimatedCard
import com.example.peitoinfinity.presentation.components.LoadingIndicator
import com.example.peitoinfinity.presentation.components.PeitoTopBar
import com.example.peitoinfinity.presentation.workout.WorkoutExerciseUi
import com.example.peitoinfinity.presentation.workout.WorkoutViewModel
import com.example.peitoinfinity.ui.theme.PeitoDimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutScreen(
    sessionId: Long,
    onFinishWorkout: () -> Unit,
    viewModel: WorkoutViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showFinishDialog by remember { mutableStateOf(false) }
    var finishNotes by remember { mutableStateOf("") }

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (uiState.isFinishing) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            LoadingIndicator(messages = listOf("Finalizando e salvando treino..."))
        }
        return
    }

    Scaffold(
        topBar = {
            PeitoTopBar(
                title = "Treino em Andamento",
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = formatElapsedTime(uiState.elapsedTimeSeconds),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { showFinishDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(PeitoDimens.paddingMd)
                        .height(PeitoDimens.buttonHeight),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text(
                        text = "Finalizar Treino",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = PeitoDimens.paddingMd,
                    end = PeitoDimens.paddingMd,
                    top = PeitoDimens.paddingMd,
                    bottom = 80.dp
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
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Progresso do Treino",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${uiState.completedExercisesCount} de ${uiState.totalExercisesCount} concluídos",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(PeitoDimens.paddingSm))
                            val progress = if (uiState.totalExercisesCount > 0) {
                                uiState.completedExercisesCount.toFloat() / uiState.totalExercisesCount
                            } else 0f
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                            )
                        }
                    }
                }

                itemsIndexed(uiState.exercises, key = { _, itemUi -> itemUi.planExercise.id }) { index, itemUi ->
                    AnimatedCard(index = index) {
                        WorkoutExerciseCard(
                            item = itemUi,
                            onToggleExpand = { viewModel.toggleExerciseExpanded(itemUi.planExercise.id) },
                            onValueChange = { weight, reps, speed, duration, distance ->
                                viewModel.updateInput(itemUi.planExercise.id) {
                                    it.copy(
                                        weightKg = weight ?: it.weightKg,
                                        reps = reps ?: it.reps,
                                        speedKmh = speed ?: it.speedKmh,
                                        durationMin = duration ?: it.durationMin,
                                        distanceM = distance ?: it.distanceM
                                    )
                                }
                            },
                            onRegisterSet = {
                                val isCardio = itemUi.exercise.exerciseType == ExerciseType.CARDIO
                                viewModel.logSet(itemUi.planExercise.id, itemUi.planExercise.exerciseId, isCardio)
                            },
                            onDeleteSet = viewModel::deleteLog
                        )
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

            if (showFinishDialog) {
                AlertDialog(
                    onDismissRequest = { showFinishDialog = false },
                    title = { Text("Concluir Treino", fontWeight = FontWeight.Bold) },
                    text = {
                        Column {
                            Text(
                                text = "Deseja salvar e encerrar este treino? Você pode registrar alguma observação abaixo.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(PeitoDimens.paddingSm))
                            OutlinedTextField(
                                value = finishNotes,
                                onValueChange = { finishNotes = it },
                                placeholder = { Text("ex: Me senti muito forte hoje!") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showFinishDialog = false
                                viewModel.finishWorkout(finishNotes.takeIf { it.isNotBlank() }, onFinishWorkout)
                            }
                        ) {
                            Text("Salvar Treino")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showFinishDialog = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun WorkoutExerciseCard(
    item: WorkoutExerciseUi,
    onToggleExpand: () -> Unit,
    onValueChange: (weight: String?, reps: String?, speed: String?, duration: String?, distance: String?) -> Unit,
    onRegisterSet: () -> Unit,
    onDeleteSet: (ExerciseLog) -> Unit,
    modifier: Modifier = Modifier
) {
    val isCardio = item.exercise.exerciseType == ExerciseType.CARDIO
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isCompleted) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PeitoDimens.paddingMd)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    if (item.isCompleted) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Concluído",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(PeitoDimens.paddingSm))
                    }
                    Column {
                        Text(
                            text = item.exercise.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Meta: ${item.planExercise.sets} séries × ${item.planExercise.reps}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Icon(
                    imageVector = if (item.isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            }

            Spacer(modifier = Modifier.height(PeitoDimens.paddingSm))
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (s in 1..item.planExercise.sets) {
                    val isSetLogged = item.logs.size >= s
                    SuggestionChip(
                        onClick = {},
                        label = { Text("Série $s") },
                        colors = if (isSetLogged) {
                            SuggestionChipDefaults.suggestionChipColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                labelColor = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            SuggestionChipDefaults.suggestionChipColors()
                        }
                    )
                }
            }

            AnimatedVisibility(visible = item.isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = PeitoDimens.paddingMd)
                ) {
                    if (item.logs.isNotEmpty()) {
                        Text(
                            text = "Séries Realizadas:",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        item.logs.forEach { log ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val logText = if (isCardio) {
                                    "${log.speedKmh ?: 0f} km/h • ${(log.durationSeconds ?: 0) / 60} min • ${log.distanceMeters ?: 0f}m"
                                } else {
                                    "${log.weightKg ?: 0f} kg × ${log.reps ?: 0} reps"
                                }
                                Text(
                                    text = "Série ${log.setNumber}: $logText",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                IconButton(
                                    onClick = { onDeleteSet(log) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Deletar Série",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(PeitoDimens.paddingMd))
                    }

                    Text(
                        text = "Registrar Série ${item.logs.size + 1}:",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    if (isCardio) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(PeitoDimens.paddingSm),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = item.currentSpeedKmh,
                                onValueChange = { onValueChange(null, null, it, null, null) },
                                label = { Text("km/h") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = item.currentDurationMin,
                                onValueChange = { onValueChange(null, null, null, it, null) },
                                label = { Text("min") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = item.currentDistanceM,
                                onValueChange = { onValueChange(null, null, null, null, it) },
                                label = { Text("metros") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    } else {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(PeitoDimens.paddingSm),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = item.currentWeightKg,
                                onValueChange = { onValueChange(it, null, null, null, null) },
                                label = { Text("Carga (kg)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = item.currentReps,
                                onValueChange = { onValueChange(null, it, null, null, null) },
                                label = { Text("Repetições") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(PeitoDimens.paddingMd))
                    Button(
                        onClick = onRegisterSet,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Registrar Série")
                    }
                }
            }
        }
    }
}

fun formatElapsedTime(seconds: Long): String {
    val hrs = seconds / 3600
    val mins = (seconds % 3600) / 60
    val secs = seconds % 60
    return if (hrs > 0) {
        String.format("%02d:%02d:%02d", hrs, mins, secs)
    } else {
        String.format("%02d:%02d", mins, secs)
    }
}
