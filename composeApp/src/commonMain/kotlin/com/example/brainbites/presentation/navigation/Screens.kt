package com.example.brainbites.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import brainbites.composeapp.generated.resources.Res
import brainbites.composeapp.generated.resources.growth_tab_icon
import brainbites.composeapp.generated.resources.home_tab_icon
import brainbites.composeapp.generated.resources.puzzle_tab_icon
import brainbites.composeapp.generated.resources.settings_tab_icon
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource


sealed class Screens {

}

object Routes {
    const val HOME_TAB = "HOME"
    const val GAME_TAB = "GAME"
    const val GROWTH_TAB = "GROWTH"
    const val SETTINGS_TAB = "SETTINGS"
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: DrawableResource,
)

object NavItems {
    val bottomNavItems = listOf(
        BottomNavItem(
            Routes.HOME_TAB,
            "Home",
            Res.drawable.home_tab_icon
        ),
        BottomNavItem(Routes.GAME_TAB, "Puzzle", Res.drawable.puzzle_tab_icon),
        BottomNavItem(Routes.GROWTH_TAB, "Growth", Res.drawable.growth_tab_icon),
        BottomNavItem(Routes.SETTINGS_TAB, "Settings", Res.drawable.settings_tab_icon)
    )
}
