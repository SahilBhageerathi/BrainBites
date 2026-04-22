package com.example.brainbites.presentation.viewModels.Tango

import com.example.brainbites.presentation.models.Edge
import com.example.brainbites.presentation.models.GameBoard


data class TangoUiState(
    val isLoading : Boolean = false,
    val board: GameBoard = GameBoard(),
    val difficulty: String? = "Hard",
    val violations: Set<Edge> = emptySet(),
    val isSolved: Boolean = false,
    val moveCount: Int = 0,
)