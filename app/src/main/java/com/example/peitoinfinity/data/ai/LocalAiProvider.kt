package com.example.peitoinfinity.data.ai

import android.content.Context
import com.google.ai.edge.litertlm.Engine
import com.google.ai.edge.litertlm.EngineConfig
import com.google.ai.edge.litertlm.Backend
import com.google.ai.edge.litertlm.Content
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.StringBuilder
import javax.inject.Inject

class LocalAiProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : AiProvider {

    private var engine: Engine? = null

    private suspend fun initEngine() {
        if (engine != null) return
        withContext(Dispatchers.IO) {
            val config = EngineConfig(
                modelPath = getModelPath(),
                backend = Backend.GPU() // Instanciação da classe GPU
            )
            engine = Engine(config).also { it.initialize() }
        }
    }

    override suspend fun generate(prompt: String): Result<String> {
        return try {
            initEngine()
            val conversation = engine!!.createConversation()
            val response = StringBuilder()
            conversation.sendMessageAsync(prompt).collect { message ->
                val text = message.contents.contents
                    .filterIsInstance<Content.Text>()
                    .joinToString("") { it.text }
                response.append(text)
            }
            Result.success(response.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun generateStream(prompt: String): Flow<String> = flow {
        initEngine()
        val conversation = engine!!.createConversation()
        conversation.sendMessageAsync(prompt).collect { message ->
            val text = message.contents.contents
                .filterIsInstance<Content.Text>()
                .joinToString("") { it.text }
            emit(text)
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun isAvailable(): Boolean {
        return try {
            val modelFile = File(getModelPath())
            modelFile.exists() && modelFile.length() > 0
        } catch (e: Exception) {
            false
        }
    }

    private fun getModelPath(): String {
        return "${context.filesDir}/models/gemma-4-1b.litertlm"
    }
}
