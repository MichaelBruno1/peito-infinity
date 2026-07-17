package com.example.peitoinfinity.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FitnessCenter
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
import com.example.peitoinfinity.presentation.components.ExerciseExclusionDialog
import com.example.peitoinfinity.presentation.components.LoadingIndicator
import com.example.peitoinfinity.presentation.components.PeitoTopBar
import com.example.peitoinfinity.presentation.components.PlanDayCard
import com.example.peitoinfinity.presentation.plan.TrainingPlanViewModel
import com.example.peitoinfinity.ui.theme.PeitoDimens
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingPlanScreen(
    onDayClick: (Long) -> Unit,
    viewModel: TrainingPlanViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val allExercises by viewModel.allExercises.collectAsStateWithLifecycle()

    if (uiState.isGenerating) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            LoadingIndicator(messages = listOf(uiState.generationProgress ?: "Processando..."))
        }
        return
    }

    Scaffold(
        topBar = {
            PeitoTopBar(title = "Meu Plano")
        },
        floatingActionButton = {
            if (uiState.activePlan != null) {
                FloatingActionButton(
                    onClick = { viewModel.setShowExclusionDialog(true) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Gerar Novo Plano")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.activePlan == null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(PeitoDimens.paddingMd),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(PeitoDimens.paddingMd))
                    Text(
                        text = "Nenhum plano ativo",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(PeitoDimens.paddingSm))
                    Text(
                        text = "Gere um plano de treinos exclusivo e personalizado com IA de acordo com o seu perfil.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = PeitoDimens.paddingMd)
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = { viewModel.setShowExclusionDialog(true) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(PeitoDimens.buttonHeight),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Text("Gerar Plano de Treino")
                    }
                }
            } else {
                val plan = uiState.activePlan!!
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(PeitoDimens.paddingMd),
                    verticalArrangement = Arrangement.spacedBy(PeitoDimens.paddingMd)
                ) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(PeitoDimens.paddingMd)) {
                                Text(
                                    text = plan.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                Text(
                                    text = "Criado em: ${dateFormat.format(Date(plan.createdAt))}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(PeitoDimens.paddingSm))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    SuggestionChip(
                                        onClick = {},
                                        label = { Text(plan.trainingLevel.displayName) }
                                    )
                                    SuggestionChip(
                                        onClick = {},
                                        label = { Text(plan.trainingGoal.displayName) }
                                    )
                                }
                            }
                        }
                    }

                    items(uiState.planDays, key = { it.id }) { day ->
                        PlanDayCard(
                            day = day,
                            onClick = { onDayClick(day.id) },
                            modifier = Modifier.fillMaxWidth()
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

            if (uiState.showExclusionDialog) {
                ExerciseExclusionDialog(
                    allExercises = allExercises,
                    initiallyExcludedIds = uiState.excludedExercises,
                    onDismiss = { viewModel.setShowExclusionDialog(false) },
                    onConfirm = viewModel::generatePlan
                )
            }
        }
    }
}
