package com.example.brainbites.presentation.models

import kotlinx.serialization.Serializable

@Serializable
data class TangoPuzzleResponse(
    val id: String,                    // Unique puzzle ID from server
    val size: Int,                     // Usually 6
    val difficulty: String?,           // "easy", "medium", "hard" (optional)
    val cells: List<LockedCell>,       // Pre-filled cells
    val edges: List<PuzzleEdge>        // Constraints between cells
)

@Serializable
data class LockedCell(
    val row: Int,
    val col: Int,
    val value: String                  // "CIRCLE" or "DIAMOND"
)

@Serializable
data class PuzzleEdge(
    val row: Int,
    val col: Int,
    val axis: String,                  // "HORIZONTAL" or "VERTICAL"
    val constraint: String             // "EQUAL" or "MULTIPLY"
)

fun TangoPuzzleResponse.toGameBoard(): GameBoard {
    // Build empty grid
    val cellsGrid = List(size) { r ->
        List(size) { c ->
            Cell(row = r, col = c)
        }
    }.toMutableList().map { it.toMutableList() }

    // Helper to lock cells
    fun lock(r: Int, c: Int, value: CellValue) {
        cellsGrid[r][c] = cellsGrid[r][c].copy(
            value = value,
            isLocked = true
        )
    }

    // Convert server cell values to your CellValue enum
    cells.forEach { locked ->
        val cellValue = when (locked.value.uppercase()) {
            "CIRCLE" -> CellValue.CIRCLE
            "DIAMOND" -> CellValue.DIAMOND
            else -> throw IllegalArgumentException("Unknown cell value: ${locked.value}")
        }
        lock(locked.row, locked.col, cellValue)
    }

    // Convert server edges to your Edge objects
    val puzzleEdges = edges.map { edge ->
        val edgeAxis = when (edge.axis.uppercase()) {
            "HORIZONTAL" -> EdgeAxis.HORIZONTAL
            "VERTICAL" -> EdgeAxis.VERTICAL
            else -> throw IllegalArgumentException("Unknown axis: ${edge.axis}")
        }

        val edgeConstraint = when (edge.constraint.uppercase()) {
            "EQUAL" -> EdgeConstraint.EQUAL
            "MULTIPLY" -> EdgeConstraint.MULTIPLY
            else -> throw IllegalArgumentException("Unknown constraint: ${edge.constraint}")
        }

        Edge(
            row = edge.row,
            col = edge.col,
            axis = edgeAxis,
            constraint = edgeConstraint
        )
    }

    return GameBoard(
        size = size,
        cells = cellsGrid.map { it.toList() },   // Convert back to immutable
        edges = puzzleEdges
    )
}