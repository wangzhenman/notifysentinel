package com.wangzhenman.notifysentinel.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val STORE_NAME = "notify_sentinel_settings"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = STORE_NAME)

class SettingsStore(
    private val context: Context,
) {
    val serverUrl: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[SERVER_URL_KEY] ?: DEFAULT_SERVER_URL
    }

    val miPushRegId: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[MIPUSH_REG_ID_KEY] ?: ""
    }

    suspend fun saveServerUrl(url: String) {
        context.dataStore.edit { preferences ->
            preferences[SERVER_URL_KEY] = url
        }
    }

    suspend fun saveMiPushRegId(regId: String) {
        context.dataStore.edit { preferences ->
            preferences[MIPUSH_REG_ID_KEY] = regId
        }
    }

    companion object {
        private val SERVER_URL_KEY = stringPreferencesKey("server_url")
        private val MIPUSH_REG_ID_KEY = stringPreferencesKey("mipush_reg_id")
        const val DEFAULT_SERVER_URL = "http://192.168.1.10:8080"
    }
}
