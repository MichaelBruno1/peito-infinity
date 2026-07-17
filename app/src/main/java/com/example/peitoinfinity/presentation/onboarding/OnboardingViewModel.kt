package com.example.peitoinfinity.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.peitoinfinity.domain.model.Gender
import com.example.peitoinfinity.domain.model.TrainingGoal
import com.example.peitoinfinity.domain.model.TrainingLevel
import com.example.peitoinfinity.domain.model.UserProfile
import com.example.peitoinfinity.domain.usecase.SaveUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingUiState(
    val currentStep: Int = 1,
    val gender: Gender? = null,
    val heightCm: String = "",
    val weightKg: String = "",
    val trainingDaysPerWeek: Int = 3,
    val availableTimeMinutes: Int = 60,
    val trainingLevel: TrainingLevel? = null,
    val trainingGoal: TrainingGoal? = null,
    val isValid: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val saveUserProfileUseCase: SaveUserProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun updateGender(gender: Gender) {
        _uiState.update { it.copy(gender = gender) }
        validateInput()
    }

    fun updateHeight(height: String) {
        _uiState.update { it.copy(heightCm = height) }
        validateInput()
    }

    fun updateWeight(weight: String) {
        _uiState.update { it.copy(weightKg = weight) }
        validateInput()
    }

    fun updateTrainingDays(days: Int) {
        _uiState.update { it.copy(trainingDaysPerWeek = days) }
        validateInput()
    }

    fun updateAvailableTime(time: Int) {
        _uiState.update { it.copy(availableTimeMinutes = time) }
        validateInput()
    }

    fun updateTrainingLevel(level: TrainingLevel) {
        _uiState.update { it.copy(trainingLevel = level) }
        validateInput()
    }

    fun updateTrainingGoal(goal: TrainingGoal) {
        _uiState.update { it.copy(trainingGoal = goal) }
        validateInput()
    }

    fun nextStep() {
        val current = _uiState.value.currentStep
        if (canGoNext(current)) {
            _uiState.update { it.copy(currentStep = current + 1) }
        }
    }

    fun previousStep() {
        val current = _uiState.value.currentStep
        if (current > 1) {
            _uiState.update { it.copy(currentStep = current - 1) }
        }
    }

    private fun canGoNext(step: Int): Boolean {
        val state = _uiState.value
        return when (step) {
            1 -> state.gender != null && (state.heightCm.toFloatOrNull() ?: 0f) in 100f..250f && (state.weightKg.toFloatOrNull() ?: 0f) in 30f..300f
            2 -> state.trainingDaysPerWeek in 1..7 && state.availableTimeMinutes in 20..180
            3 -> state.trainingLevel != null
            else -> false
        }
    }

    private fun validateInput() {
        val state = _uiState.value
        val isValid = state.gender != null &&
                (state.heightCm.toFloatOrNull() ?: 0f) in 100f..250f &&
                (state.weightKg.toFloatOrNull() ?: 0f) in 30f..300f &&
                state.trainingLevel != null &&
                state.trainingGoal != null
        _uiState.update { it.copy(isValid = isValid) }
    }

    fun saveProfile() {
        val state = _uiState.value
        if (!state.isValid) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            val profile = UserProfile(
                gender = state.gender!!,
                heightCm = state.heightCm.toFloat(),
                weightKg = state.weightKg.toFloat(),
                trainingDaysPerWeek = state.trainingDaysPerWeek,
                availableTimeMinutes = state.availableTimeMinutes,
                trainingLevel = state.trainingLevel!!,
                trainingGoal = state.trainingGoal!!
            )
            saveUserProfileUseCase(profile)
                .onSuccess {
                    _uiState.update { it.copy(isSaving = false, isSaved = true) }
                }
                .onFailure { t ->
                    _uiState.update { it.copy(isSaving = false, error = t.message) }
                }
        }
    }
}
