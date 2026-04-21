package com.example.brainbites.presentation.viewModels.Flow

import com.example.brainbites.domain.models.CellPosition
import com.example.brainbites.domain.models.FlowPuzzle

data class FlowUiState (
    val isLoading: Boolean = true,
    val puzzle: FlowPuzzle = FlowPuzzle(id = 0, gridSize = 7, hints = emptyList(), walls = emptyList()),
    val path: List<CellPosition> = emptyList(),
    val timerText: String = "0:00",
    val isSolved: Boolean = false,
    val errorMsg : String? = null,
)