package com.example.brainbites.presentation.screens.GamesTab.Tango

import brainbites.composeapp.generated.resources.Res
import brainbites.composeapp.generated.resources.close_icon
import org.jetbrains.compose.resources.painterResource




import androidx.compose.ui.draw.drawBehind
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.brainbites.presentation.viewModels.AbstractViewModel
import com.example.brainbites.presentation.viewModels.Tango.TangoViewModel
import org.koin.compose.koinInject

private val ScreenBg = Color(0xFFF8F5FF)
private val PrimaryPurple = Color(0xFF5B4BE1)
private val AccentPink = Color(0xFFFF4FC3)
private val TextMain = Color(0xFF37364A)
private val TextSub = Color(0xFF6C6A7F)
private val CardWhite = Color(0xFFFDFCFF)
private val CardShadow = Color(0x1A5B4BE1)
private val BlueBorder = Color(0xFF2FA2FF)
private val BlueSoft = Color(0xFFEAF5FF)
private val DividerSoft = Color(0xFFE7E1F6)
private val SelectedNavBg = Color(0xFFE7E3FF)
private val SelectedNavText = Color(0xFF5C4BE4)
private val NavText = Color(0xFF3E3D4F)



@Composable
fun PuzzleSolvedScreen(
    modifier: Modifier = Modifier,
    timeText: String = "04:12",
    title: String = "Puzzle Solved",
    onClose: () -> Unit = {},
    vm : AbstractViewModel
) {



    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(ScreenBg)
    ) {
        val horizontalPadding = 22.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = horizontalPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(10.dp))

            TopBar(
                title = title,
                onClose = onClose,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(18.dp))

            PartyEmojiCard()

            Spacer(Modifier.height(22.dp))

            Text(
                text = "Level Complete",
                color = TextMain,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 30.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "You navigated the Lavender Logic with\nease.",
                color = TextSub,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 15.sp,
                    lineHeight = 20.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(22.dp))


            TimeCard(timeText = timeText)

            Spacer(Modifier.height(18.dp))


            PrimaryActionButton(
                text = "Play Next",
                onClick = {
                    vm.onPlayNext()
                }
            )

            Spacer(Modifier.height(14.dp))

            SecondaryActionButton(
                text = "Show Leaderboard",
                onClick = {vm.onLeaderboardClick()}
            )

            Spacer(Modifier.weight(1f))

        }
    }
}

@Composable
private fun TopBar(
    title: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.height(46.dp),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = onClose,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                painter = painterResource(Res.drawable.close_icon),
                contentDescription = "Close",
                tint = TextMain
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(
                text = title,
                color = TextMain,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            )
            Spacer(Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .width(26.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(BlueBorder)
            )
        }
    }
}

@Composable
private fun PartyEmojiCard() {
    Box(
        modifier = Modifier
            .size(168.dp)
            .clip(RoundedCornerShape(42.dp))
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFF2EEFF), Color(0xFFFDFBFF)),
                    radius = 260f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // soft glow behind the emoji card
        Box(
            modifier = Modifier
                .size(118.dp)
                .clip(RoundedCornerShape(36.dp))
                .background(Color.White)
        )
        Text(
            text = "🥳",
            fontSize = 56.sp
        )
    }
}

@Composable
private fun AccentDivider() {
    Box(
        modifier = Modifier
            .width(26.dp)
            .height(3.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(AccentPink)
    )
}

@Composable
private fun TimeCard(timeText: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xFFEDEAF7))
            .padding(18.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "⏱",
                    fontSize = 16.sp
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "TIME",
                    color = PrimaryPurple,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        fontSize = 13.sp
                    )
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = timeText,
                color = TextMain,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 26.sp
                )
            )
        }
    }
}



@Composable
private fun PrimaryActionButton(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF6B5CF2), PrimaryPurple)
                )
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = text,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "→",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            )
        }
    }
}

@Composable
private fun SecondaryActionButton(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(Color(0xFFE5E2F1))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "▥",
                color = SelectedNavText,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = text,
                color = SelectedNavText,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            )
        }
    }
}


private fun Modifier.dashedRoundedBorder(
    color: Color,
    strokeWidth: Dp,
    dashLength: Dp,
    gapLength: Dp,
    cornerRadius: Dp,
): Modifier = this.then(
    Modifier
        .drawBehind {
            val strokePx = strokeWidth.toPx()
            val dashPx = dashLength.toPx()
            val gapPx = gapLength.toPx()
            val radiusPx = cornerRadius.toPx()

            drawRoundRect(
                color = color,
                topLeft = Offset(strokePx / 2f, strokePx / 2f),
                size = Size(size.width - strokePx, size.height - strokePx),
                cornerRadius = CornerRadius(radiusPx, radiusPx),
                style = Stroke(
                    width = strokePx,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashPx, gapPx), 0f),
                    cap = StrokeCap.Round
                )
            )
        }
)

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun PreviewPuzzleSolvedScreen() {
    MaterialTheme {
        Surface {
            PuzzleSolvedScreen(vm = koinInject<TangoViewModel>())
        }
    }
}
