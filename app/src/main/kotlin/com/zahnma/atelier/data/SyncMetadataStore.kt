package com.zahnma.atelier.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.syncDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "atelier_sync",
)

class SyncMetadataStore(
    private val context: Context,
) {
    val dataVersion: Flow<String?> = context.syncDataStore.data.map { prefs ->
        prefs[KEY_DATA_VERSION]
    }

    val lastSyncedAt: Flow<Long?> = context.syncDataStore.data.map { prefs ->
        prefs[KEY_LAST_SYNCED_AT]
    }

    suspend fun saveSync(dataVersion: String) {
        context.syncDataStore.edit { prefs ->
            prefs[KEY_DATA_VERSION] = dataVersion
            prefs[KEY_LAST_SYNCED_AT] = System.currentTimeMillis()
        }
    }

    companion object {
        private val KEY_DATA_VERSION = stringPreferencesKey("data_version")
        private val KEY_LAST_SYNCED_AT = longPreferencesKey("last_synced_at")
    }
}
