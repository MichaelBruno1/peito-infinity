package com.example.peitoinfinity.presentation.plan

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.peitoinfinity.domain.model.Exercise
import com.example.peitoinfinity.domain.model.PlanDay
import com.example.peitoinfinity.domain.model.PlanExercise
import com.example.peitoinfinity.domain.repository.ExerciseExclusionRepository
import com.example.peitoinfinity.domain.repository.ExerciseRepository
import com.example.peitoinfinity.domain.repository.TrainingPlanRepository
import com.example.peitoinfinity.domain.repository.WorkoutSessionRepository
import com.example.peitoinfinity.domain.usecase.RegenerateWorkoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlanDayDetailUiState(
    val planDay: PlanDay? = null,
    val exercises: List<PlanExercise> = emptyList(),
    val isLoading: Boolean = true,
    val isRegenerating: Boolean = false,
    val showExclusionDialog: Boolean = false,
    val excludedExercises: Set<String> = emptySet(),
    val error: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PlanDayDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val trainingPlanRepository: TrainingPlanRepository,
    private val regenerateWorkoutUseCase: RegenerateWorkoutUseCase,
    private val exerciseExclusionRepository: ExerciseExclusionRepository,
    private val workoutSessionRepository: WorkoutSessionRepository,
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    private val dayId: Long = checkNotNull(savedStateHandle["dayId"])

    private val _isRegenerating = MutableStateFlow(false)
    private val _showExclusionDialog = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)
    private val _dayTrigger = MutableStateFlow(Unit)

    private val _exclusions = flow {
        emit(exerciseExclusionRepository.getExcludedIds())
        exerciseExclusionRepository.getAll().collect { emit(it.toSet()) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    private val _planDay = _dayTrigger.flatMapLatest {
        flow { emit(trainingPlanRepository.getDayById(dayId)) }
    }

    val allExercises = exerciseRepository.getAllExercises()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Agrupamento para contornar o limite de 5 fluxos do combine
    private val flowA = combine(_planDay, trainingPlanRepository.getExercisesForDay(dayId)) { day, exercises -> Pair(day, exercises) }
    private val flowB = combine(_isRegenerating, _showExclusionDialog) { regenerating, showDialog -> Pair(regenerating, showDialog) }
    private val flowC = combine(_exclusions, _error) { exclusions, err -> Pair(exclusions, err) }

    val uiState: StateFlow<PlanDayDetailUiState> = combine(flowA, flowB, flowC) { a, b, c ->
        val (day, exercises) = a
        val (regenerating, showDialog) = b
        val (exclusions, err) = c
        
        PlanDayDetailUiState(
            planDay = day,
            exercises = exercises,
            isLoading = day == null && exercises.isEmpty(),
            isRegenerating = regenerating,
            showExclusionDialog = showDialog,
            excludedExercises = exclusions,
            error = err
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PlanDayDetailUiState()
    )

    fun setShowExclusionDialog(show: Boolean) {
        _showExclusionDialog.value = show
    }

    fun startWorkout(onSessionStarted: (Long) -> Unit) {
        viewModelScope.launch {
            try {
                val sessionId = workoutSessionRepository.startSession(dayId)
                onSessionStarted(sessionId)
            } catch (e: Exception) {
                _error.value = "Erro ao iniciar treino: ${e.message}"
            }
        }
    }

    fun regenerateDay(exclusions: Set<String>) {
        viewModelScope.launch {
            _showExclusionDialog.value = false
            _isRegenerating.value = true
            _error.value = null

            if (exclusions.isNotEmpty()) {
                exerciseExclusionRepository.addExclusions(exclusions.toList(), isGlobal = false)
            }

            regenerateWorkoutUseCase(dayId, exclusions)
                .onSuccess {
                    _isRegenerating.value = false
                    _dayTrigger.value = Unit // Força recarga do PlanDay no flow
                }
                .onFailure { t ->
                    _isRegenerating.value = false
                    _error.value = "Erro ao regenerar treino do dia: ${t.message}"
                }
        }
    }
}
