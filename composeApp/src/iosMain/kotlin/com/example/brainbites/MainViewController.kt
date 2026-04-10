package com.example.brainbites

import androidx.compose.ui.window.ComposeUIViewController
import com.example.brainbites.presentation.common.HomeContainer
import com.example.brainbites.presentation.navigation.AppNavGraph

fun MainViewController() = ComposeUIViewController {
    HomeContainer()
//    AppNavGraph()
}