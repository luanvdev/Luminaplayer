package com.samyak.iptvminepro.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val Sans = FontFamily.SansSerif

// Tighter, heavier headlines and calmer body copy, like the streaming UIs in the references.
val Typography = Typography(
    displaySmall = TextStyle(
        fontFamily = Sans, fontWeight = FontWeight.Bold,
        fontSize = 34.sp, lineHeight = 38.sp, letterSpacing = (-0.8).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = Sans, fontWeight = FontWeight.Bold,
        fontSize = 28.sp, lineHeight = 32.sp, letterSpacing = (-0.6).sp
    ),
    headlineSmall = TextStyle(
        fontFamily = Sans, fontWeight = FontWeight.Bold,
        fontSize = 24.sp, lineHeight = 28.sp, letterSpacing = (-0.4).sp
    ),
    titleLarge = TextStyle(
        fontFamily = Sans, fontWeight = FontWeight.Bold,
        fontSize = 20.sp, lineHeight = 26.sp, letterSpacing = (-0.2).sp
    ),
    titleMedium = TextStyle(
        fontFamily = Sans, fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp, lineHeight = 22.sp, letterSpacing = 0.sp
    ),
    titleSmall = TextStyle(
        fontFamily = Sans, fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = Sans, fontWeight = FontWeight.Normal,
        fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.1.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Sans, fontWeight = FontWeight.Normal,
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp
    ),
    bodySmall = TextStyle(
        fontFamily = Sans, fontWeight = FontWeight.Normal,
        fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.2.sp
    ),
    labelLarge = TextStyle(
        fontFamily = Sans, fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = Sans, fontWeight = FontWeight.Medium,
        fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.2.sp
    ),
    labelSmall = TextStyle(
        fontFamily = Sans, fontWeight = FontWeight.Medium,
        fontSize = 11.sp, lineHeight = 14.sp, letterSpacing = 0.2.sp
    )
)
