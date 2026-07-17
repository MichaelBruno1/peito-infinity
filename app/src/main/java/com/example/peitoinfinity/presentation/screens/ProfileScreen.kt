package com.example.peitoinfinity.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.peitoinfinity.domain.model.Gender
import com.example.peitoinfinity.domain.model.TrainingGoal
import com.example.peitoinfinity.domain.model.TrainingLevel
import com.example.peitoinfinity.presentation.components.PeitoTopBar
import com.example.peitoinfinity.presentation.profile.ProfileViewModel
import com.example.peitoinfinity.ui.theme.PeitoDimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onBackClick()
        }
    }

    Scaffold(
        topBar = {
            PeitoTopBar(
                title = "Editar Perfil",
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
            Surface(
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = viewModel::saveProfile,
                    enabled = !uiState.isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(PeitoDimens.paddingMd)
                        .height(PeitoDimens.buttonHeight),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text(if (uiState.isSaving) "Salvando..." else "Salvar Alterações")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(PeitoDimens.paddingMd)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(PeitoDimens.paddingMd)
        ) {
            // Sexo
            Text(text = "Sexo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(PeitoDimens.paddingMd)
            ) {
                GenderOptionCard(
                    label = "Masculino",
                    selected = uiState.gender == Gender.MALE,
                    onClick = { viewModel.updateGender(Gender.MALE) },
                    modifier = Modifier.weight(1f)
                )
                GenderOptionCard(
                    label = "Feminino",
                    selected = uiState.gender == Gender.FEMALE,
                    onClick = { viewModel.updateGender(Gender.FEMALE) },
                    modifier = Modifier.weight(1f)
                )
            }

            // Altura / Peso
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(PeitoDimens.paddingMd)
            ) {
                OutlinedTextField(
                    value = uiState.heightCm,
                    onValueChange = viewModel::updateHeight,
                    label = { Text("Altura (cm)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = uiState.weightKg,
                    onValueChange = viewModel::updateWeight,
                    label = { Text("Peso (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }

            // Dias por semana
            Text(
                text = "Dias por semana: ${uiState.trainingDaysPerWeek}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Slider(
                value = uiState.trainingDaysPerWeek.toFloat(),
                onValueChange = { viewModel.updateTrainingDays(it.toInt()) },
                valueRange = 1f..7f,
                steps = 5
            )

            // Tempo disponível
            Text(
                text = "Tempo disponível: ${uiState.availableTimeMinutes} min",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Slider(
                value = uiState.availableTimeMinutes.toFloat(),
                onValueChange = { viewModel.updateAvailableTime(it.toInt()) },
                valueRange = 20f..180f,
                steps = 15
            )

            // Nível de Treinamento
            Text(text = "Nível de Experiência", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Column(verticalArrangement = Arrangement.spacedBy(PeitoDimens.paddingSm)) {
                TrainingLevelOptionCard(
                    title = "Iniciante",
                    description = "Treino há menos de 6 meses",
                    selected = uiState.trainingLevel == TrainingLevel.BEGINNER,
                    onClick = { viewModel.updateTrainingLevel(TrainingLevel.BEGINNER) }
                )
                TrainingLevelOptionCard(
                    title = "Intermediário",
                    description = "Treino entre 6 meses e 2 anos",
                    selected = uiState.trainingLevel == TrainingLevel.INTERMEDIATE,
                    onClick = { viewModel.updateTrainingLevel(TrainingLevel.INTERMEDIATE) }
                )
                TrainingLevelOptionCard(
                    title = "Avançado",
                    description = "Treino há mais de 2 anos",
                    selected = uiState.trainingLevel == TrainingLevel.ADVANCED,
                    onClick = { viewModel.updateTrainingLevel(TrainingLevel.ADVANCED) }
                )
            }

            // Objetivo
            Text(text = "Objetivo Principal", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Column(verticalArrangement = Arrangement.spacedBy(PeitoDimens.paddingSm)) {
                TrainingGoalOptionCard(
                    title = "Perda de Peso",
                    selected = uiState.trainingGoal == TrainingGoal.WEIGHT_LOSS,
                    onClick = { viewModel.updateTrainingGoal(TrainingGoal.WEIGHT_LOSS) }
                )
                TrainingGoalOptionCard(
                    title = "Ganho de Massa",
                    selected = uiState.trainingGoal == TrainingGoal.MUSCLE_GAIN,
                    onClick = { viewModel.updateTrainingGoal(TrainingGoal.MUSCLE_GAIN) }
                )
                TrainingGoalOptionCard(
                    title = "Ganho de Força",
                    selected = uiState.trainingGoal == TrainingGoal.STRENGTH_GAIN,
                    onClick = { viewModel.updateTrainingGoal(TrainingGoal.STRENGTH_GAIN) }
                )
                TrainingGoalOptionCard(
                    title = "Manutenção",
                    selected = uiState.trainingGoal == TrainingGoal.MAINTENANCE,
                    onClick = { viewModel.updateTrainingGoal(TrainingGoal.MAINTENANCE) }
                )
            }

            uiState.error?.let { err ->
                Text(
                    text = err,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun GenderOptionCard(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(
            1.dp,
            if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        ),
        modifier = modifier.height(56.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = label, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun TrainingLevelOptionCard(
    title: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(
            1.dp,
            if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PeitoDimens.paddingMd)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun TrainingGoalOptionCard(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(
            1.dp,
            if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PeitoDimens.paddingMd)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}
