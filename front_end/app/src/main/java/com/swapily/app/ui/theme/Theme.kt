package com.swapily.app.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val AppColors = lightColorScheme(
    primary = GreenPrimary,
    background = Background,
    surface = White
)

@Composable
fun SwapilyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColors,
        content = content
    )
}