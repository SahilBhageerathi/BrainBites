package com.example.brainbites.platform

import androidx.datastore.preferences.core.Preferences
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

private lateinit var appContext: Context  // Set this from your Application class

fun setDataStoreContext(context: Context) {
    appContext = context
}

actual fun createDataStore(): DataStore<Preferences> = PreferenceDataStoreFactory.create(
    corruptionHandler = null,
    migrations = emptyList(),
    scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
) {
    appContext.preferencesDataStoreFile(DATA_STORE_FILE_NAME)
}