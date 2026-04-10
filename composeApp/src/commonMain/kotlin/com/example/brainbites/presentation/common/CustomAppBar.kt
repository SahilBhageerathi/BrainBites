package com.example.brainbites.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brainbites.core.theme.BeVietnamPro

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrainBitesTopBar(
    appBarTitle: String = "",
    showBackButton: Boolean = false,
    onClickBackBtn: () -> Unit = {},
    actionWidget: @Composable (() -> Unit)? = null,
    onProfileClick: () -> Unit = {}
) {
    Surface(
        shadowElevation = 6.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {


                if (showBackButton) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                        contentDescription = "back",
                        tint = Color(0xFF5B4BDB),
                        modifier = Modifier.size(28.dp)
                            .clickable { onClickBackBtn() }
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.GridView,
                        contentDescription = "Menu",
                        tint = Color(0xFF5B4BDB),
                        modifier = Modifier.size(28.dp)
                    )
                }


                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = appBarTitle,
                    style = TextStyle(
                        fontFamily = BeVietnamPro(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold),
                    color = Color(0xFF5B4BDB)
                )
            }


            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                actionWidget?.invoke()
//                UserXp(currentXp)

                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFD9C9A3))
                        .clickable { onProfileClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Color.White
                    )
                }
            }
        }
    }
}