package com.swapily.app.ui.Navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Swaps : Screen("swaps")
    object Profile : Screen("profile")
    object AddProduct : Screen("add_product")
    object Chat : Screen("chat/{swapId}") {
        fun createRoute(swapId: String) = "chat/$swapId"
    }
    object EditProfile : Screen("edit_profile")
    object ProductDetail : Screen("product_detail/{productId}") {
        fun createRoute(productId: String) = "product_detail/$productId"
    }
    object EditProduct : Screen("edit_product/{productId}") {
        fun createRoute(productId: String) = "edit_product/$productId"
    }
    object PublicProfile : Screen("public_profile/{userId}") {
        fun createRoute(userId: String) = "public_profile/$userId"
    }
}