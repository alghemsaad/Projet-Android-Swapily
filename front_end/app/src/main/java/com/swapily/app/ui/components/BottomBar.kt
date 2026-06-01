package com.swapily.app.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.swapily.app.ui.Navigation.Screen

@Composable
fun AppBottomBar(navController: NavController) {

    val darkGreen = Color(0xFF0D5C3D)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentRoute == Screen.Home.route,
            onClick = {
                if (currentRoute != Screen.Home.route) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            },
            icon = { Icon(Icons.Default.Search, null) },
            label = { Text("Discover") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = darkGreen,
                indicatorColor = darkGreen
            )
        )

        NavigationBarItem(
            selected = currentRoute == Screen.Messages.route,
            onClick = {
                if (currentRoute != Screen.Messages.route) {
                    navController.navigate(Screen.Messages.route)
                }
            },
            icon = { Icon(Icons.Default.SwapHoriz, null) },
            label = { Text("Swaps") }
        )

        NavigationBarItem(
            selected = currentRoute == Screen.AddProduct.route,
            onClick = {
                if (currentRoute != Screen.AddProduct.route) {
                    navController.navigate(Screen.AddProduct.route)
                }
            },
            icon = { Icon(Icons.Default.AddCircle, null, modifier = Modifier.size(32.dp)) },
            label = { Text("Add") }
        )

        NavigationBarItem(
            selected = currentRoute == Screen.Profile.route || currentRoute == Screen.EditProfile.route,
            onClick = {
                if (currentRoute != Screen.Profile.route) {
                    navController.navigate(Screen.Profile.route)
                }
            },
            icon = { Icon(Icons.Default.PersonOutline, null) },
            label = { Text("Profile") }
        )
    }
}