package com.example.peitoinfinity.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.peitoinfinity.domain.model.AiMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "peito_infinity_preferences")

class AppPreferences(private val context: Context) {

    private val dataStore = context.dataStore

    companion object {
        private val KEY_AI_MODE = stringPreferencesKey("ai_mode")
        private val KEY_LOCAL_MODEL_NAME = stringPreferencesKey("local_model_name")
    }

    val aiMode: Flow<AiMode> = dataStore.data.map { preferences ->
        val modeStr = preferences[KEY_AI_MODE] ?: AiMode.LOCAL.name
        runCatching { AiMode.valueOf(modeStr) }.getOrDefault(AiMode.LOCAL)
    }

    suspend fun setAiMode(mode: AiMode) {
        dataStore.edit { preferences ->
            preferences[KEY_AI_MODE] = mode.name
        }
    }

    val localModelName: Flow<String> = dataStore.data.map { preferences ->
        preferences[KEY_LOCAL_MODEL_NAME] ?: com.example.peitoinfinity.BuildConfig.LOCAL_MODEL_NAME
    }

    suspend fun setLocalModelName(name: String) {
        dataStore.edit { preferences ->
            preferences[KEY_LOCAL_MODEL_NAME] = name
        }
    }

    fun getLocalModelNameSync(): String {
        return runBlocking {
            localModelName.first()
        }
    }

    fun getAiModeSync(): AiMode {
        return runBlocking {
            aiMode.first()
        }
    }
}
