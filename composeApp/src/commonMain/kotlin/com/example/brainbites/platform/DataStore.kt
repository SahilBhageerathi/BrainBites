package com.example.brainbites.platform

// shared/src/commonMain/kotlin/data/datastore/DataStore.kt

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// Add other keys as needed: intPreferencesKey, longPreferencesKey, floatPreferencesKey, doublePreferencesKey

const val DATA_STORE_FILE_NAME = "app_preferences.preferences_pb"

expect fun createDataStore(): DataStore<Preferences>

val getDataStoreInstance : DataStore<Preferences> by lazy {
    createDataStore()
}
// Helper extension for cleaner usage
suspend fun DataStore<Preferences>.putString(key: String, value: String) {
    edit { preferences ->
        preferences[stringPreferencesKey(key)] = value
    }
}

suspend fun DataStore<Preferences>.putBoolean(key: String, value: Boolean) {
    edit { preferences ->
        preferences[booleanPreferencesKey(key)] = value
    }
}

suspend fun DataStore<Preferences>.putInt(key: String, value: Int) {
    edit { preferences ->
        preferences[intPreferencesKey(key)] = value
    }
}

suspend fun DataStore<Preferences>.getString(key: String, default: String = ""): String {
    val preferences = data.first()
    return preferences[stringPreferencesKey(key)] ?: default
}

suspend fun DataStore<Preferences>.getInt(key: String, default: Int = -1): Int {
    val preferences = data.first()
    return preferences[intPreferencesKey(key)] ?: default
}

fun DataStore<Preferences>.getStringFlow(key: String, default: String = "") =
data.map { it[stringPreferencesKey(key)] ?: default }

fun DataStore<Preferences>.getBooleanFlow(key: String, default: Boolean = false) =
data.map { it[booleanPreferencesKey(key)] ?: default }