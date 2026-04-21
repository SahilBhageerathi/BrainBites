package com.example.brainbites.presentation.screens.GamesTab.Flow

import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import com.example.brainbites.presentation.viewModels.Flow.FlowAction
import com.example.brainbites.presentation.viewModels.Flow.FlowUiEvent
import com.example.brainbites.presentation.viewModels.Flow.FlowViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FlowGameRoute(
    navController: NavController
) {
    val viewModel = koinViewModel<FlowViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is FlowUiEvent.ShowSnackBar -> {
                    snackBarHostState.showSnackbar(event.message)
                }
                FlowUiEvent.PuzzleSolved -> {
                    snackBarHostState.showSnackbar("Puzzle Complete!")
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) {
        FlowPuzzleScreen(
            puzzle = uiState.puzzle,
            path = uiState.path,
            timerText = uiState.timerText,
            isSolved = uiState.isSolved,
            onCellDragged = { row, col ->
                viewModel.onAction(FlowAction.CellDragged(row, col))
            },
            onUndo = { viewModel.onAction(FlowAction.Undo) },
            onReset = { viewModel.onAction(FlowAction.Reset) },
            onBack = { navController.popBackStack() }
        )
    }
}
