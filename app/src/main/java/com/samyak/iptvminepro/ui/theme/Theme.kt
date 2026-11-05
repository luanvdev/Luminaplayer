package com.samyak.iptvminepro.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * The redesigned app is dark-only, so this is always `true`. Screens that still branch on
 * it (`if (isBlackTheme) darkStyle else lightStyle`) therefore always take their dark branch.
 */
val LocalIsBlackTheme = staticCompositionLocalOf { true }

// Default look: deep midnight navy with amber accents.
private val MidnightColorScheme = darkColorScheme(
    primary = Amber,
    onPrimary = OnAmber,
    primaryContainer = InkHigh,
    onPrimaryContainer = TextHigh,
    secondary = Violet,
    onSecondary = TextHigh,
    secondaryContainer = VioletDeep,
    onSecondaryContainer = TextHigh,
    tertiary = Teal,
    onTertiary = OnAmber,
    background = Midnight,
    onBackground = TextHigh,
    surface = Midnight,
    onSurface = TextHigh,
    surfaceVariant = Ink,
    onSurfaceVariant = TextMuted,
    surfaceContainerLowest = MidnightDeep,
    surfaceContainerLow = Midnight,
    surfaceContainer = Ink,
    surfaceContainerHigh = InkHigh,
    surfaceContainerHighest = InkHigh,
    outline = InkStroke,
    outlineVariant = InkStroke,
    error = LiveRed
)

// "Material black" switch: same palette on a near-AMOLED background.
private val AmoledColorScheme = darkColorScheme(
    primary = Amber,
    onPrimary = OnAmber,
    primaryContainer = MaterialBlackSurfaceVariant,
    onPrimaryContainer = TextHigh,
    secondary = Violet,
    onSecondary = TextHigh,
    secondaryContainer = VioletDeep,
    onSecondaryContainer = TextHigh,
    tertiary = Teal,
    onTertiary = OnAmber,
    background = MidnightDeep,
    onBackground = TextHigh,
    surface = MidnightDeep,
    onSurface = TextHigh,
    surfaceVariant = Color0F0F1A,
    onSurfaceVariant = TextMuted,
    surfaceContainerLowest = MaterialPureBlack,
    surfaceContainerLow = MidnightDeep,
    surfaceContainer = Color0F0F1A,
    surfaceContainerHigh = MaterialBlackSurfaceVariant,
    surfaceContainerHighest = MaterialBlackSurfaceVariant,
    outline = InkStroke,
    outlineVariant = InkStroke,
    error = LiveRed
)

@Composable
fun IPTVMineProTheme(
    blackTheme: Boolean = ThemePreferenceManager.isBlackTheme(androidx.compose.ui.platform.LocalContext.current),
    content: @Composable () -> Unit
) {
    val colorScheme = if (blackTheme) AmoledColorScheme else MidnightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    CompositionLocalProvider(LocalIsBlackTheme provides true) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
