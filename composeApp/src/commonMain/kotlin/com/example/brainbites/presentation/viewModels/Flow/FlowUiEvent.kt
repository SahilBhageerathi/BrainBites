package com.example.brainbites.presentation.viewModels.Flow

// User actions INTO ViewModel
sealed class FlowAction {
    data class CellDragged(val row: Int, val col: Int) : FlowAction()
    object Undo : FlowAction()
    object Reset : FlowAction()
}

// One-time events OUT to UI
sealed class FlowUiEvent {
    data class ShowSnackBar(val message: String) : FlowUiEvent()
    object PuzzleSolved : FlowUiEvent()
}