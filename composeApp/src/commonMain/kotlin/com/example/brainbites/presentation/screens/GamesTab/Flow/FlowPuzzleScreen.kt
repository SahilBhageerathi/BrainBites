package com.example.brainbites.presentation.screens.GamesTab.Flow

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


data class Hint(val row: Int, val col: Int, val value: Int)
data class Wall(
    val row: Int,
    val col: Int,
    val direction: String  // "right", "bottom"
)

data class FlowPuzzle(
    val id: Int,
    val gridSize: Int,
    val hints: List<Hint>,
    val walls: List<Wall>
)

data class CellPosition(val row: Int, val col: Int)

// ─── Color Theme ────────────────────────────────────────────────────────────

object FlowColors {
    val Background = Color(0xFFF7F7F8)
    val GridBackground = Color(0xFFFFFFFF)
    val GridBorder = Color(0xFFE0E0E0)
    val CellBorder = Color(0xFFE8E8E8)
    val HintCircle = Color(0xFF1A1A1A)
    val HintText = Color(0xFFFFFFFF)
    val ActiveCell = Color(0xFFD4EAFF)
    val VisitedCell = Color(0xFFE8E8E8)
    val PathColor = Color(0xFF1A1A1A)
    val TextPrimary = Color(0xFF1A1A1A)
    val TextSecondary = Color(0xFF888888)
    val SuccessBackground = Color(0xFFE6F9E6)
    val SuccessText = Color(0xFF2E7D32)
    val ButtonBorder = Color(0xFFD0D0D0)
}


@Composable
fun FlowPuzzleScreen(
    puzzle: FlowPuzzle,
    path: List<CellPosition>,
    timerText: String,
    isSolved: Boolean,
    onCellDragged: (Int, Int) -> Unit,
    onUndo: () -> Unit,
    onReset: () -> Unit,
    onBack: () -> Unit
) {
    val hintMap = remember(puzzle) {
        puzzle.hints.associate { (it.row to it.col) to it.value }
    }
    val pathSet = remember(path) {
        path.map { it.row to it.col }.toSet()
    }
    val lastCell = path.lastOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FlowColors.Background)
    ) {

        TopBar(
            timerText = timerText,
            onBack = onBack,
            onReset = onReset
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            FlowGrid(
                gridSize = puzzle.gridSize,
                hintMap = hintMap,
                pathSet = pathSet,
                path = path,
                lastCell = lastCell,
                walls = puzzle.walls,
                onCellDragged = onCellDragged
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ── Cell Counter ──
            Text(
                text = "${path.size} / ${puzzle.gridSize * puzzle.gridSize}",
                fontSize = 14.sp,
                color = FlowColors.TextSecondary
            )

            // ── Win Message ──
            if (isSolved) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = FlowColors.SuccessBackground
                ) {
                    Text(
                        text = "Puzzle complete! $timerText",
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = FlowColors.SuccessText
                    )
                }
            }
        }

        // ── Bottom Buttons ──
        BottomButtons(
            undoEnabled = path.isNotEmpty() && !isSolved,
            onUndo = onUndo,
            onReset = onReset
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}


@Composable
private fun TopBar(
    timerText: String,
    onBack: () -> Unit,
    onReset: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = timerText,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = FlowColors.TextSecondary
        )

        Spacer(modifier = Modifier.weight(1f))

        // Title
        Text(
            text = "Flow",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = FlowColors.TextPrimary
        )

        Spacer(modifier = Modifier.weight(1f))

        // Timer
        Text(
            text = "RESET",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = FlowColors.TextSecondary
        )

    }
}


@Composable
private fun FlowGrid(
    gridSize: Int,
    hintMap: Map<Pair<Int, Int>, Int>,
    pathSet: Set<Pair<Int, Int>>,
    path: List<CellPosition>,
    lastCell: CellPosition?,
    walls: List<Wall>,
    onCellDragged: (Int, Int) -> Unit
) {
    val density = LocalDensity.current
    val wallSet = remember(walls) {
        walls.map { "${it.row},${it.col},${it.direction}" }.toSet()
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        val gridWidthPx = with(density) { maxWidth.toPx() }
        val cellSizePx = gridWidthPx / gridSize
        val cellSizeDp = with(density) { cellSizePx.toDp() }

        // ↓↓↓ REPLACE THIS ENTIRE Box ↓↓↓
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, FlowColors.CellBorder, RoundedCornerShape(16.dp))
                .background(FlowColors.GridBackground)
                .pointerInput(gridSize) {
                    awaitEachGesture {
                        var lastRow = -1
                        var lastCol = -1
                        val down = awaitFirstDown()
                        val col = (down.position.x / cellSizePx).toInt().coerceIn(0, gridSize - 1)
                        val row = (down.position.y / cellSizePx).toInt().coerceIn(0, gridSize - 1)
                        onCellDragged(row, col)
                        lastRow = row
                        lastCol = col

                        do {
                            val event = awaitPointerEvent()
                            event.changes.forEach { change ->
                                change.consume()
                                val c = (change.position.x / cellSizePx).toInt().coerceIn(0, gridSize - 1)
                                val r = (change.position.y / cellSizePx).toInt().coerceIn(0, gridSize - 1)
                                if (lastRow != -1 && (r != lastRow || c != lastCol)) {
                                    val dr = r - lastRow
                                    val dc = c - lastCol
                                    if (dr == 0 || dc == 0) {
                                        val stepR = if (dr != 0) dr / kotlin.math.abs(dr) else 0
                                        val stepC = if (dc != 0) dc / kotlin.math.abs(dc) else 0
                                        var cr = lastRow + stepR
                                        var cc = lastCol + stepC
                                        while (cr != r || cc != c) {
                                            onCellDragged(cr, cc)
                                            cr += stepR
                                            cc += stepC
                                        }
                                    }
                                    onCellDragged(r, c)
                                    lastRow = r
                                    lastCol = c
                                }
                            }
                        } while (event.changes.any { it.pressed })
                    }
                }
        ) {
            // Layer 1: White cells with borders and walls
            Column {
                for (row in 0 until gridSize) {
                    Row {
                        for (col in 0 until gridSize) {
                            FlowCell(
                                row = row, col = col,
                                gridSize = gridSize, cellSize = cellSizeDp,
                                isVisited = false, isActive = false,
                                hintValue = null, wallSet = wallSet
                            )
                        }
                    }
                }
            }

            // Layer 2: Blue path on top of cells
            PathOverlay(path = path, cellSizePx = cellSizePx)

            // Layer 3: Hint circles on top of path
            HintOverlay(hintMap = hintMap, cellSizePx = cellSizePx)
        }
    }
}

// ─── Single Cell ────────────────────────────────────────────────────────────

@Composable
private fun FlowCell(
    row: Int,
    col: Int,
    gridSize: Int,
    cellSize: androidx.compose.ui.unit.Dp,
    isVisited: Boolean,
    isActive: Boolean,
    hintValue: Int?,
    wallSet: Set<String>
) {
    Box(
        modifier = Modifier
            .size(cellSize)
            .background(Color(0xFFFFFFFF))
            .drawWithContent {
                drawContent()

                val thinWidth = 4.dp.toPx()
                val thickWidth = 15.dp.toPx()
                val borderColor = Color(0xFFDDDDDD)
                val wallColor = Color(0xFF1A1A1A)

                if (col < gridSize - 1) {
                    val hasWall = wallSet.contains("$row,$col,right")
                    drawLine(
                        color = if (hasWall) wallColor else borderColor,
                        start = Offset(size.width, 0f),
                        end = Offset(size.width, size.height),
                        strokeWidth = if (hasWall) thickWidth else thinWidth
                    )
                }

                if (row < gridSize - 1) {
                    val hasWall = wallSet.contains("$row,$col,bottom")
                    drawLine(
                        color = if (hasWall) wallColor else borderColor,
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = if (hasWall) thickWidth else thinWidth
                    )
                }
            },
        contentAlignment = Alignment.Center
    ) {

    }
}


@Composable
private fun BoxScope.HintOverlay(
    hintMap: Map<Pair<Int, Int>, Int>,
    cellSizePx: Float
) {
    val density = LocalDensity.current

    hintMap.forEach { (pos, value) ->
        val row = pos.first
        val col = pos.second
        val offsetX = with(density) { (col * cellSizePx + cellSizePx * 0.15f).toDp() }
        val offsetY = with(density) { (row * cellSizePx + cellSizePx * 0.15f).toDp() }
        val size = with(density) { (cellSizePx * 0.7f).toDp() }

        Box(
            modifier = Modifier
                .offset(x = offsetX, y = offsetY)
                .size(size)
                .background(Color(0xFF1A1A1A), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value.toString(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}


// ─── Path Overlay (Canvas) ──────────────────────────────────────────────────

@Composable
private fun BoxScope.PathOverlay(
    path: List<CellPosition>,
    cellSizePx: Float
) {
    if (path.isEmpty()) return

    Canvas(modifier = Modifier.fillMaxSize()) {
        val padding = cellSizePx * 0.25f
        val pathColor = Color(0xFF4AADE8)
        val halfPath = cellSizePx / 2 - padding
        val cornerRadius = androidx.compose.ui.geometry.CornerRadius(
            halfPath, halfPath
        )


        // Draw connectors FIRST (so rounded rects cover the joints)
        for (i in 0 until path.size - 1) {
            val cell = path[i]
            val next = path[i + 1]
            val cx = cell.col * cellSizePx + cellSizePx / 2
            val cy = cell.row * cellSizePx + cellSizePx / 2
            val nx = next.col * cellSizePx + cellSizePx / 2
            val ny = next.row * cellSizePx + cellSizePx / 2

            if (cell.row == next.row) {
                val left = minOf(cx, nx)
                drawRect(
                    color = pathColor,
                    topLeft = Offset(left, cy - halfPath),
                    size = androidx.compose.ui.geometry.Size(
                        kotlin.math.abs(nx - cx), halfPath * 2
                    )
                )
            }

            if (cell.col == next.col) {
                val top = minOf(cy, ny)
                drawRect(
                    color = pathColor,
                    topLeft = Offset(cx - halfPath, top),
                    size = androidx.compose.ui.geometry.Size(
                        halfPath * 2, kotlin.math.abs(ny - cy)
                    )
                )
            }
        }

        // Draw circles at joints to smooth corners
        for (i in 1 until path.size - 1) {
            val cell = path[i]
            val prev = path[i - 1]
            val next = path[i + 1]

            // If direction changes (turn), draw a circle to smooth the corner
            val isTurn = prev.row != next.row && prev.col != next.col
            if (isTurn) {
                val cx = cell.col * cellSizePx + cellSizePx / 2
                val cy = cell.row * cellSizePx + cellSizePx / 2
                drawCircle(
                    color = pathColor,
                    radius = halfPath,
                    center = Offset(cx, cy)
                )
            }
        }

        // Draw rounded rect for each cell ON TOP of connectors
        path.forEach { cell ->
            val cx = cell.col * cellSizePx + cellSizePx / 2
            val cy = cell.row * cellSizePx + cellSizePx / 2

            drawRoundRect(
                color = pathColor,
                topLeft = Offset(cx - halfPath, cy - halfPath),
                size = androidx.compose.ui.geometry.Size(halfPath * 2, halfPath * 2),
                cornerRadius = cornerRadius
            )
        }
    }
}

// ─── Bottom Buttons ─────────────────────────────────────────────────────────

@Composable
private fun BottomButtons(
    undoEnabled: Boolean,
    onUndo: () -> Unit,
    onReset: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Undo
        OutlinedButton(
            onClick = onUndo,
            enabled = undoEnabled,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = FlowColors.TextPrimary,
                disabledContentColor = FlowColors.TextSecondary
            )
        ) {
            Text(
                text = "Undo",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Hint
        OutlinedButton(
            onClick = { /* TODO: Hint logic */ },
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = FlowColors.TextPrimary
            )
        ) {
            Text(
                text = "Hint",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}