package com.swapily.app.ui.Navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.swapily.app.ui.screens.auth.LoginScreen
import com.swapily.app.ui.screens.home.HomeScreen
import com.swapily.app.ui.screens.messages.MessagesScreen
import com.swapily.app.ui.screens.profile.ProfileScreen
import com.swapily.app.ui.screens.profile.EditProfileScreen
import com.swapily.app.ui.screens.addproduct.AddProductScreen
import com.swapily.app.ui.screens.chat.ChatScreen
import com.swapily.app.ui.screens.productdetail.ProductDetailScreen
import com.swapily.app.ui.screens.profile.EditProductScreen
import com.swapily.app.ui.screens.profile.PublicProfileScreen
import com.swapily.app.viewmodel.AuthViewModel
import com.swapily.app.viewmodel.ProductViewModel

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val productViewModel: ProductViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {

        composable(Screen.Login.route) {
            LoginScreen(navController, authViewModel)
        }

        composable(Screen.Home.route) {
            HomeScreen(navController, productViewModel, authViewModel)
        }

        composable(Screen.Swaps.route) {
            MessagesScreen(navController)
        }

        composable(Screen.Profile.route) {
            ProfileScreen(navController, authViewModel, productViewModel)
        }

        composable(Screen.AddProduct.route) {
            AddProductScreen(navController, productViewModel)
        }

        composable(Screen.Chat.route) { backStackEntry ->
            // On récupère le nom depuis l'URL, ou on met "Utilisateur" par défaut
            val userName = backStackEntry.arguments?.getString("userName") ?: "Utilisateur"
            ChatScreen(navController, userName) // On envoie le nom à l'écran
        }

        composable(Screen.EditProfile.route) {
            EditProfileScreen(navController, authViewModel)
        }

        composable(Screen.ProductDetail.route) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            ProductDetailScreen(navController, productId, productViewModel, authViewModel)
        }

        composable(Screen.EditProduct.route) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            EditProductScreen(navController, productId, productViewModel)
        }

        composable(Screen.PublicProfile.route) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            PublicProfileScreen(navController, userId, authViewModel, productViewModel)
        }
    }
}
