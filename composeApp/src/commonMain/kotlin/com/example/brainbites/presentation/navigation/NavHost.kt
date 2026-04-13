package com.example.brainbites.presentation.navigation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.brainbites.presentation.common.AppScaffold
import com.example.brainbites.presentation.screens.TangoGameScreen

@Composable
fun AppNavGraph() {

    val navController = rememberNavController()

    AppScaffold { padding ->

        NavHost(
            navController = navController,
            startDestination = Routes.HOME_TAB,
            modifier = Modifier.padding(8.dp)
        ) {

            composable(Routes.HOME_TAB) {
                Screen1()
            }

            composable(Routes.GAME_TAB) {
                TangoGameScreen()
            }

            composable(Routes.GROWTH_TAB) {
                Screen3()
            }
            composable(Routes.SETTINGS_TAB) {
                Screen4()
            }

        }
    }
}


@Composable
fun Screen1() {
    var visible by remember { mutableStateOf(false) }

    // Trigger animation on first composition
    LaunchedEffect(Unit) {
        visible = true
    }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        label = "alpha"
    )

    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.8f,
        label = "scale"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = "Internet Screen",
            color = Color.Black,
            modifier = Modifier.graphicsLayer {
                this.alpha = alpha
                scaleX = scale
                scaleY = scale
            }
        )
    }
}

@Composable
fun Screen2() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Game Screen",
            style = TextStyle(color = Color.Black)
        )
    }
}

@Composable
fun Screen3() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Growth Screen",
            style = TextStyle(color = Color.Black)
        )
    }
}

@Composable
fun Screen4() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Settings Screen",
            style = TextStyle(color = Color.Black)
        )
    }
}

