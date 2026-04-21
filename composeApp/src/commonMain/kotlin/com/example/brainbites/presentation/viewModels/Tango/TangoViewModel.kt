package com.example.brainbites.presentation.viewModels.Tango

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.viewModelScope
import brainbites.composeapp.generated.resources.Res
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
    val timer: GameTimer
) : AbstractViewModel(), AppLifecycleObserver {
    private val _uiState = MutableStateFlow(TangoUiState(isLoading = true))
    val uiState: StateFlow<TangoUiState> = _uiState.asStateFlow()
    val scope = viewModelScope
    val timeElapsed = timer.elapsedTime
    var lastIndex: Int = -1
    var puzzles: List<TangoPuzzleResponse>? = null
    val movesStack: Stack<Pair<Int, Int>> = Stack()

    init {
        scope.launch {
            puzzles = getPuzzles()
            lastIndex = dataStore.getInt("last_puzzle_index")
            loadPuzzle(lastIndex + 1)
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
        timer.reset()

        loadPuzzle(lastIndex + 1)
    }

    fun loadPuzzle(index: Int) {
        Logger.d(TAG, "Loaded last puzzle index: $lastIndex")
        movesStack.clear()
        timer.reset()
        timer.start(scope)
        val nextPuzzle = puzzles?.get(lastIndex + 1)
        val board = nextPuzzle?.toGameBoard() ?: PuzzleFactory.createDefaultPuzzle()
        _uiState.value = TangoUiState(board = board, difficulty = nextPuzzle?.difficulty)
    }

    suspend fun loadPuzzlesJson(): String? {
        val bytes = Res.readBytes("files/tango_puzzles.json")
        return bytes.decodeToString()
    }

    suspend fun getPuzzles(): List<TangoPuzzleResponse>? {
        val jsonString = loadPuzzlesJson()
        return Json.decodeFromString(jsonString ?: "[]")
    }

    override fun onPlayNext() {
        scope.launch {
            puzzles?.let {
                lastIndex++
                timer.reset()
                val nextPuzzle = it.getOrNull(lastIndex)
                if (nextPuzzle != null) {
                    _uiState.value = TangoUiState(board = nextPuzzle.toGameBoard(), difficulty = nextPuzzle.difficulty)
                    Logger.d(TAG, "Playing next puzzle at index: $lastIndex")
                    onStartGame()
                } else {
                    Logger.d(TAG, "No more puzzles available at index: $lastIndex")
                }
            }
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
        scope.launch {
            lastIndex++
            dataStore.putInt("last_puzzle_index", lastIndex)
            onEndGame()
            Logger.d(TAG, "Puzzle solved! Updated last puzzle index to: $lastIndex")
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