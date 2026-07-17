package com.example.peitoinfinity.presentation.workout

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.peitoinfinity.domain.model.Difficulty
import com.example.peitoinfinity.domain.model.Equipment
import com.example.peitoinfinity.domain.model.Exercise
import com.example.peitoinfinity.domain.model.ExerciseLog
import com.example.peitoinfinity.domain.model.ExerciseType
import com.example.peitoinfinity.domain.model.MuscleGroup
import com.example.peitoinfinity.domain.model.PlanExercise
import com.example.peitoinfinity.domain.model.WorkoutSession
import com.example.peitoinfinity.domain.repository.ExerciseLogRepository
import com.example.peitoinfinity.domain.repository.TrainingPlanRepository
import com.example.peitoinfinity.domain.repository.WorkoutSessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WorkoutInput(
    val weightKg: String = "",
    val reps: String = "",
    val speedKmh: String = "",
    val durationMin: String = "",
    val distanceM: String = ""
)

data class WorkoutExerciseUi(
    val planExercise: PlanExercise,
    val exercise: Exercise,
    val logs: List<ExerciseLog> = emptyList(),
    val isExpanded: Boolean = false,
    val currentWeightKg: String = "",
    val currentReps: String = "",
    val currentSpeedKmh: String = "",
    val currentDurationMin: String = "",
    val currentDistanceM: String = "",
    val isCompleted: Boolean = false
)

data class WorkoutUiState(
    val session: WorkoutSession? = null,
    val exercises: List<WorkoutExerciseUi> = emptyList(),
    val elapsedTimeSeconds: Long = 0,
    val completedExercisesCount: Int = 0,
    val totalExercisesCount: Int = 0,
    val isFinishing: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val workoutSessionRepository: WorkoutSessionRepository,
    private val exerciseLogRepository: ExerciseLogRepository,
    private val trainingPlanRepository: TrainingPlanRepository
) : ViewModel() {

    private val sessionId: Long = checkNotNull(savedStateHandle["sessionId"])

    private val _isFinishing = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)
    private val _elapsedTimeSeconds = MutableStateFlow(0L)
    private val _expandedStates = MutableStateFlow<Map<Long, Boolean>>(emptyMap())
    private val _userInputs = MutableStateFlow<Map<Long, WorkoutInput>>(emptyMap())

    private val _session = flow {
        emit(workoutSessionRepository.getSessionById(sessionId))
    }.filterNotNull().stateIn(viewModelScope, SharingStarted.Lazily, null)

    private val _exercises = _session.flatMapLatest { session ->
        if (session != null) {
            trainingPlanRepository.getExercisesForDay(session.planDayId)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _logs = exerciseLogRepository.getLogsForSession(sessionId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Agrupamento para contornar o limite de 5 fluxos do combine
    private val flowA = combine(_session, _exercises, _logs) { s, e, l -> Triple(s, e, l) }
    private val flowB = combine(_expandedStates, _userInputs) { exp, inp -> Pair(exp, inp) }
    private val flowC = combine(_elapsedTimeSeconds, _isFinishing, _error) { t, f, err -> Triple(t, f, err) }

    val uiState: StateFlow<WorkoutUiState> = combine(flowA, flowB, flowC) { a, b, c ->
        val (session, planExercises, logs) = a
        val (expanded, inputs) = b
        val (elapsed, finishing, err) = c

        if (session == null) {
            WorkoutUiState(isLoading = true, error = err)
        } else {
            val workoutExercises = planExercises.map { planEx ->
                val exLogs = logs.filter { it.exerciseId == planEx.exerciseId }
                val input = inputs[planEx.id] ?: WorkoutInput()
                val isCompleted = exLogs.size >= planEx.sets
                
                val fallbackExercise = Exercise(
                    id = planEx.exerciseId,
                    name = "Exercício",
                    primaryMuscleGroup = MuscleGroup.CHEST,
                    secondaryMuscleGroups = emptyList(),
                    equipment = Equipment.NONE,
                    exerciseType = ExerciseType.COMPOUND,
                    description = "",
                    difficulty = Difficulty.BEGINNER
                )

                WorkoutExerciseUi(
                    planExercise = planEx,
                    exercise = planEx.exercise ?: fallbackExercise,
                    logs = exLogs.sortedBy { it.setNumber },
                    isExpanded = expanded[planEx.id] ?: false,
                    currentWeightKg = input.weightKg,
                    currentReps = input.reps,
                    currentSpeedKmh = input.speedKmh,
                    currentDurationMin = input.durationMin,
                    currentDistanceM = input.distanceM,
                    isCompleted = isCompleted
                )
            }
            
            val completedCount = workoutExercises.count { it.isCompleted }
            
            WorkoutUiState(
                session = session,
                exercises = workoutExercises,
                elapsedTimeSeconds = elapsed,
                completedExercisesCount = completedCount,
                totalExercisesCount = workoutExercises.size,
                isFinishing = finishing,
                isLoading = false,
                error = err
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = WorkoutUiState()
    )

    init {
        viewModelScope.launch {
            while (true) {
                val currentSession = _session.value
                if (currentSession != null) {
                    _elapsedTimeSeconds.value = (System.currentTimeMillis() - currentSession.startedAt) / 1000
                }
                delay(1000)
            }
        }
    }

    fun toggleExerciseExpanded(planExerciseId: Long) {
        val current = _expandedStates.value
        _expandedStates.value = current + (planExerciseId to !(current[planExerciseId] ?: false))
    }

    fun updateInput(planExerciseId: Long, block: (WorkoutInput) -> WorkoutInput) {
        val current = _userInputs.value
        val input = current[planExerciseId] ?: WorkoutInput()
        _userInputs.value = current + (planExerciseId to block(input))
    }

    fun logSet(planExerciseId: Long, exerciseId: String, isCardio: Boolean) {
        val input = _userInputs.value[planExerciseId] ?: WorkoutInput()
        viewModelScope.launch {
            try {
                val sessionLogs = _logs.value
                val existingSetsCount = sessionLogs.count { it.exerciseId == exerciseId }
                val setNumber = existingSetsCount + 1

                val log = if (isCardio) {
                    ExerciseLog(
                        sessionId = sessionId,
                        exerciseId = exerciseId,
                        setNumber = setNumber,
                        speedKmh = input.speedKmh.toFloatOrNull(),
                        durationSeconds = input.durationMin.toIntOrNull()?.let { it * 60 },
                        distanceMeters = input.distanceM.toFloatOrNull()
                    )
                } else {
                    ExerciseLog(
                        sessionId = sessionId,
                        exerciseId = exerciseId,
                        setNumber = setNumber,
                        weightKg = input.weightKg.toFloatOrNull(),
                        reps = input.reps.toIntOrNull()
                    )
                }
                
                exerciseLogRepository.insertLog(log)
                updateInput(planExerciseId) { WorkoutInput() } // Limpar inputs
            } catch (e: Exception) {
                _error.value = "Erro ao registrar série: ${e.message}"
            }
        }
    }

    fun deleteLog(log: ExerciseLog) {
        viewModelScope.launch {
            try {
                exerciseLogRepository.deleteLog(log)
            } catch (e: Exception) {
                _error.value = "Erro ao deletar série: ${e.message}"
            }
        }
    }

    fun finishWorkout(notes: String?, onFinished: () -> Unit) {
        viewModelScope.launch {
            _isFinishing.value = true
            try {
                workoutSessionRepository.finishSession(sessionId, notes)
                onFinished()
            } catch (e: Exception) {
                _isFinishing.value = false
                _error.value = "Erro ao finalizar treino: ${e.message}"
            }
        }
    }
}
