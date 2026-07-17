package com.example.peitoinfinity.data.ai

import com.example.peitoinfinity.BuildConfig
import com.google.genai.kotlin.Client
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RemoteAiProvider @Inject constructor() : AiProvider {

    private val client by lazy {
        Client(apiKey = BuildConfig.GEMINI_API_KEY)
    }

    override suspend fun generate(prompt: String): Result<String> {
        return try {
            val response = client.models.generateContent(
                model = BuildConfig.GEMINI_MODEL,
                text = prompt
            )
            Result.success(response.text ?: "")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun generateStream(prompt: String): Flow<String> {
        return client.models.generateContentStream(
            model = BuildConfig.GEMINI_MODEL,
            text = prompt
        ).map { it.text ?: "" }
         .flowOn(Dispatchers.IO)
    }

    override suspend fun isAvailable(): Boolean {
        return BuildConfig.GEMINI_API_KEY.isNotBlank()
    }
}
