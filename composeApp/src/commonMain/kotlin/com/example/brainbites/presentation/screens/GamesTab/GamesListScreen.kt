package com.example.brainbites.presentation.screens.GamesTab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GamesListScreen(onPlayClick: () -> Unit = {}) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F5))
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Text(
                text = "Puzzles",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF2E2E8C),
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        items(puzzles) { puzzle ->
            PuzzleCard(puzzle, onPlayClick = onPlayClick)
        }
    }
}


private data class PuzzleItem(
    val title: String,
    val description: String,
    val difficulty: String,
    val difficultyColor: Color,
)

private val puzzles = listOf(
    PuzzleItem("Tango", "Place circles and diamonds so no row or column repeats.", "Expert", Color(0xFF8B3A00)),
//    PuzzleItem("Zip", "Connect all dots in a single path without crossing.", "Medium", Color(0xFF2E7D32)),
//    PuzzleItem("Queens", "Place queens so none attack each other.", "Hard", Color(0xFF1565C0)),
//    PuzzleItem("Crossclimb", "Fill the ladder so each rung is a valid word.", "Easy", Color(0xFF6A1B9A)),
)



@Composable
private fun PuzzleCard(puzzle: PuzzleItem, onPlayClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF6F2FA)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = puzzle.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E2E8C)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = puzzle.description,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(puzzle.difficultyColor.copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = puzzle.difficulty,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = puzzle.difficultyColor
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Button(
                onClick = onPlayClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E2E8C))
            ) {
                Text("Play", fontSize = 14.sp)
            }
        }
    }
}