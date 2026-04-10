package com.example.brainbites.presentation.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.brainbites.presentation.navigation.NavItems
import com.example.brainbites.presentation.navigation.Routes
import com.example.brainbites.presentation.navigation.Screen1
import com.example.brainbites.presentation.navigation.Screen2
import com.example.brainbites.presentation.navigation.Screen3
import com.example.brainbites.presentation.navigation.Screen4
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeContainer() {

    val items = NavItems.bottomNavItems

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { items.size }
    )

    val coroutineScope = rememberCoroutineScope()

    AppScaffold { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {


            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->

                when (items[page].route) {
                    Routes.HOME_TAB -> Screen1()
                    Routes.GAME_TAB -> Screen2()
                    Routes.GROWTH_TAB -> Screen3()
                    Routes.SETTINGS_TAB -> Screen4()
                }
            }


            BrainBitesBottomBar(
                tabs = items,
                currentRoute = items[pagerState.currentPage].route,
                onItemClick = { route ->

                    val index = items.indexOfFirst { it.route == route }

                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
        }
    }
}