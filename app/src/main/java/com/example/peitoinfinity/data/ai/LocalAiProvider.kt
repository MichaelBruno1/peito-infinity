package com.example.peitoinfinity.data.ai

import android.content.Context
import com.example.peitoinfinity.data.local.preferences.AppPreferences
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
    @ApplicationContext private val context: Context,
    private val appPreferences: AppPreferences
) : AiProvider {

    private var engine: Engine? = null

    fun getModelFile(): File {
        val modelName = appPreferences.getLocalModelNameSync()
        return File(File(context.filesDir, "models"), modelName)
    }

    private suspend fun initEngine() {
        if (engine != null) return
        withContext(Dispatchers.IO) {
            val modelPath = getModelPath()
            val modelFile = File(modelPath)
            if (!modelFile.exists() || modelFile.length() == 0L) {
                throw java.io.FileNotFoundException(
                    "Modelo local não encontrado no caminho: $modelPath. " +
                    "Por favor, faça o download do modelo nas configurações do app (Ajustes) ou selecione o 'Modelo Externo' para usar via internet."
                )
            }
            try {
                // Tenta inicializar com aceleração de GPU primeiro
                val config = EngineConfig(
                    modelPath = modelPath,
                    backend = Backend.GPU()
                )
                engine = Engine(config).also { it.initialize() }
            } catch (gpuException: Exception) {
                // Se falhar (ex: emuladores ou GPUs incompatíveis), faz fallback automático para CPU
                try {
                    val config = EngineConfig(
                        modelPath = modelPath,
                        backend = Backend.CPU()
                    )
                    engine = Engine(config).also { it.initialize() }
                } catch (cpuException: Exception) {
                    // Repassa o erro se ambos falharem (indica formato de arquivo realmente inválido)
                    throw Exception(
                        "Falha ao carregar o modelo local (GPU & CPU). Certifique-se de que o arquivo " +
                        "está no formato LiteRT compatível (.litertlm / .bin) e não está corrompido. Detalhe: ${cpuException.message}",
                        cpuException
                    )
                }
            }
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
            val path = getModelPath()
            val modelFile = File(path)
            modelFile.exists() && modelFile.length() > 0
        } catch (e: Exception) {
            false
        }
    }

    private fun getModelPath(): String {
        val modelsDir = File(context.filesDir, "models")
        if (!modelsDir.exists()) {
            modelsDir.mkdirs()
        }
        val configName = appPreferences.getLocalModelNameSync()
        val baseName = configName.substringBeforeLast(".")
        val names = listOf(
            configName,
            "$baseName.literlm",
            "$baseName.bin"
        )
        for (name in names) {
            val file = File(modelsDir, name)
            if (file.exists() && file.length() > 0) {
                return file.absolutePath
            }
        }
        return File(modelsDir, configName).absolutePath
    }
}
