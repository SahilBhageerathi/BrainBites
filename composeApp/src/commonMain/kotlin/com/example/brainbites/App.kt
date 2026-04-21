package com.example.brainbites

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.example.brainbites.di.appModules

import com.example.brainbites.presentation.navigation.AppNavGraph
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration

@Composable
@Preview
fun App() {
    MaterialTheme {
        KoinApplication(
            configuration = koinConfiguration {
                modules(appModules())
            }
        ) {
            AppNavGraph()
        }
    }
}

