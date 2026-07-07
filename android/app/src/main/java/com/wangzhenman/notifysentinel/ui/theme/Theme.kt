package com.wangzhenman.notifysentinel.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = BluePrimary,
    secondary = TealSecondary,
    background = SlateBackground,
    surface = SlateBackground,
    onBackground = SlateText,
    onSurface = SlateText,
)

private val DarkColors = darkColorScheme(
    primary = BluePrimary,
    secondary = TealSecondary,
)

@Composable
fun NotifySentinelTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = LightColors,
        content = content,
    )
}
