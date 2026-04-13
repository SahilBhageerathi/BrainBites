package com.example.brainbites.presentation.models

// ── Cell state ────────────────────────────────────────────────────────────────

enum class CellValue { EMPTY, CIRCLE, DIAMOND }

data class Cell(
    val row: Int,
    val col: Int,
    val value: CellValue = CellValue.EMPTY,
    val isLocked: Boolean = false,   // visual region shading
    val isError : Boolean = false
)

// ── Border constraints ────────────────────────────────────────────────────────

/**
 * A constraint symbol placed on the border between two adjacent cells.
 * [EdgeConstraint.MULTIPLY] → the two cells must differ  (× symbol)
 * [EdgeConstraint.EQUAL]    → the two cells must be equal (= symbol)
 */
enum class EdgeConstraint { MULTIPLY, EQUAL }

/**
 * Axis of the edge.
 * HORIZONTAL → constraint is on the vertical border between (row, col) and (row, col+1)
 * VERTICAL   → constraint is on the horizontal border between (row, col) and (row+1, col)
 */
enum class EdgeAxis { HORIZONTAL, VERTICAL }

data class Edge(
    val row: Int,
    val col: Int,
    val axis: EdgeAxis,
    val constraint: EdgeConstraint,
)

// ── Full board ────────────────────────────────────────────────────────────────

data class GameBoard(
    val size: Int = 6,
    val cells: List<List<Cell>> = emptyList(),
    val edges: List<Edge> = emptyList(),
) {
    fun cellAt(row: Int, col: Int): Cell = cells[row][col]

    fun withCellCycled(row: Int, col: Int): GameBoard {
        val cell = cellAt(row, col)
        if (cell.isLocked) return this
        val next = when (cell.value) {
            CellValue.EMPTY   -> CellValue.CIRCLE
            CellValue.CIRCLE  -> CellValue.DIAMOND
            CellValue.DIAMOND -> CellValue.EMPTY
        }
        val newCells = cells.mapIndexed { r, rowList ->
            rowList.mapIndexed { c, existing ->
                if (r == row && c == col) {
                    existing.copy(value = next, isError = false)
                } else {
                    existing.copy(isError = false)
                }
            }
        }
        return copy(cells = newCells)
    }

    fun withCellUndo(row: Int, col: Int): GameBoard {
        val cell = cellAt(row, col)
        if (cell.isLocked) return this
        val prev = when (cell.value) {
            CellValue.EMPTY   -> CellValue.DIAMOND
            CellValue.CIRCLE  -> CellValue.EMPTY
            CellValue.DIAMOND -> CellValue.CIRCLE
        }
        val newCells = cells.mapIndexed { r, rowList ->
            rowList.mapIndexed { c, existing ->
                if (r == row && c == col) {
                    existing.copy(value = prev, isError = false)
                } else {
                    existing.copy(isError = false)
                }
            }
        }
        return copy(cells = newCells)
    }

    // Returns all violated edge constraints for UI highlighting
    fun violations(): Set<Edge> = edges.filter { edge ->
        val a = cellAt(edge.row, edge.col).value
        val (br, bc) = if (edge.axis == EdgeAxis.HORIZONTAL)
            edge.row to edge.col + 1 else edge.row + 1 to edge.col
        val b = cellAt(br, bc).value
        if (a == CellValue.EMPTY || b == CellValue.EMPTY) false
        else when (edge.constraint) {
            EdgeConstraint.MULTIPLY -> a == b          // must differ
            EdgeConstraint.EQUAL    -> a != b          // must match
        }
    }.toSet()

    fun isSolved(): Boolean {
        if (violations().isNotEmpty()) return false
        // Every row and column must have equal counts of CIRCLE and DIAMOND
        for (i in 0 until size) {
            val row = cells[i].map { it.value }
            val col = cells.map { it[i].value }
            if (!row.isBalanced() || !col.isBalanced()) return false
        }
        // No empty cells
        return cells.flatten().none { it.value == CellValue.EMPTY }
    }

    fun findErrors(): GameBoard {
        // Step 1: Row validation
        val afterRowValidation = cells.map { row -> row.assignError() }

        val rowCount = afterRowValidation.size
        val colCount = afterRowValidation[0].size

        val result = afterRowValidation.map { it.toMutableList() }.toMutableList()

        // Step 2: Column validation
        for (c in 0 until colCount) {
            val column = List(rowCount) { r -> result[r][c] }
            val validatedColumn = column.assignError()

            for (r in 0 until rowCount) {
                if (validatedColumn[r].isError) {
                    result[r][c] = result[r][c].copy(isError = true)
                }
            }
        }
        return copy(cells = result.map { it.toList() })
    }

    private fun List<Cell>.assignError(): List<Cell> {
        val result = map { it.copy(isError = false) }.toMutableList()

        val circles = count { it.value == CellValue.CIRCLE }
        val diamonds = count { it.value == CellValue.DIAMOND }

        // Rule 1: count overflow
        if (circles > size / 2 || diamonds > size / 2) {
            return result.map { it.copy(isError = true) }
        }

        // Rule 2: no 3 consecutive
        for (i in 0 until size - 2) {
            val v = result[i].value

            if (
                v != CellValue.EMPTY &&
                v == result[i + 1].value &&
                v == result[i + 2].value
            ) {
                for (j in i..i + 2) {
                    result[j] = result[j].copy(isError = true)
                }
            }
        }

        return result
    }

    private fun List<CellValue>.isBalanced(): Boolean {
        val circles  = count { it == CellValue.CIRCLE }
        val diamonds = count { it == CellValue.DIAMOND }
        return circles == diamonds
    }
}

// ── Puzzle factory ────────────────────────────────────────────────────────────

object PuzzleFactory {
    /** Recreates the puzzle visible in the screenshot. */
    fun createDefaultPuzzle(): GameBoard {
        val size = 6

        // Build empty grid, then mark locked/shaded cells
        val cells = List(size) { r ->
            List(size) { c ->
                Cell(row = r, col = c)
            }
        }.toMutableList().map { it.toMutableList() }

        fun lock(r: Int, c: Int, v: CellValue) {
            cells[r][c] = cells[r][c].copy(value = v, isLocked = true)
        }

        // Pre-filled circles (from screenshot)
        lock(0, 0, CellValue.CIRCLE)           // top-left
        lock(3, 3, CellValue.CIRCLE)           // center-right area
        lock(4, 2, CellValue.CIRCLE)           // row 4
        lock(5, 3, CellValue.CIRCLE)           // bottom row
        lock(5, 5, CellValue.CIRCLE)           // bottom-right

        // Pre-filled diamond
        lock(4, 3, CellValue.DIAMOND)          // row 4, next to circle


        val edges = listOf(
            // = between (1,2) and (1,3)  — horizontal
            Edge(1, 2, EdgeAxis.HORIZONTAL, EdgeConstraint.EQUAL),
            // × between (1,4) and (1,5)  — horizontal
            Edge(1, 4, EdgeAxis.HORIZONTAL, EdgeConstraint.MULTIPLY),
            // × between (4,1) and (4,2)  — horizontal
            Edge(4, 1, EdgeAxis.HORIZONTAL, EdgeConstraint.MULTIPLY),
            // × between (4,2) and (4,3)  — horizontal (between circle and diamond)
            Edge(4, 2, EdgeAxis.HORIZONTAL, EdgeConstraint.MULTIPLY),
        )

        return GameBoard(
            size = size,
            cells = cells.map { it.toList() },
            edges = edges,
        )
    }

    fun createEasyPuzzle(): GameBoard {
        val size = 6

        // Build empty grid, then mark locked/shaded cells
        val cells = List(size) { r ->
            List(size) { c ->
                Cell(row = r, col = c)
            }
        }.toMutableList().map { it.toMutableList() }

        fun lock(r: Int, c: Int, v: CellValue) {
            cells[r][c] = cells[r][c].copy(value = v, isLocked = true)
        }

        // Pre-filled cells (0 = CIRCLE / sun, 1 = DIAMOND / moon)
        lock(0, 1, CellValue.CIRCLE)
        lock(0, 2, CellValue.DIAMOND)
        lock(0, 5, CellValue.CIRCLE)
        lock(1, 2, CellValue.CIRCLE)
        lock(1, 4, CellValue.DIAMOND)
        lock(2, 3, CellValue.CIRCLE)
        lock(2, 4, CellValue.DIAMOND)
        lock(2, 5, CellValue.DIAMOND)
        lock(3, 2, CellValue.CIRCLE)
        lock(3, 4, CellValue.CIRCLE)
        lock(3, 5, CellValue.DIAMOND)
        lock(4, 0, CellValue.DIAMOND)
        lock(4, 5, CellValue.CIRCLE)
        lock(5, 4, CellValue.CIRCLE)
        lock(5, 5, CellValue.DIAMOND)

        val edges = listOf(
            // Vertical DIFF between (3,4) and (4,4)
            Edge(3, 4, EdgeAxis.VERTICAL, EdgeConstraint.MULTIPLY),
            // Vertical DIFF between (2,0) and (3,0)
            Edge(2, 0, EdgeAxis.VERTICAL, EdgeConstraint.MULTIPLY),
            // Horizontal DIFF between (0,3) and (0,4)
            Edge(0, 3, EdgeAxis.HORIZONTAL, EdgeConstraint.MULTIPLY),
            // Vertical EQUAL between (3,0) and (4,0)
            Edge(3, 0, EdgeAxis.VERTICAL, EdgeConstraint.EQUAL),
            // Horizontal DIFF between (0,0) and (0,1)
            Edge(0, 0, EdgeAxis.HORIZONTAL, EdgeConstraint.MULTIPLY)
        )

        return GameBoard(
            size = size,
            cells = cells.map { it.toList() },
            edges = edges,
        )
    }

    fun createMediumPuzzle(): GameBoard {
        val size = 6

        // Build empty grid, then mark locked/shaded cells
        val cells = List(size) { r ->
            List(size) { c ->
                Cell(row = r, col = c)
            }
        }.toMutableList().map { it.toMutableList() }

        fun lock(r: Int, c: Int, v: CellValue) {
            cells[r][c] = cells[r][c].copy(value = v, isLocked = true)
        }

        // Pre-filled cells (0 = CIRCLE / sun, 1 = DIAMOND / moon)
        lock(1, 0, CellValue.CIRCLE)
        lock(1, 2, CellValue.CIRCLE)
        lock(1, 3, CellValue.DIAMOND)
        lock(2, 3, CellValue.CIRCLE)
        lock(2, 4, CellValue.DIAMOND)
        lock(3, 1, CellValue.CIRCLE)
        lock(4, 2, CellValue.CIRCLE)
        lock(5, 3, CellValue.CIRCLE)
        lock(5, 4, CellValue.CIRCLE)
        lock(5, 5, CellValue.DIAMOND)

        val edges = listOf(
            // Horizontal EQUAL between (4,2) and (4,3)
            Edge(4, 2, EdgeAxis.HORIZONTAL, EdgeConstraint.EQUAL),
            // Horizontal DIFF between (3,2) and (3,3)
            Edge(3, 2, EdgeAxis.HORIZONTAL, EdgeConstraint.MULTIPLY)
        )

        return GameBoard(
            size = size,
            cells = cells.map { it.toList() },
            edges = edges,
        )
    }

    fun createHardPuzzle(): GameBoard {
        val size = 6

        // Build empty grid, then mark locked/shaded cells
        val cells = List(size) { r ->
            List(size) { c ->
                Cell(row = r, col = c)
            }
        }.toMutableList().map { it.toMutableList() }

        fun lock(r: Int, c: Int, v: CellValue) {
            cells[r][c] = cells[r][c].copy(value = v, isLocked = true)
        }

        // Pre-filled cells (0 = CIRCLE / sun, 1 = DIAMOND / moon)
        lock(5, 1, CellValue.DIAMOND)
        lock(5, 5, CellValue.DIAMOND)

        val edges = listOf(
            // Vertical EQUAL between (2,1) and (3,1)
            Edge(2, 1, EdgeAxis.VERTICAL, EdgeConstraint.EQUAL),
            // Vertical DIFF between (0,4) and (1,4)
            Edge(0, 4, EdgeAxis.VERTICAL, EdgeConstraint.MULTIPLY),
            // Vertical EQUAL between (1,0) and (2,0)
            Edge(1, 0, EdgeAxis.VERTICAL, EdgeConstraint.EQUAL),
            // Vertical DIFF between (0,0) and (1,0)
            Edge(0, 0, EdgeAxis.VERTICAL, EdgeConstraint.MULTIPLY),
            // Vertical DIFF between (4,2) and (5,2)
            Edge(4, 2, EdgeAxis.VERTICAL, EdgeConstraint.MULTIPLY),
            // Vertical EQUAL between (0,5) and (1,5)
            Edge(0, 5, EdgeAxis.VERTICAL, EdgeConstraint.EQUAL)
        )

        return GameBoard(
            size = size,
            cells = cells.map { it.toList() },
            edges = edges,
        )
    }
}
