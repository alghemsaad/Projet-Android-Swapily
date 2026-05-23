package com.swapily.app.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun BottomBar() {

    NavigationBar {

        NavigationBarItem(
            selected = true,
            onClick = {},
            icon = { Text("🏠") }
        )

        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = { Text("💬") }
        )

        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = { Text("👤") }
        )
    }
}