package com.example.brainbites.presentation.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.brainbites.presentation.common.AppScaffold
import com.example.brainbites.presentation.common.BrainBitesBottomBar
import com.example.brainbites.presentation.screens.GamesTab.GamesListScreen
import com.example.brainbites.presentation.screens.GamesTab.Tango.TangoGameScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val selectedTabRoute = NavItems.bottomNavItems.firstOrNull { item ->
        currentDestination?.hierarchy?.any { it.route == item.route } == true
    }?.route ?: Routes.HOME_TAB

    val showBottomBar = currentDestination?.route != Routes.TANGO_GAME

    AppScaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(animationSpec = tween(300)) { it } + fadeIn(tween(300)),
                exit = slideOutVertically(animationSpec = tween(300)) { it } + fadeOut(tween(300))
            ) {
                BrainBitesBottomBar(
                    tabs = NavItems.bottomNavItems,
                    currentRoute = selectedTabRoute,
                    onItemClick = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME_TAB,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.HOME_TAB) {
                Screen1()
            }
            composable(Routes.GAME_TAB) {
                GamesListScreen(onPlayClick = { navController.navigate(Routes.TANGO_GAME) })
            }
            composable(Routes.TANGO_GAME) {
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
            text = "Home Screen",
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