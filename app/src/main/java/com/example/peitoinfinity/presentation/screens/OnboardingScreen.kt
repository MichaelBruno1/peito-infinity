package com.example.peitoinfinity.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.peitoinfinity.domain.model.Gender
import com.example.peitoinfinity.domain.model.TrainingGoal
import com.example.peitoinfinity.domain.model.TrainingLevel
import com.example.peitoinfinity.presentation.onboarding.OnboardingViewModel
import com.example.peitoinfinity.ui.theme.PeitoDimens

@Composable
fun OnboardingScreen(
    onFinishOnboarding: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onFinishOnboarding()
        }
    }

    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(PeitoDimens.paddingMd),
                horizontalArrangement = Arrangement.spacedBy(PeitoDimens.paddingMd)
            ) {
                if (uiState.currentStep > 1) {
                    OutlinedButton(
                        onClick = viewModel::previousStep,
                        modifier = Modifier
                            .weight(1f)
                            .height(PeitoDimens.buttonHeight),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Text("Voltar")
                    }
                }
                
                Button(
                    onClick = {
                        if (uiState.currentStep == 4) {
                            viewModel.saveProfile()
                        } else {
                            viewModel.nextStep()
                        }
                    },
                    enabled = when (uiState.currentStep) {
                        1 -> uiState.gender != null && uiState.heightCm.isNotEmpty() && uiState.weightKg.isNotEmpty()
                        2 -> true
                        3 -> uiState.trainingLevel != null
                        4 -> uiState.trainingGoal != null
                        else -> false
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(PeitoDimens.buttonHeight),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text(if (uiState.currentStep == 4) "Criar Meu Plano" else "Avançar")
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(PeitoDimens.paddingMd)
        ) {
            LinearProgressIndicator(
                progress = { uiState.currentStep / 4f },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(PeitoDimens.paddingSm))

            when (uiState.currentStep) {
                1 -> Step1PhysicalData(
                    gender = uiState.gender,
                    height = uiState.heightCm,
                    weight = uiState.weightKg,
                    onGenderSelected = viewModel::updateGender,
                    onHeightChanged = viewModel::updateHeight,
                    onWeightChanged = viewModel::updateWeight
                )
                2 -> Step2Availability(
                    days = uiState.trainingDaysPerWeek,
                    time = uiState.availableTimeMinutes,
                    onDaysChanged = viewModel::updateTrainingDays,
                    onTimeChanged = viewModel::updateAvailableTime
                )
                3 -> Step3Level(
                    selectedLevel = uiState.trainingLevel,
                    onLevelSelected = viewModel::updateTrainingLevel
                )
                4 -> Step4Goal(
                    selectedGoal = uiState.trainingGoal,
                    onGoalSelected = viewModel::updateTrainingGoal
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
private fun Step1PhysicalData(
    gender: Gender?,
    height: String,
    weight: String,
    onGenderSelected: (Gender) -> Unit,
    onHeightChanged: (String) -> Unit,
    onWeightChanged: (String) -> Unit
) {
    Text(
        text = "Vamos começar!",
        style = MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )
    Text(
        text = "Conte-nos sobre você para criar o treino ideal",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(PeitoDimens.paddingMd))

    Text(
        text = "Sexo",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.fillMaxWidth()
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(PeitoDimens.paddingMd)
    ) {
        GenderCard(
            label = "Masculino",
            icon = "♂️",
            selected = gender == Gender.MALE,
            onClick = { onGenderSelected(Gender.MALE) },
            modifier = Modifier.weight(1f)
        )
        GenderCard(
            label = "Feminino",
            icon = "♀️",
            selected = gender == Gender.FEMALE,
            onClick = { onGenderSelected(Gender.FEMALE) },
            modifier = Modifier.weight(1f)
        )
    }

    Spacer(modifier = Modifier.height(PeitoDimens.paddingSm))

    OutlinedTextField(
        value = height,
        onValueChange = onHeightChanged,
        label = { Text("Altura (cm)") },
        placeholder = { Text("Ex: 175") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth()
    )

    OutlinedTextField(
        value = weight,
        onValueChange = onWeightChanged,
        label = { Text("Peso (kg)") },
        placeholder = { Text("Ex: 75") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun GenderCard(
    label: String,
    icon: String,
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
        modifier = modifier.height(100.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = icon, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = label, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun Step2Availability(
    days: Int,
    time: Int,
    onDaysChanged: (Int) -> Unit,
    onTimeChanged: (Int) -> Unit
) {
    Text(
        text = "Sua rotina de treinos",
        style = MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(PeitoDimens.paddingMd))

    Text(
        text = "Dias por semana: $days",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.fillMaxWidth()
    )
    Slider(
        value = days.toFloat(),
        onValueChange = { onDaysChanged(it.toInt()) },
        valueRange = 1f..7f,
        steps = 5
    )

    Spacer(modifier = Modifier.height(PeitoDimens.paddingMd))

    Text(
        text = "Tempo disponível por treino: $time min",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.fillMaxWidth()
    )
    Slider(
        value = time.toFloat(),
        onValueChange = { onTimeChanged(it.toInt()) },
        valueRange = 20f..180f,
        steps = 15
    )
}

@Composable
private fun Step3Level(
    selectedLevel: TrainingLevel?,
    onLevelSelected: (TrainingLevel) -> Unit
) {
    Text(
        text = "Qual seu nível de experiência?",
        style = MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(PeitoDimens.paddingMd))

    Column(
        verticalArrangement = Arrangement.spacedBy(PeitoDimens.paddingSm),
        modifier = Modifier.fillMaxWidth()
    ) {
        TrainingLevelCard(
            title = "Iniciante",
            description = "Treino há menos de 6 meses",
            selected = selectedLevel == TrainingLevel.BEGINNER,
            onClick = { onLevelSelected(TrainingLevel.BEGINNER) }
        )
        TrainingLevelCard(
            title = "Intermediário",
            description = "Treino entre 6 meses e 2 anos",
            selected = selectedLevel == TrainingLevel.INTERMEDIATE,
            onClick = { onLevelSelected(TrainingLevel.INTERMEDIATE) }
        )
        TrainingLevelCard(
            title = "Avançado",
            description = "Treino há mais de 2 anos",
            selected = selectedLevel == TrainingLevel.ADVANCED,
            onClick = { onLevelSelected(TrainingLevel.ADVANCED) }
        )
    }
}

@Composable
private fun TrainingLevelCard(
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
private fun Step4Goal(
    selectedGoal: TrainingGoal?,
    onGoalSelected: (TrainingGoal) -> Unit
) {
    Text(
        text = "Qual seu objetivo principal?",
        style = MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(PeitoDimens.paddingMd))

    Column(
        verticalArrangement = Arrangement.spacedBy(PeitoDimens.paddingSm),
        modifier = Modifier.fillMaxWidth()
    ) {
        TrainingGoalCard(
            title = "Perda de Peso",
            description = "Emagrecer e definir",
            selected = selectedGoal == TrainingGoal.WEIGHT_LOSS,
            onClick = { onGoalSelected(TrainingGoal.WEIGHT_LOSS) }
        )
        TrainingGoalCard(
            title = "Ganho de Massa",
            description = "Hipertrofia muscular",
            selected = selectedGoal == TrainingGoal.MUSCLE_GAIN,
            onClick = { onGoalSelected(TrainingGoal.MUSCLE_GAIN) }
        )
        TrainingGoalCard(
            title = "Ganho de Força",
            description = "Aumentar cargas e performance",
            selected = selectedGoal == TrainingGoal.STRENGTH_GAIN,
            onClick = { onGoalSelected(TrainingGoal.STRENGTH_GAIN) }
        )
        TrainingGoalCard(
            title = "Manutenção",
            description = "Manter o peso e forma atual",
            selected = selectedGoal == TrainingGoal.MAINTENANCE,
            onClick = { onGoalSelected(TrainingGoal.MAINTENANCE) }
        )
    }
}

@Composable
private fun TrainingGoalCard(
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
