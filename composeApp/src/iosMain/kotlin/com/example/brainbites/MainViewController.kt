package com.example.brainbites

import androidx.compose.ui.window.ComposeUIViewController
import com.example.brainbites.di.appModules
import com.example.brainbites.platform.DriverFactory
import org.koin.core.context.startKoin
import org.koin.dsl.module

private var koinInitialized = false

fun initKoin() {
    if (koinInitialized) return
    startKoin {
        modules(appModules())
        modules(
            module {
                single { DriverFactory() }
            }
        )
    }
    koinInitialized = true
}

fun MainViewController() = ComposeUIViewController {
    initKoin()
    App()
}
