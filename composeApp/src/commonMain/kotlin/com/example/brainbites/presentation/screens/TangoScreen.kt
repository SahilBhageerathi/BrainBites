package com.example.brainbites.presentation.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import brainbites.composeapp.generated.resources.Res
import brainbites.composeapp.generated.resources.bar_chart_icon
import brainbites.composeapp.generated.resources.clear_all_icon
import brainbites.composeapp.generated.resources.hint_icon
import brainbites.composeapp.generated.resources.settings_icon
import brainbites.composeapp.generated.resources.timer_icon
import brainbites.composeapp.generated.resources.undo_icon
import com.example.brainbites.platform.PlatformLifecycle
import com.example.brainbites.platform.getPlatform
import com.example.brainbites.presentation.models.Cell
import com.example.brainbites.presentation.models.CellValue
import com.example.brainbites.presentation.models.Edge
import com.example.brainbites.presentation.models.EdgeAxis
import com.example.brainbites.presentation.models.EdgeConstraint
import com.example.brainbites.presentation.models.GameBoard
import com.example.brainbites.presentation.viewModels.TangoViewModel
import com.example.brainbites.utils.GameTimer
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

private val BackgroundColor = Color(0xFFF0F0F5)
private val CardColor = Color(0xFFF6F2FA)
private val GridLineColor = Color(0xFFDDDDE8)
private val ShadedCellColor = Color(0xFFE8E8F0)
private val ErrorCellColor = Color(0xFFFF0000)
private val CircleColor = Color(0xFF2E2E8C)
private val DiamondColor = Color(0xFF2DAA78)
private val ViolationColor = Color(0xFFE53935)
private val ConstraintColor = Color(0xFF888899)
private val SolvedColor = Color(0xFF2DAA78)

@Composable
fun TangoGameScreen() {
    val viewModel: TangoViewModel = koinInject()
    val state by viewModel.uiState.collectAsState()
    val timer by viewModel.timeElapsed.collectAsState()

    val lifecycle = remember { PlatformLifecycle() }

    DisposableEffect(Unit) {

        lifecycle.register(viewModel)
        viewModel.onStartGame()

        onDispose {
            lifecycle.unregister()
            viewModel.onPauseGame()
        }
    }

    if (state.isLoading) {
        LoadingScreen()
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(WindowInsets.safeDrawing.asPaddingValues())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
//            Spacer(Modifier.height(12.dp))
//            TopBar()

//            Spacer(Modifier.height(12.dp))
            DifficultyTimerCard(time = GameTimer.formatTime(timer))

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CardColor),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    GameBoardView(
                        board = state.board,
                        violations = state.violations,
                        onCellClick = { r, c -> viewModel.onCellClicked(r, c) },
                    )
                }
            }

            ActionButtons(viewModel)

            if (state.isSolved) {
                PuzzleSolvedScreen(vm = viewModel)
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun TopBar() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(Res.drawable.bar_chart_icon),
            contentDescription = null,
            tint = Color(0xFF5B5BD6)
        )

        Text(
            text = "Lavender Logic",
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
        )

        Icon(
            painter = painterResource(Res.drawable.settings_icon),
            contentDescription = null
        )
    }
}

@Composable
fun DifficultyTimerCard(
    difficulty: String = "Expert",
    time: String = "04:12",
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Card(
            modifier = Modifier
                .weight(1f)
                .height(76.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardColor)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "DIFFICULTY",
                    fontSize = 12.sp,
                    letterSpacing = 1.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = difficulty,
                        fontSize = 20.sp,
                        color = Color(0xFF4B5BD3),
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color(0xFF8B3A00), CircleShape)
                    )
                }
            }
        }

        Card(
            modifier = Modifier
                .weight(1f)
                .height(76.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardColor)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.timer_icon),
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = time,
                    fontSize = 18.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun GameBoardView(
    board: GameBoard,
    violations: Set<Edge>,
    onCellClick: (Int, Int) -> Unit,
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        val ratio = 0.4f
        val availableWidth = maxWidth - 4.dp
        val cellSize = availableWidth / (board.size + (board.size - 1) * ratio)
        val constraintSize = cellSize * ratio
        val totalSize = cellSize * board.size + constraintSize * (board.size - 1)

        Box(modifier = Modifier.size(totalSize)) {
            for (r in 0 until board.size) {
                for (c in 0 until board.size) {
                    val cell = board.cellAt(r, c)
                    val x = c * (cellSize + constraintSize)
                    val y = r * (cellSize + constraintSize)
                    CellView(
                        cell = cell,
                        modifier = Modifier
                            .offset(x = x, y = y)
                            .size(cellSize),
                        onClick = { onCellClick(r, c) },
                    )
                }
            }

            for (edge in board.edges.filter { it.axis == EdgeAxis.HORIZONTAL }) {
                val violated = edge in violations
                val r = edge.row
                val c = edge.col
                val x = c * (cellSize + constraintSize) + cellSize
                val y = r * (cellSize + constraintSize) + (cellSize - constraintSize) / 2
                ConstraintLabel(
                    edge = edge,
                    violated = violated,
                    modifier = Modifier
                        .offset(x = x, y = y)
                        .size(constraintSize, constraintSize),
                )
            }

            for (edge in board.edges.filter { it.axis == EdgeAxis.VERTICAL }) {
                val violated = edge in violations
                val r = edge.row
                val c = edge.col
                val x = c * (cellSize + constraintSize) + (cellSize - constraintSize) / 2
                val y = r * (cellSize + constraintSize) + cellSize
                ConstraintLabel(
                    edge = edge,
                    violated = violated,
                    modifier = Modifier
                        .offset(x = x, y = y)
                        .size(constraintSize, constraintSize),
                )
            }
        }
    }
}

@Composable
private fun CellView(
    cell: Cell,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(dampingRatio = 0.4f),
        label = "cellScale",
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(6.dp))
            .background(
                if (cell.isError) ErrorCellColor
                else if (cell.isLocked) ShadedCellColor
                else Color.Transparent
            )
            .border(
                width = 1.dp,
                color = GridLineColor,
                shape = RoundedCornerShape(6.dp),
            )
            .then(
                if (!cell.isLocked) Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick,
                ) else Modifier
            ),
    ) {
        when (cell.value) {
            CellValue.CIRCLE -> CirclePiece(locked = cell.isLocked)
            CellValue.DIAMOND -> DiamondPiece(locked = cell.isLocked)
            CellValue.EMPTY -> Unit
        }
    }
}

@Composable
private fun CirclePiece(locked: Boolean) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(CircleColor.copy(alpha = if (locked) 1f else 0.85f)),
    )
}

@Composable
private fun DiamondPiece(locked: Boolean) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(34.dp),
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .background(
                    color = DiamondColor.copy(alpha = if (locked) 1f else 0.85f),
                    shape = RoundedCornerShape(4.dp),
                )
                .graphicsLayer { rotationZ = 45f },
        )
    }
}

@Composable
private fun ConstraintLabel(
    edge: Edge,
    violated: Boolean,
    modifier: Modifier = Modifier,
) {
    val color by animateColorAsState(
        targetValue = if (violated) ViolationColor else ConstraintColor,
        label = "constraintColor",
    )
    val symbol = when (edge.constraint) {
        EdgeConstraint.MULTIPLY -> "×"
        EdgeConstraint.EQUAL    -> "="
    }
    val verticalOffset = if (getPlatform().name.contains("iOS")) (-1).dp else 0.dp
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            text = symbol,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            textAlign = TextAlign.Center,
            modifier = Modifier.offset(y = verticalOffset)
        )
    }
}

@Composable
fun ActionButtons(vm: TangoViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ActionItem(
            label = "UNDO",
            icon = painterResource(Res.drawable.undo_icon),
            modifier = Modifier.weight(1f),
            onClick = { vm.onUndoClick() }
        )

        ActionItem(
            label = "HINT",
            icon = painterResource(Res.drawable.hint_icon),
            modifier = Modifier.weight(1f),
            onClick = { }
        )

        ActionItem(
            label = "CLEAR",
            icon = painterResource(Res.drawable.clear_all_icon),
            modifier = Modifier.weight(1f),
            onClick = { vm.resetPuzzle() }
        )
    }
}

@Composable
fun ActionItem(
    label: String,
    icon: Painter,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .height(64.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(color = CardColor)
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Icon(icon, contentDescription = label)
        Spacer(Modifier.height(4.dp))
        Text(label, fontSize = 12.sp)
    }
}

@Composable
fun SubmitButton() {
    Button(
        onClick = { },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(18.dp),
        elevation = ButtonDefaults.buttonElevation(6.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF5B5BD6)
        )
    ) {
        Text(
            "Submit Solution",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

//@Composable
//private fun CompleteScreen() {
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        shape = RoundedCornerShape(20.dp),
//        colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF7EF))
//    ) {
//        Text(
//            text = "Puzzle Solved!",
//            modifier = Modifier.padding(16.dp),
//            color = SolvedColor,
//            fontWeight = FontWeight.Bold
//        )
//    }
//}

@Preview
@Composable
fun TangoGamePreview() {
    TangoGameScreen()
}