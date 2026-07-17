package com.example.peitoinfinity.data.ai

import com.example.peitoinfinity.data.local.preferences.AppPreferences
import com.example.peitoinfinity.domain.model.AiMode
import javax.inject.Inject

class AiProviderSelector @Inject constructor(
    private val localProvider: LocalAiProvider,
    private val remoteProvider: RemoteAiProvider,
    private val preferences: AppPreferences
) {
    fun getCurrentProvider(): AiProvider {
        return when (preferences.getAiModeSync()) {
            AiMode.LOCAL -> localProvider
            AiMode.REMOTE -> remoteProvider
        }
    }

    suspend fun isCurrentProviderAvailable(): Boolean {
        return getCurrentProvider().isAvailable()
    }
}
