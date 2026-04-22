package com.example.brainbites.presentation.viewModels.Tango

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.viewModelScope
import brainbites.composeapp.generated.resources.Res
import com.example.brainbites.core.utils.AppConstants.TANGO_GAME_NAME
import com.example.brainbites.domain.interfaces.SolvedGamesRepo
import com.example.brainbites.platform.Logger
import com.example.brainbites.platform.getInt
import com.example.brainbites.platform.putInt
import com.example.brainbites.presentation.common.Stack
import com.example.brainbites.presentation.models.PuzzleFactory
import com.example.brainbites.presentation.models.TangoPuzzleResponse
import com.example.brainbites.presentation.models.toGameBoard
import com.example.brainbites.presentation.viewModels.AbstractViewModel
import com.example.brainbites.utils.GameTimer
import com.example.brainbites.utils.AppLifecycleObserver
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

private const val TAG = "TangoViewModel"

class TangoViewModel(
    val dataStore: DataStore<Preferences>,
    val timer: GameTimer,
    val solvedGamesRepo: SolvedGamesRepo
) : AbstractViewModel(), AppLifecycleObserver {
    private val _uiState = MutableStateFlow(TangoUiState(isLoading = true))
    val uiState: StateFlow<TangoUiState> = _uiState.asStateFlow()
    val scope = viewModelScope
    val timeElapsed = timer.elapsedTime
    var lastIndex: Int = 0
    var puzzles: List<TangoPuzzleResponse>? = null
    val movesStack: Stack<Pair<Int, Int>> = Stack()

    init {
        scope.launch {
            puzzles = getPuzzles()
            val totalPuzzles = puzzles?.size ?: 0
            val savedIndex = dataStore.getInt("last_puzzle_index", default = 0)

            lastIndex = if (totalPuzzles > 0) {
                savedIndex.coerceIn(0, totalPuzzles - 1)
            } else {
                0
            }
            loadPuzzle(lastIndex)
        }
    }
    private var errorJob: Job? = null
    fun onCellClicked(row: Int, col: Int) {
        val current = _uiState.value
        if (current.isSolved) return

        movesStack.push(Pair(row, col))
        val newBoard = current.board.withCellCycled(row, col)
        val violations = newBoard.violations()
        val solved = newBoard.isSolved()

        if (solved) {
            onGameSolved()
        }

        _uiState.value = current.copy(
            board = newBoard,
            violations = violations,
            isSolved = solved,
            moveCount = current.moveCount + 1,
        )
        scheduleErrorJob()
    }

    fun scheduleErrorJob() {
        errorJob?.cancel()
        errorJob = scope.launch {
            delay(500)
            val current = _uiState.value
            _uiState.value = current.copy(
                board = current.board.findErrors(),
                violations = current.violations,
                isSolved = current.isSolved
            )
        }
    }

    fun resetPuzzle() {
        errorJob?.cancel()
        loadPuzzle(lastIndex)
    }

    fun loadPuzzle(index: Int) {
        Logger.d(TAG, "Loading puzzle at index: $index")
        lastIndex = index
        movesStack.clear()
        timer.reset()
        timer.start(scope)

        val nextPuzzle = puzzles?.getOrNull(index)
        if (nextPuzzle == null) {
            Logger.d(TAG, "Puzzle at index $index not found.")
        }

        val board = nextPuzzle?.toGameBoard() ?: PuzzleFactory.createDefaultPuzzle()
        _uiState.value = TangoUiState(board = board, difficulty = nextPuzzle?.difficulty)
    }

    suspend fun loadPuzzlesJson(): String? {
        val bytes = Res.readBytes("files/tango_puzzles.json")
        return bytes.decodeToString()
    }

    suspend fun getPuzzles(): List<TangoPuzzleResponse>? {
        val jsonString = loadPuzzlesJson()
        return try {
            Json.decodeFromString(jsonString ?: "[]")
        } catch (e: Exception) {
            Logger.e(TAG, "Error decoding puzzles: ${e.message}")
            null
        }
    }

    override fun onPlayNext() {
        scope.launch {
            val total = puzzles?.size ?: 0
            if (total == 0) return@launch

            val nextIndex = (lastIndex + 1) % total
            loadPuzzle(nextIndex)
            dataStore.putInt("last_puzzle_index", nextIndex)
        }
    }

    override fun onLeaderboardClick() {
        Logger.d(TAG, "Leaderboard clicked - feature not implemented yet")
    }

    fun onUndoClick() {
        if (movesStack.isEmpty()) return
        val row = movesStack.peek().first
        val col = movesStack.peek().second
        movesStack.pop()
        val current = _uiState.value
        val newBoard = current.board.withCellUndo(row, col)
        val violations = newBoard.violations()
        val solved = newBoard.isSolved()
        _uiState.value = current.copy(
            board = newBoard,
            violations = violations,
            isSolved = solved,
            moveCount = current.moveCount - 1,
        )
        scheduleErrorJob()
    }

    fun onGameSolved() {
        val solveTime = timeElapsed.value
        val solvedLevel = lastIndex
        scope.launch {
            solvedGamesRepo.insertGame(
                level = solvedLevel.toLong(),
                gameName = TANGO_GAME_NAME,
                time = solveTime
            )

            // Advance progress for NEXT session
            val total = puzzles?.size ?: 0
            if (total > 0) {
                val nextIndex = (solvedLevel + 1) % total
                dataStore.putInt("last_puzzle_index", nextIndex)
            }
            onEndGame()
            Logger.d(TAG, "Puzzle $solvedLevel solved!")
        }
    }

    fun onStartGame() {
        timer.start(scope)
    }

    fun onPauseGame() {
        timer.pause()
    }

    fun onResumeGame() {
        timer.resume(scope)
    }

    fun onEndGame() {
        timer.stop()
    }

    override fun onForeground() {
        onResumeGame()
    }

    override fun onBackground() {
        onPauseGame()
    }
}
