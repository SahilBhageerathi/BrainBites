package com.example.brainbites.presentation.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brainbites.presentation.navigation.BottomNavItem
import org.jetbrains.compose.resources.painterResource


@Composable
fun BrainBitesBottomBar(
    tabs: List<BottomNavItem>,
    currentRoute: String,
    onItemClick: (String) -> Unit
) {


    val selectedIndex = tabs.indexOfFirst { it.route == currentRoute }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {

        val itemWidth = maxWidth / tabs.size

        val indicatorOffset by animateDpAsState(
            targetValue = itemWidth * selectedIndex,
            animationSpec = spring(
                dampingRatio = 0.7f,
                stiffness = Spring.StiffnessMediumLow
            ),
            label = "indicatorOffset"
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp,32.dp))
                .background(Color.White)
        ) {


            Box(
                modifier = Modifier
                    .offset(x = indicatorOffset)
                    .width(itemWidth-5.dp)
                    .height(60.dp)
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color(0xFFFFE6CC))
            )


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                tabs.forEachIndexed { index, item ->

                    val isSelected = index == selectedIndex

                    val contentColor by animateColorAsState(
                        targetValue = if (isSelected) Color(0xFFFF6A00) else Color.Gray,
                        label = "contentColor"
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable (
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ){
                                onItemClick(item.route) }
                            .padding(vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Icon(
                            painter = painterResource(item.icon),
                            contentDescription = item.label,
                            tint = contentColor
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = item.label,
                            fontSize = 12.sp,
                            color = contentColor
                        )
                    }
                } }
            }
        }
    }

