package com.example.brainbites.presentation.viewModels.Flow

import androidx.lifecycle.viewModelScope
import com.example.brainbites.presentation.screens.GamesTab.Flow.CellPosition
import com.example.brainbites.presentation.screens.GamesTab.Flow.FlowPuzzle
import com.example.brainbites.presentation.screens.GamesTab.Flow.Hint
import com.example.brainbites.presentation.screens.GamesTab.Flow.Wall
import com.example.brainbites.presentation.viewModels.AbstractViewModel
import com.example.brainbites.utils.AppLifecycleObserver
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.abs

class FlowViewModel : AbstractViewModel(), AppLifecycleObserver {
    private val _uiState = MutableStateFlow(
        FlowUiState(
            isLoading = false,
            puzzle = loadPuzzle()
        )
    )
    val uiState: StateFlow<FlowUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<FlowUiEvent>()
    val uiEvent: SharedFlow<FlowUiEvent> = _uiEvent.asSharedFlow()


    private var hintMap: Map<Pair<Int, Int>, Int> = emptyMap()
    private var hintValueMap: Map<Int, Pair<Int, Int>> = emptyMap()
    private var wallSet: Set<String> = emptySet()
    private var gridSize = 0

    fun onAction(action: FlowAction) {
        when (action) {
            is FlowAction.CellDragged -> { handleCellDragged(action.row,action.col) }
            FlowAction.Undo -> { handleUndo() }
            FlowAction.Reset -> { handleReset() }
        }
    }

    init {
        val puzzle = loadPuzzle()
        gridSize = puzzle.gridSize
        hintMap = puzzle.hints.associate { (it.row to it.col) to it.value }
        hintValueMap = puzzle.hints.associate { it.value to (it.row to it.col) }
        wallSet = puzzle.walls.map { "${it.row},${it.col},${it.direction}" }.toSet()

        _uiState.value = FlowUiState(
            isLoading = false,
            puzzle = puzzle
        )
    }

    private fun loadPuzzle(): FlowPuzzle{
        return FlowPuzzle(
            id = 1,
            gridSize = 6,
            hints = listOf(
                Hint(row = 3, col = 3, value = 1),
                Hint(row = 1, col = 1, value = 2),
                Hint(row = 2, col = 2, value = 3),
                Hint(row = 4, col = 4, value = 4),
                Hint(row = 3, col = 0, value = 5),
                Hint(row = 2, col = 5, value = 6)
            ),
            walls = listOf(
                // Top horizontal wall (row 1, cols 1-4)
                Wall(0, 1, "bottom"),
                Wall(0, 2, "bottom"),
                Wall(0, 3, "bottom"),
                Wall(0, 4, "bottom"),
                // Left vertical wall (rows 1-4, col 1)
                Wall(1, 0, "right"),
                Wall(3, 0, "right"),
                Wall(4, 0, "right"),
                // Right vertical wall (rows 1-4, col 5)
                Wall(1, 4, "right"),
                Wall(2, 4, "right"),
                Wall(4, 4, "right"),
                // Bottom horizontal wall (row 4, cols 1-4)
                Wall(4, 1, "bottom"),
                Wall(4, 2, "bottom"),
                Wall(4, 3, "bottom"),
                Wall(4, 4, "bottom"),
                // Inner walls
                Wall(1,2,"bottom"),
                Wall(1,3,"bottom"),
                Wall(2,2,"right"),
                Wall(3,2,"right"),
                Wall(3,2,"bottom"),
                Wall(3,3,"bottom"),

                )
        )
    }

    private fun handleCellDragged(row: Int, col: Int) {
        val state = _uiState.value
        if (state.isSolved) return

        val path = state.path
        val cell = CellPosition(row, col)

        // Dragging backwards — undo
        if (path.size >= 2 && path[path.size - 2] == cell) {
            handleUndo()
            return
        }

        // Already in path — ignore
        if (path.any { it.row == row && it.col == col }) return

        // First cell must be hint "1"
        if (path.isEmpty()) {
            if (hintMap[row to col] != 1) return
        }

        // Must be adjacent to last cell
        if (path.isNotEmpty()) {
            val last = path.last()
            if (!isAdjacent(last.row, last.col, row, col)) return
        }

        // Check wall between cells
        if (path.isNotEmpty()) {
            val last = path.last()
            if (hasWallBetween(last.row, last.col, row, col)) return
        }

        // Hint ordering check
        val hintAtCell = hintMap[row to col]
        if (hintAtCell != null) {
            // Find which hint we need next
            val nextHintNeeded = findNextHint(path)
            // If this cell has a hint, it must be the NEXT expected hint
            if (hintAtCell != nextHintNeeded) return
        }

        // Add cell
        val newPath = path + cell
        _uiState.value = state.copy(path = newPath)

        // Check win
        if (newPath.size == gridSize * gridSize) {
            _uiState.value = _uiState.value.copy(isSolved = true)
            viewModelScope.launch {
                _uiEvent.emit(FlowUiEvent.PuzzleSolved)
            }
        }
    }

    private fun findNextHint(path: List<CellPosition>): Int {
        // Check which hints have already been visited
        var lastVisitedHint = 0
        for (cell in path) {
            val hint = hintMap[cell.row to cell.col]
            if (hint != null && hint > lastVisitedHint) {
                lastVisitedHint = hint
            }
        }
        return lastVisitedHint + 1
    }

    private fun handleUndo() {
        val state = _uiState.value
        if (state.path.isEmpty() || state.isSolved) return
        _uiState.value = state.copy(path = state.path.dropLast(1))
    }

    private fun handleReset() {
        _uiState.value = _uiState.value.copy(
            path = emptyList(),
            isSolved = false
        )
    }

    private fun isAdjacent(r1: Int, c1: Int, r2: Int, c2: Int): Boolean {
        return abs(r1 - r2) + abs(c1 - c2) == 1
    }

    private fun hasWallBetween(r1: Int, c1: Int, r2: Int, c2: Int): Boolean {
        if (r1 == r2 && c2 == c1 + 1) return wallSet.contains("$r1,$c1,right")
        if (r1 == r2 && c2 == c1 - 1) return wallSet.contains("$r2,$c2,right")
        if (c1 == c2 && r2 == r1 + 1) return wallSet.contains("$r1,$c1,bottom")
        if (c1 == c2 && r2 == r1 - 1) return wallSet.contains("$r2,$c2,bottom")
        return false
    }

    override fun onPlayNext() {
        TODO("Not yet implemented")
    }

    override fun onLeaderboardClick() {
        TODO("Not yet implemented")
    }

    override fun onForeground() {
        TODO("Not yet implemented")
    }

    override fun onBackground() {
        TODO("Not yet implemented")
    }

}