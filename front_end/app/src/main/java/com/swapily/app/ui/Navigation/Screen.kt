package com.swapily.app.ui.Navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Messages : Screen("messages")
    object Profile : Screen("profile")
    object AddProduct : Screen("add_product")
    object Chat : Screen("chat")
}