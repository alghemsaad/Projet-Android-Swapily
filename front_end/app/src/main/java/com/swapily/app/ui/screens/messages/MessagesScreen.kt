package com.swapily.app.ui.screens.messages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.swapily.app.ui.Navigation.Screen
import com.swapily.app.ui.components.AppBottomBar

@Composable
fun MessagesScreen(navController: NavController) {

    val chats = listOf(
        "Ahmed",
        "Sara",
        "Youssef"
    )

    Scaffold(
        bottomBar = { AppBottomBar(navController) }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            items(chats) { user ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .clickable {
                            navController.navigate(Screen.Chat.route)
                        }
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(user)
                        Text("Last message...")
                    }
                }
            }
        }
    }
}