package com.example.peitoinfinity.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.peitoinfinity.domain.model.Gender
import com.example.peitoinfinity.domain.model.TrainingGoal
import com.example.peitoinfinity.domain.model.TrainingLevel
import com.example.peitoinfinity.domain.model.UserProfile
import com.example.peitoinfinity.domain.usecase.GetUserProfileUseCase
import com.example.peitoinfinity.domain.usecase.SaveUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val gender: Gender? = null,
    val heightCm: String = "",
    val weightKg: String = "",
    val trainingDaysPerWeek: Int = 3,
    val availableTimeMinutes: Int = 60,
    val trainingLevel: TrainingLevel? = null,
    val trainingGoal: TrainingGoal? = null,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val saveUserProfileUseCase: SaveUserProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            getUserProfileUseCase().collect { profile ->
                profile?.let { p ->
                    _uiState.update {
                        it.copy(
                            gender = p.gender,
                            heightCm = p.heightCm.toInt().toString(),
                            weightKg = p.weightKg.toInt().toString(),
                            trainingDaysPerWeek = p.trainingDaysPerWeek,
                            availableTimeMinutes = p.availableTimeMinutes,
                            trainingLevel = p.trainingLevel,
                            trainingGoal = p.trainingGoal
                        )
                    }
                }
            }
        }
    }

    fun updateGender(gender: Gender) {
        _uiState.update { it.copy(gender = gender) }
    }

    fun updateHeight(height: String) {
        _uiState.update { it.copy(heightCm = height) }
    }

    fun updateWeight(weight: String) {
        _uiState.update { it.copy(weightKg = weight) }
    }

    fun updateTrainingDays(days: Int) {
        _uiState.update { it.copy(trainingDaysPerWeek = days) }
    }

    fun updateAvailableTime(time: Int) {
        _uiState.update { it.copy(availableTimeMinutes = time) }
    }

    fun updateTrainingLevel(level: TrainingLevel) {
        _uiState.update { it.copy(trainingLevel = level) }
    }

    fun updateTrainingGoal(goal: TrainingGoal) {
        _uiState.update { it.copy(trainingGoal = goal) }
    }

    fun saveProfile() {
        val state = _uiState.value
        val height = state.heightCm.toFloatOrNull()
        val weight = state.weightKg.toFloatOrNull()

        if (state.gender == null || height == null || weight == null || state.trainingLevel == null || state.trainingGoal == null) {
            _uiState.update { it.copy(error = "Preencha todos os campos corretamente") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null, isSaved = false) }
            val profile = UserProfile(
                gender = state.gender,
                heightCm = height,
                weightKg = weight,
                trainingDaysPerWeek = state.trainingDaysPerWeek,
                availableTimeMinutes = state.availableTimeMinutes,
                trainingLevel = state.trainingLevel,
                trainingGoal = state.trainingGoal
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
