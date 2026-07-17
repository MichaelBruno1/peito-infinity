package com.example.peitoinfinity.data.ai

import kotlinx.coroutines.flow.Flow

interface AiProvider {
    suspend fun generate(prompt: String): Result<String>
    fun generateStream(prompt: String): Flow<String>
    suspend fun isAvailable(): Boolean
}
