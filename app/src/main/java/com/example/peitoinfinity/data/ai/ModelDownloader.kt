package com.example.peitoinfinity.data.ai

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

sealed interface DownloadState {
    object Started : DownloadState
    data class Downloading(val progress: Float, val downloadedBytes: Long, val totalBytes: Long) : DownloadState
    object Success : DownloadState
    data class Error(val message: String) : DownloadState
}

@Singleton
class ModelDownloader @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun downloadModel(urlStr: String, targetFile: File): Flow<DownloadState> = flow {
        emit(DownloadState.Started)
        try {
            val url = URL(urlStr)
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 15000
            connection.readTimeout = 15000
            
            // Siga redirects se houver (Hugging Face resolve redirects para cloudfront/s3)
            connection.instanceFollowRedirects = true
            
            var responseCode = connection.responseCode
            var currentConnection = connection
            var redirectCount = 0
            
            // Seguir redirecionamentos manualmente se connection.instanceFollowRedirects falhar em alguns cenários de protocolo misto (http -> https)
            while ((responseCode == HttpURLConnection.HTTP_MOVED_TEMP || 
                    responseCode == HttpURLConnection.HTTP_MOVED_PERM || 
                    responseCode == HttpURLConnection.HTTP_SEE_OTHER ||
                    responseCode == 307 || responseCode == 308) && redirectCount < 5) {
                
                val newUrl = currentConnection.getHeaderField("Location") ?: break
                currentConnection.disconnect()
                
                val redirectUrl = URL(newUrl)
                val conn = redirectUrl.openConnection() as HttpURLConnection
                conn.connectTimeout = 15000
                conn.readTimeout = 15000
                conn.instanceFollowRedirects = true
                
                currentConnection = conn
                responseCode = conn.responseCode
                redirectCount++
            }

            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw Exception("Erro no servidor: $responseCode ${currentConnection.responseMessage}")
            }

            val fileLength = currentConnection.contentLengthLong
            val parent = targetFile.parentFile
            if (parent != null && !parent.exists()) {
                parent.mkdirs()
            }

            // Arquivo temporário para evitar corrupção
            val tempFile = File(targetFile.absolutePath + ".tmp")
            if (tempFile.exists()) {
                tempFile.delete()
            }

            currentConnection.inputStream.use { input ->
                FileOutputStream(tempFile).use { output ->
                    val data = ByteArray(8192)
                    var total: Long = 0
                    var count: Int
                    var lastProgressEmitTime = 0L
                    
                    while (input.read(data).also { count = it } != -1) {
                        total += count
                        output.write(data, 0, count)
                        
                        val currentTime = System.currentTimeMillis()
                        if (fileLength > 0 && currentTime - lastProgressEmitTime > 150) {
                            val progress = total.toFloat() / fileLength
                            emit(DownloadState.Downloading(progress, total, fileLength))
                            lastProgressEmitTime = currentTime
                        }
                    }
                }
            }
            
            // Renomear arquivo temporário para o destino final
            if (targetFile.exists()) {
                targetFile.delete()
            }
            if (!tempFile.renameTo(targetFile)) {
                throw Exception("Não foi possível salvar o arquivo do modelo após o download.")
            }
            emit(DownloadState.Success)
        } catch (e: Exception) {
            val tempFile = File(targetFile.absolutePath + ".tmp")
            if (tempFile.exists()) {
                tempFile.delete()
            }
            emit(DownloadState.Error(e.message ?: "Erro desconhecido durante o download"))
        }
    }.flowOn(Dispatchers.IO)
}
