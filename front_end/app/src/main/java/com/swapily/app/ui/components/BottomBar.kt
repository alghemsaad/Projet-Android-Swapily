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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.swapily.app.ui.Navigation.Screen
import com.swapily.app.ui.theme.GrayText
import com.swapily.app.ui.theme.GreenLight
import com.swapily.app.ui.theme.GreenPrimary

@Composable
fun AppBottomBar(navController: NavController) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val itemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = GreenPrimary,
        selectedTextColor = GreenPrimary,
        unselectedIconColor = GrayText,
        unselectedTextColor = GrayText,
        indicatorColor = GreenLight
    )

    NavigationBar(
        containerColor = com.swapily.app.ui.theme.White,
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
            colors = itemColors
        )

        NavigationBarItem(
            selected = currentRoute == Screen.Swaps.route,
            onClick = {
                if (currentRoute != Screen.Swaps.route) {
                    navController.navigate(Screen.Swaps.route)
                }
            },
            icon = { Icon(Icons.Default.SwapHoriz, null) },
            label = { Text("Swaps") },
            colors = itemColors
        )

        NavigationBarItem(
            selected = currentRoute == Screen.AddProduct.route,
            onClick = {
                if (currentRoute != Screen.AddProduct.route) {
                    navController.navigate(Screen.AddProduct.route)
                }
            },
            icon = { Icon(Icons.Default.AddCircle, null, modifier = Modifier.size(32.dp)) },
            label = { Text("Add") },
            colors = itemColors
        )

        NavigationBarItem(
            selected = currentRoute == Screen.Profile.route || currentRoute == Screen.EditProfile.route,
            onClick = {
                if (currentRoute != Screen.Profile.route) {
                    navController.navigate(Screen.Profile.route)
                }
            },
            icon = { Icon(Icons.Default.PersonOutline, null) },
            label = { Text("Profile") },
            colors = itemColors
        )
    }
}
