package com.example.brainbites.core.theme


import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import brainbites.composeapp.generated.resources.Res
import androidx.compose.material3.Typography
import brainbites.composeapp.generated.resources.BeVietnamPro_Bold
import brainbites.composeapp.generated.resources.BeVietnamPro_ExtraBold
import brainbites.composeapp.generated.resources.BeVietnamPro_Light
import brainbites.composeapp.generated.resources.BeVietnamPro_Medium
import brainbites.composeapp.generated.resources.BeVietnamPro_Regular
import brainbites.composeapp.generated.resources.BeVietnamPro_SemiBold
import brainbites.composeapp.generated.resources.PlusJakartaSans_Bold
import brainbites.composeapp.generated.resources.PlusJakartaSans_ExtraBold
import brainbites.composeapp.generated.resources.PlusJakartaSans_Light
import brainbites.composeapp.generated.resources.PlusJakartaSans_Medium
import brainbites.composeapp.generated.resources.PlusJakartaSans_Regular
import brainbites.composeapp.generated.resources.PlusJakartaSans_SemiBold
import org.jetbrains.compose.resources.Font


@Composable
fun PlusJakartaSans() = FontFamily(
    Font(Res.font.PlusJakartaSans_Light, FontWeight.Light),
    Font(Res.font.PlusJakartaSans_Regular, FontWeight.Normal),
    Font(Res.font.PlusJakartaSans_Medium, FontWeight.Medium),
    Font(Res.font.PlusJakartaSans_SemiBold, FontWeight.SemiBold),
    Font(Res.font.PlusJakartaSans_Bold, FontWeight.Bold),
    Font(Res.font.PlusJakartaSans_ExtraBold, FontWeight.ExtraBold)
)

@Composable
fun BeVietnamPro() = FontFamily(
    Font(Res.font.BeVietnamPro_Light, FontWeight.Light),
    Font(Res.font.BeVietnamPro_Regular, FontWeight.Normal),
    Font(Res.font.BeVietnamPro_Medium, FontWeight.Medium),
    Font(Res.font.BeVietnamPro_SemiBold, FontWeight.SemiBold),
    Font(Res.font.BeVietnamPro_Bold, FontWeight.Bold),
    Font(Res.font.BeVietnamPro_ExtraBold, FontWeight.ExtraBold)
)

