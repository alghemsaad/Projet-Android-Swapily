package com.swapily.app.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swapily.app.ui.theme.*
import com.swapily.app.viewmodel.AdminNotificationsViewModel

@Composable
fun AdminNotificationsScreen(
    viewModel: AdminNotificationsViewModel
) {
    val loading by viewModel.loading.collectAsState()
    val sendSuccess by viewModel.sendSuccess.collectAsState()
    val error by viewModel.error.collectAsState()

    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var targetUserId by remember { mutableStateOf("") }
    var sendToAll by remember { mutableStateOf(true) }
    var showSuccessSnackbar by remember { mutableStateOf(false) }

    LaunchedEffect(sendSuccess) {
        if (sendSuccess) {
            showSuccessSnackbar = true
            title = ""
            message = ""
            targetUserId = ""
            viewModel.resetSendSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Send Notification",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = GreenPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Notify users about updates, events, or announcements",
            fontSize = 14.sp,
            color = GrayText
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Target selection
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = White),
            elevation = CardDefaults.cardElevation(3.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Target Audience", fontWeight = FontWeight.Bold, color = TextDark, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = sendToAll,
                        onClick = { sendToAll = true },
                        colors = RadioButtonDefaults.colors(selectedColor = GreenPrimary)
                    )
                    Text("All Users", color = TextDark)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = !sendToAll,
                        onClick = { sendToAll = false },
                        colors = RadioButtonDefaults.colors(selectedColor = GreenPrimary)
                    )
                    Text("Specific User", color = TextDark)
                }

                if (!sendToAll) {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = targetUserId,
                        onValueChange = { targetUserId = it },
                        label = { Text("User ID") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenPrimary,
                            unfocusedBorderColor = GreenLight
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Title field
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            placeholder = { Text("Notification title...") },
            leadingIcon = { Icon(Icons.Default.Title, null, tint = GreenPrimary) },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = White,
                unfocusedContainerColor = White,
                focusedBorderColor = GreenPrimary,
                unfocusedBorderColor = GreenLight
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Message field
        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Message") },
            placeholder = { Text("Notification message...") },
            leadingIcon = { Icon(Icons.Default.Message, null, tint = GreenPrimary) },
            minLines = 4,
            maxLines = 6,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = White,
                unfocusedContainerColor = White,
                focusedBorderColor = GreenPrimary,
                unfocusedBorderColor = GreenLight
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Send button
        Button(
            onClick = {
                if (sendToAll) {
                    viewModel.sendToAll(title, message)
                } else {
                    viewModel.sendToUser(targetUserId, title, message)
                }
            },
            enabled = title.isNotBlank() && message.isNotBlank() && !loading &&
                    (sendToAll || targetUserId.isNotBlank()),
            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth().height(54.dp)
        ) {
            if (loading) {
                CircularProgressIndicator(
                    color = White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Icon(Icons.Default.Send, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Send Notification", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (error.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Error, null, tint = Color.Red, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(error, color = Color.Red, fontSize = 13.sp)
                }
            }
        }

        if (showSuccessSnackbar) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Notification sent successfully!", color = Color(0xFF4CAF50), fontSize = 13.sp)
                }
            }
            LaunchedEffect(showSuccessSnackbar) {
                kotlinx.coroutines.delay(3000)
                showSuccessSnackbar = false
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
