package com.example.brainbites.presentation.GamesTab.Flow

import FakeFlowRepository
import com.example.brainbites.Fakes.TestDispatcherProvider
import com.example.brainbites.domain.models.CellPosition
import com.example.brainbites.presentation.viewModels.Flow.FlowAction
import com.example.brainbites.presentation.viewModels.Flow.FlowViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse


@OptIn(ExperimentalCoroutinesApi::class)
class FlowViewModelTest {

    private lateinit var viewModel: FlowViewModel
    private val testDispatcher = StandardTestDispatcher()


    @BeforeTest
    fun startUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = FlowViewModel(
            FakeFlowRepository(),
            TestDispatcherProvider(testDispatcher)
        )
    }

    @AfterTest
    fun tearDown(){
        Dispatchers.resetMain()
    }


    @Test
    fun `path must start at hint 1`() = runTest {
        advanceUntilIdle()
        viewModel.onAction(FlowAction.CellDragged(0, 0))
        assertEquals(CellPosition(0, 0), viewModel.uiState.value.path.first())
    }

    @Test
    fun `path cannot start at non-hint cell`() = runTest {
        advanceUntilIdle()
        viewModel.onAction(FlowAction.CellDragged(0, 1))
        assertTrue(viewModel.uiState.value.path.isEmpty())
    }

    @Test
    fun `path cannot start at hint 2`() = runTest {
        advanceUntilIdle()
        viewModel.onAction(FlowAction.CellDragged(2, 2))
        assertTrue(viewModel.uiState.value.path.isEmpty())
    }

    @Test
    fun `can move to adjacent Cell`() = runTest{
        advanceUntilIdle()
        viewModel.onAction(FlowAction.CellDragged(0, 0))
        viewModel.onAction(FlowAction.CellDragged(0, 1))

        assertEquals(2, viewModel.uiState.value.path.size)
    }

    @Test
    fun `cannot move to non-adjacent cell`() = runTest {
        advanceUntilIdle()
        viewModel.onAction(FlowAction.CellDragged(0, 0))
        viewModel.onAction(FlowAction.CellDragged(1, 1))

        assertEquals(1, viewModel.uiState.value.path.size)
    }

    @Test
    fun `cannot cross a wall`() = runTest{
        advanceUntilIdle()
        viewModel.onAction(FlowAction.CellDragged(0, 0))
        viewModel.onAction(FlowAction.CellDragged(0, 1))
        viewModel.onAction(FlowAction.CellDragged(1, 1))

        assertEquals(2, viewModel.uiState.value.path.size)
    }

    @Test
    fun `should fill all cells to win`() = runTest{
        advanceUntilIdle()
        viewModel.onAction(FlowAction.CellDragged(0, 0))
        viewModel.onAction(FlowAction.CellDragged(0, 1))
        viewModel.onAction(FlowAction.CellDragged(0, 2))
        viewModel.onAction(FlowAction.CellDragged(1, 2))
        viewModel.onAction(FlowAction.CellDragged(2, 2))

        assertEquals(5,viewModel.uiState.value.path.size)
        assertFalse(viewModel.uiState.value.isSolved)

    }

    @Test
    fun `puzzle solved when all cells filled`() = runTest {
        advanceUntilIdle()

        val moves = listOf(
            0 to 0, 0 to 1, 0 to 2,
            1 to 2, 1 to 1, 1 to 0,
            2 to 0, 2 to 1, 2 to 2
        )
        moves.forEach { (r, c) ->
            viewModel.onAction(FlowAction.CellDragged(r, c))
        }

        assertEquals(9, viewModel.uiState.value.path.size)
        assertTrue(viewModel.uiState.value.isSolved)
    }

    @Test
    fun`undo removes last cell form path`() = runTest {
        advanceUntilIdle()

        viewModel.onAction(FlowAction.CellDragged(0,0))
        viewModel.onAction(FlowAction.CellDragged(0,1))
        viewModel.onAction(FlowAction.Undo)

        assertEquals(1,viewModel.uiState.value.path.size)
        assertEquals(CellPosition(0,0),viewModel.uiState.value.path.first())
    }


    @Test
    fun `undo on empty path does nothing`() {
        viewModel.onAction(FlowAction.Undo)

        assertTrue(viewModel.uiState.value.path.isEmpty())
    }


    @Test
    fun `reset clears everything`() {
        viewModel.onAction(FlowAction.CellDragged(0, 0))
        viewModel.onAction(FlowAction.CellDragged(0, 1))
        viewModel.onAction(FlowAction.Reset)

        assertTrue(viewModel.uiState.value.path.isEmpty())
        assertFalse(viewModel.uiState.value.isSolved)
    }

}