package com.example.peitoinfinity.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.peitoinfinity.data.ai.LocalAiProvider
import com.example.peitoinfinity.data.ai.ModelDownloader
import com.example.peitoinfinity.data.ai.DownloadState
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
    val downloadProgress: Float = 0f,
    val downloadError: String? = null,
    val userProfile: UserProfile? = null,
    val error: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val localAiProvider: LocalAiProvider,
    private val modelDownloader: ModelDownloader
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

    fun downloadModel() {
        val targetFile = localAiProvider.getModelFile()
        viewModelScope.launch {
            _uiState.update { it.copy(isLocalModelDownloading = true, downloadError = null, downloadProgress = 0f) }
            val url = "https://huggingface.co/litert-community/gemma-4-E2B-it-litert-lm/resolve/main/gemma-4-E2B-it.litertlm?download=true"
            modelDownloader.downloadModel(url, targetFile).collect { state ->
                when (state) {
                    is DownloadState.Started -> {
                        _uiState.update { it.copy(isLocalModelDownloading = true, downloadProgress = 0f) }
                    }
                    is DownloadState.Downloading -> {
                        _uiState.update { it.copy(downloadProgress = state.progress) }
                    }
                    is DownloadState.Success -> {
                        _uiState.update { it.copy(
                            isLocalModelDownloading = false,
                            isLocalModelAvailable = true,
                            downloadProgress = 1f
                        ) }
                    }
                    is DownloadState.Error -> {
                        _uiState.update { it.copy(
                            isLocalModelDownloading = false,
                            downloadError = state.message
                        ) }
                    }
                }
            }
        }
    }
}
