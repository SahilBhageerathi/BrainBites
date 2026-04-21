package com.example.brainbites.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.brainbites.core.theme.scaffold_Bg_Color
import com.example.brainbites.core.utils.AppConstants

@Composable
fun AppScaffold(
    bgColor: Color = scaffold_Bg_Color,
    bottomBar: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0),
        modifier = Modifier
            .background(bgColor)
            .padding(0.dp),
        topBar = {
            BrainBitesTopBar(
                appBarTitle = AppConstants.APP_NAME,
                actionWidget = { UserXp(1240) },
                onProfileClick = { /* navigate */ }
            )
        },
        bottomBar = bottomBar,
    ) { padding ->
        content(padding)
    }
}