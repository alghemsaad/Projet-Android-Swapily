package com.swapily.app.ui.Navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.swapily.app.ui.screens.auth.LoginScreen
import com.swapily.app.ui.screens.home.HomeScreen
import com.swapily.app.ui.screens.messages.MessagesScreen
import com.swapily.app.ui.screens.profile.ProfileScreen
import com.swapily.app.ui.screens.addproduct.AddProductScreen
import com.swapily.app.ui.screens.chat.ChatScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {

        composable(Screen.Login.route) {
            LoginScreen(navController)
        }

        composable(Screen.Home.route) {
            HomeScreen(navController)
        }

        composable(Screen.Messages.route) {
            MessagesScreen(navController)
        }

        composable(Screen.Profile.route) {
            ProfileScreen(navController)
        }

        composable(Screen.AddProduct.route) {
            AddProductScreen(navController)
        }

        composable(Screen.Chat.route) {
            ChatScreen(navController)
        }
    }
}