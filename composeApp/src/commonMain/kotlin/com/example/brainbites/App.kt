package com.example.brainbites

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview

import com.example.brainbites.di.appModule
import com.example.brainbites.presentation.navigation.AppNavGraph
import org.koin.compose.KoinApplication

@Composable
@Preview
fun App() {
    MaterialTheme {
        KoinApplication(
            application = {
                modules(appModule)
            }
        ) {
            AppNavGraph()
        }
    }
}

