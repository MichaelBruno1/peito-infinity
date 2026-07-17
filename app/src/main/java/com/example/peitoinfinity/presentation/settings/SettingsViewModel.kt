package com.example.peitoinfinity.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.peitoinfinity.data.ai.LocalAiProvider
import com.example.peitoinfinity.data.local.preferences.AppPreferences
import com.example.peitoinfinity.domain.model.AiMode
import com.example.peitoinfinity.domain.model.UserProfile
import com.example.peitoinfinity.domain.usecase.GetUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val aiMode: AiMode = AiMode.LOCAL,
    val isLocalModelAvailable: Boolean = false,
    val isLocalModelDownloading: Boolean = false,
    val userProfile: UserProfile? = null,
    val error: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val localAiProvider: LocalAiProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        observeSettings()
        observeProfile()
        checkLocalModelAvailability()
    }

    private fun checkLocalModelAvailability() {
        viewModelScope.launch {
            val available = localAiProvider.isAvailable()
            _uiState.update { it.copy(isLocalModelAvailable = available) }
        }
    }

    private fun observeSettings() {
        viewModelScope.launch {
            appPreferences.aiMode.collect { mode ->
                _uiState.update { it.copy(aiMode = mode) }
            }
        }
    }

    private fun observeProfile() {
        viewModelScope.launch {
            getUserProfileUseCase().collect { profile ->
                _uiState.update { it.copy(userProfile = profile) }
            }
        }
    }

    fun updateAiMode(mode: AiMode) {
        viewModelScope.launch {
            appPreferences.setAiMode(mode)
            checkLocalModelAvailability()
        }
    }
}
