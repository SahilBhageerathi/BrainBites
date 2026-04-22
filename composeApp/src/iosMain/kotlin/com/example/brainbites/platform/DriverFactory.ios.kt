package com.example.brainbites.platform

import app.cash.sqldelight.db.SqlDriver

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.example.brainbites.AppDatabase

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = AppDatabase.Schema,
            name = "brainbites.db"
        )
    }
}