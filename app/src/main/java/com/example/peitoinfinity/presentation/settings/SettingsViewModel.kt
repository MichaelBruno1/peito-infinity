package com.example.peitoinfinity.presentation.settings

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

data class SettingsUiState(
    val aiMode: AiMode = AiMode.LOCAL,
    val isLocalModelAvailable: Boolean = false,
    val isLocalModelDownloading: Boolean = false,
    val downloadProgress: Float = 0f,
    val downloadError: String? = null,
    val userProfile: UserProfile? = null,
    val error: String? = null,
    val isImporting: Boolean = false,
    val importProgress: Float = 0f,
    val importError: String? = null
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
            val url = com.example.peitoinfinity.BuildConfig.LOCAL_MODEL_URL
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

    fun importModelFile(context: Context, uri: Uri, modelName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isImporting = true, importProgress = 0f, importError = null) }
            try {
                // Garante que o arquivo tenha extensão suportada no final para evitar o erro de formato na Engine nativa
                val finalName = if (!modelName.endsWith(".litertlm") && 
                    !modelName.endsWith(".literlm") && 
                    !modelName.endsWith(".bin")) {
                    "$modelName.litertlm"
                } else {
                    modelName
                }

                // Salvar o nome do modelo nas configurações do DataStore
                appPreferences.setLocalModelName(finalName)

                val modelsDir = File(context.filesDir, "models")
                if (!modelsDir.exists()) {
                    modelsDir.mkdirs()
                }
                val destinationFile = File(modelsDir, finalName)

                withContext(Dispatchers.IO) {
                    val cursor = context.contentResolver.query(uri, null, null, null, null)
                    val sizeIndex = cursor?.getColumnIndex(OpenableColumns.SIZE)
                    cursor?.moveToFirst()
                    val totalBytes = sizeIndex?.let { cursor.getLong(it) }?.coerceAtLeast(1L) ?: 1L
                    cursor?.close()

                    context.contentResolver.openInputStream(uri)?.use { input ->
                        destinationFile.outputStream().use { output ->
                            val buffer = ByteArray(1024 * 64) // 64kb chunks
                            var bytesRead: Int
                            var totalCopied = 0L
                            while (input.read(buffer).also { bytesRead = it } != -1) {
                                output.write(buffer, 0, bytesRead)
                                totalCopied += bytesRead
                                val progress = totalCopied.toFloat() / totalBytes
                                _uiState.update { it.copy(importProgress = progress.coerceAtMost(1f)) }
                            }
                        }
                    }
                }

                _uiState.update { it.copy(isImporting = false, isLocalModelAvailable = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isImporting = false, importError = "Erro ao copiar arquivo: ${e.message}") }
            }
        }
    }
}
