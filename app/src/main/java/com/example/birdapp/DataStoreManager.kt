package com.example.birdapp

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class DataStoreManager(private val context: Context) {

    companion object {
        private val HAS_SEEN_WELCOME = booleanPreferencesKey("has_seen_welcome")
    }

    // Check if user has seen the Welcome Page
    fun hasSeenWelcome(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[HAS_SEEN_WELCOME] ?: false
        }
    }

    // Set flag when user has seen the Welcome Page
    suspend fun setHasSeenWelcome(value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[HAS_SEEN_WELCOME] = value
        }
    }

    // Reset the Welcome Page flag (so it shows again)
    suspend fun resetWelcomePage() {
        context.dataStore.edit { preferences ->
            preferences[HAS_SEEN_WELCOME] = false
        }
    }
}
