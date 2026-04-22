package com.example.brainbites.platform

import app.cash.sqldelight.db.SqlDriver

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.brainbites.AppDatabase

actual class DriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = AppDatabase.Schema,
            context = context,
            name = "brainbites.db"
        )
    }
}