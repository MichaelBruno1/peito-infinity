package com.example.peitoinfinity.presentation.exercises

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.peitoinfinity.domain.model.Exercise
import com.example.peitoinfinity.domain.model.MuscleGroup
import com.example.peitoinfinity.domain.repository.ExerciseExclusionRepository
import com.example.peitoinfinity.domain.usecase.GetExercisesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExerciseListUiState(
    val exercises: List<Exercise> = emptyList(),
    val filteredExercises: List<Exercise> = emptyList(),
    val searchQuery: String = "",
    val selectedMuscleGroup: MuscleGroup? = null,
    val isLoading: Boolean = true,
    val excludedExerciseIds: Set<String> = emptySet()
)

@HiltViewModel
class ExerciseListViewModel @Inject constructor(
    private val getExercisesUseCase: GetExercisesUseCase,
    private val exerciseExclusionRepository: ExerciseExclusionRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedMuscleGroup = MutableStateFlow<MuscleGroup?>(null)
    private val _isLoading = MutableStateFlow(true)

    private val _excludedIds = flow {
        emit(exerciseExclusionRepository.getExcludedIds())
        exerciseExclusionRepository.getAll().collect { list ->
            emit(list.toSet())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val uiState: StateFlow<ExerciseListUiState> = combine(
        getExercisesUseCase(),
        _searchQuery,
        _selectedMuscleGroup,
        _excludedIds,
        _isLoading
    ) { exercises, query, group, excluded, loading ->
        val filtered = exercises.filter { exercise ->
            val matchesQuery = query.isBlank() || exercise.name.contains(query, ignoreCase = true)
            val matchesGroup = group == null || exercise.primaryMuscleGroup == group
            matchesQuery && matchesGroup
        }
        ExerciseListUiState(
            exercises = exercises,
            filteredExercises = filtered,
            searchQuery = query,
            selectedMuscleGroup = group,
            isLoading = loading && exercises.isEmpty(),
            excludedExerciseIds = excluded
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ExerciseListUiState()
    )

    init {
        viewModelScope.launch {
            getExercisesUseCase().firstOrNull()
            _isLoading.value = false
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateSelectedMuscleGroup(group: MuscleGroup?) {
        _selectedMuscleGroup.value = group
    }

    fun toggleExclusion(exerciseId: String) {
        viewModelScope.launch {
            val excluded = _excludedIds.value
            if (excluded.contains(exerciseId)) {
                exerciseExclusionRepository.removeExclusion(exerciseId)
            } else {
                exerciseExclusionRepository.addExclusion(exerciseId, isGlobal = true)
            }
        }
    }
}
