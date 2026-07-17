package com.example.peitoinfinity.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.peitoinfinity.domain.model.AiMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "peito_infinity_preferences")

class AppPreferences(private val context: Context) {

    private val dataStore = context.dataStore

    companion object {
        private val KEY_AI_MODE = stringPreferencesKey("ai_mode")
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
}
