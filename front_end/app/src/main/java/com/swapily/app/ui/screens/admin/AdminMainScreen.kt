package com.swapily.app.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.swapily.app.ui.Navigation.Screen
import com.swapily.app.ui.theme.*
import com.swapily.app.viewmodel.*
import kotlinx.coroutines.launch

data class AdminNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMainScreen(
    rootNavController: NavController,
    authViewModel: AuthViewModel
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val adminNavItems = listOf(
        AdminNavItem("Dashboard", Icons.Default.Dashboard, Screen.AdminDashboard.route),
        AdminNavItem("Users", Icons.Default.People, Screen.AdminUsers.route),
        AdminNavItem("Products", Icons.Default.ShoppingBag, Screen.AdminProducts.route),
        AdminNavItem("Swaps", Icons.Default.SwapHoriz, Screen.AdminSwaps.route),
        AdminNavItem("Reports", Icons.Default.ReportProblem, Screen.AdminReports.route),
        AdminNavItem("Notifications", Icons.Default.Notifications, Screen.AdminNotifications.route)
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = White,
                modifier = Modifier.width(280.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(vertical = 24.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(GreenPrimary, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("S", color = White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                "Swapily Admin",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = GreenPrimary
                            )
                            Text(
                                "Administration Panel",
                                fontSize = 12.sp,
                                color = GrayText
                            )
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp),
                        color = Background
                    )

                    // Nav items
                    adminNavItems.forEach { item ->
                        val isSelected = currentRoute == item.route
                        NavigationDrawerItem(
                            label = {
                                Text(
                                    item.label,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) GreenPrimary else TextDark
                                )
                            },
                            icon = {
                                Icon(
                                    item.icon,
                                    null,
                                    tint = if (isSelected) GreenPrimary else GrayText
                                )
                            },
                            selected = isSelected,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(Screen.AdminDashboard.route) { inclusive = false }
                                        launchSingleTop = true
                                    }
                                }
                                scope.launch { drawerState.close() }
                            },
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = GreenLight.copy(alpha = 0.5f),
                                unselectedContainerColor = Color.Transparent
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 24.dp),
                        color = Background
                    )

                    // Logout
                    NavigationDrawerItem(
                        label = { Text("Logout", color = Color.Red, fontWeight = FontWeight.Medium) },
                        icon = { Icon(Icons.Default.Logout, null, tint = Color.Red) },
                        selected = false,
                        onClick = {
                            authViewModel.logout()
                            rootNavController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor = Color.Transparent
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }
        }
    ) {
        Scaffold(
            containerColor = Background,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = when (currentRoute) {
                                Screen.AdminDashboard.route -> "Dashboard"
                                Screen.AdminUsers.route -> "Users"
                                Screen.AdminProducts.route -> "Products"
                                Screen.AdminSwaps.route -> "Swaps"
                                Screen.AdminReports.route -> "Reports"
                                Screen.AdminNotifications.route -> "Notifications"
                                else -> "Admin"
                            },
                            color = GreenPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, null, tint = GreenPrimary)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Background
                    )
                )
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                AdminNavHost(navController)
            }
        }
    }
}

@Composable
fun AdminNavHost(navController: androidx.navigation.NavHostController) {
    val dashboardVM: AdminDashboardViewModel = viewModel()
    val usersVM: AdminUsersViewModel = viewModel()
    val productsVM: AdminProductsViewModel = viewModel()
    val swapsVM: AdminSwapsViewModel = viewModel()
    val reportsVM: AdminReportsViewModel = viewModel()
    val notificationsVM: AdminNotificationsViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.AdminDashboard.route
    ) {
        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen(dashboardVM)
        }
        composable(Screen.AdminUsers.route) {
            AdminUsersScreen(usersVM)
        }
        composable(Screen.AdminProducts.route) {
            AdminProductsScreen(productsVM)
        }
        composable(Screen.AdminSwaps.route) {
            AdminSwapsScreen(swapsVM)
        }
        composable(Screen.AdminReports.route) {
            AdminReportsScreen(reportsVM)
        }
        composable(Screen.AdminNotifications.route) {
            AdminNotificationsScreen(notificationsVM)
        }
    }
}
