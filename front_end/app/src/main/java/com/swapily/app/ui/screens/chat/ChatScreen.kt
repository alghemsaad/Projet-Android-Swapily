package com.swapily.app.ui.screens.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ChatScreen(navController: NavController) {

    var message by remember { mutableStateOf("") }

    val messages = listOf(
        "Hello",
        "Is this available?",
        "Yes"
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
        ) {
            items(messages) {
                Text(it)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        Row(
            modifier = Modifier.padding(16.dp)
        ) {

            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = {}) {
                Text("Send")
            }
        }
    }
}