package com.example.peitoinfinity.presentation.plan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.peitoinfinity.domain.model.Exercise
import com.example.peitoinfinity.domain.model.PlanDay
import com.example.peitoinfinity.domain.model.TrainingPlan
import com.example.peitoinfinity.domain.repository.ExerciseExclusionRepository
import com.example.peitoinfinity.domain.repository.ExerciseRepository
import com.example.peitoinfinity.domain.repository.TrainingPlanRepository
import com.example.peitoinfinity.domain.usecase.GenerateTrainingPlanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TrainingPlanUiState(
    val activePlan: TrainingPlan? = null,
    val planDays: List<PlanDay> = emptyList(),
    val isLoading: Boolean = true,
    val isGenerating: Boolean = false,
    val showExclusionDialog: Boolean = false,
    val excludedExercises: Set<String> = emptySet(),
    val error: String? = null,
    val generationProgress: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TrainingPlanViewModel @Inject constructor(
    private val trainingPlanRepository: TrainingPlanRepository,
    private val generateTrainingPlanUseCase: GenerateTrainingPlanUseCase,
    private val exerciseExclusionRepository: ExerciseExclusionRepository,
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    private val _isGenerating = MutableStateFlow(false)
    private val _showExclusionDialog = MutableStateFlow(false)
    private val _generationProgress = MutableStateFlow<String?>(null)
    private val _error = MutableStateFlow<String?>(null)

    private val _exclusions = flow {
        emit(exerciseExclusionRepository.getExcludedIds())
        exerciseExclusionRepository.getAll().collect { emit(it.toSet()) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    private val _activePlan = trainingPlanRepository.getActivePlan()
    
    private val _planDays = _activePlan.flatMapLatest { plan ->
        if (plan != null) {
            trainingPlanRepository.getDaysForPlan(plan.id)
        } else {
            flowOf(emptyList())
        }
    }

    val allExercises = exerciseRepository.getAllExercises()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Agrupamento para contornar o limite de 5 fluxos do combine
    private val flowA = combine(_activePlan, _planDays) { plan, days -> Pair(plan, days) }
    private val flowB = combine(_isGenerating, _showExclusionDialog) { generating, showDialog -> Pair(generating, showDialog) }
    private val flowC = combine(_exclusions, _generationProgress, _error) { exclusions, progress, err -> Triple(exclusions, progress, err) }

    val uiState: StateFlow<TrainingPlanUiState> = combine(flowA, flowB, flowC) { a, b, c ->
        val (plan, days) = a
        val (generating, showDialog) = b
        val (exclusions, progress, err) = c
        
        TrainingPlanUiState(
            activePlan = plan,
            planDays = days,
            isLoading = false,
            isGenerating = generating,
            showExclusionDialog = showDialog,
            excludedExercises = exclusions,
            error = err,
            generationProgress = progress
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TrainingPlanUiState()
    )

    fun setShowExclusionDialog(show: Boolean) {
        _showExclusionDialog.value = show
    }

    fun generatePlan(exclusions: Set<String>) {
        viewModelScope.launch {
            _showExclusionDialog.value = false
            _isGenerating.value = true
            _generationProgress.value = "Analisando seu perfil..."
            _error.value = null
            
            if (exclusions.isNotEmpty()) {
                exerciseExclusionRepository.addExclusions(exclusions.toList(), isGlobal = false)
            }

            _generationProgress.value = "Gerando plano com IA..."
            generateTrainingPlanUseCase(exclusions)
                .onSuccess {
                    _isGenerating.value = false
                    _generationProgress.value = null
                }
                .onFailure { t ->
                    _isGenerating.value = false
                    _generationProgress.value = null
                    _error.value = "Erro ao gerar treino: ${t.message}"
                }
        }
    }
}
