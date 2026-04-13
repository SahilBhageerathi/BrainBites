package com.example.brainbites.platform

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.example.brainbites.platform.DATA_STORE_FILE_NAME
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.io.File

actual fun createDataStore(): DataStore<Preferences> = PreferenceDataStoreFactory.create(
    corruptionHandler = null,
    migrations = emptyList(),
    scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
) {
    File(System.getProperty("java.io.tmpdir"), DATA_STORE_FILE_NAME).absoluteFile  // or use a better app-specific directory
}