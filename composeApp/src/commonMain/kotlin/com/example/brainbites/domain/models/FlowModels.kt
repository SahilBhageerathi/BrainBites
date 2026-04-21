package com.example.brainbites.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Hint(val row: Int, val col: Int, val value: Int)

@Serializable
data class Wall(
    val row: Int,
    val col: Int,
    val direction: String  // "right", "bottom"
)

@Serializable
data class FlowPuzzle(
    val id: Int,
    val gridSize: Int,
    val hints: List<Hint>,
    val walls: List<Wall>
)

data class CellPosition(val row: Int, val col: Int)