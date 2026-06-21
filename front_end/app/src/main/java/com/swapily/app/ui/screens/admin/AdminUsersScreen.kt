package com.swapily.app.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.swapily.app.data.model.User
import com.swapily.app.ui.theme.*
import com.swapily.app.viewmodel.AdminUsersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUsersScreen(
    viewModel: AdminUsersViewModel
) {
    val users by viewModel.users.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val actionSuccess by viewModel.actionSuccess.collectAsState()

    var selectedUser by remember { mutableStateOf<User?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var userToDelete by remember { mutableStateOf("") }

    LaunchedEffect(actionSuccess) {
        if (actionSuccess) viewModel.resetActionSuccess()
    }

    val filteredUsers = users.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
                it.email.contains(searchQuery, ignoreCase = true)
    }

    val pullRefreshState = remember { mutableStateOf(false) }

    LaunchedEffect(loading) {
        pullRefreshState.value = loading
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Background)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Users Management",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = GreenPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Search users...", color = GrayText) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = GreenPrimary) },
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

            Text(
                text = "${filteredUsers.size} users",
                fontSize = 13.sp,
                color = GrayText
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredUsers.isEmpty() && !loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.People, null, tint = GrayText, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No users found", color = GrayText)
                    }
                }
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(filteredUsers, key = { it.uid }) { user ->
                    UserCard(
                        user = user,
                        onClick = { selectedUser = user },
                        onBlock = { viewModel.blockUser(user.uid) },
                        onUnblock = { viewModel.unblockUser(user.uid) },
                        onDelete = {
                            userToDelete = user.uid
                            showDeleteDialog = true
                        }
                    )
                }
                item { Spacer(modifier = Modifier.height(20.dp)) }
            }
        }
    }

    // User Detail Dialog
    selectedUser?.let { user ->
        AlertDialog(
            onDismissRequest = { selectedUser = null },
            containerColor = White,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = user.image,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(user.name, fontWeight = FontWeight.Bold, color = TextDark)
                        Text(user.email, fontSize = 12.sp, color = GrayText)
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    DetailRow("Role", user.role)
                    DetailRow("Status", if (user.isBlocked) "Blocked" else "Active")
                    DetailRow("Location", user.location.ifEmpty { "Not set" })
                    DetailRow("Swaps", user.swapsCount.toString())
                    DetailRow("Rating", String.format("%.1f", user.rating))
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedUser = null }) {
                    Text("Close", color = GreenPrimary)
                }
            }
        )
    }

    // Delete Confirmation
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = White,
            title = { Text("Delete User", fontWeight = FontWeight.Bold, color = TextDark) },
            text = { Text("Are you sure you want to delete this user? This action cannot be undone.", color = TextDark) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteUser(userToDelete)
                    showDeleteDialog = false
                }) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = GrayText)
                }
            }
        )
    }
}

@Composable
internal fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 13.sp, color = GrayText)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextDark)
    }
}

@Composable
fun UserCard(
    user: User,
    onClick: () -> Unit,
    onBlock: () -> Unit,
    onUnblock: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = user.image,
                contentDescription = null,
                modifier = Modifier.size(48.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = user.email,
                    fontSize = 12.sp,
                    color = GrayText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatusChip(
                        text = if (user.isBlocked) "Blocked" else "Active",
                        isActive = !user.isBlocked
                    )
                    if (user.role == "ADMIN") {
                        StatusChip(text = "Admin", isActive = true, color = Color(0xFF9C27B0))
                    }
                }
            }

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, null, tint = GrayText)
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(White)
                ) {
                    if (user.isBlocked) {
                        DropdownMenuItem(
                            text = { Text("Unblock") },
                            leadingIcon = { Icon(Icons.Default.CheckCircle, null, tint = Color.Green) },
                            onClick = { onUnblock(); showMenu = false }
                        )
                    } else {
                        DropdownMenuItem(
                            text = { Text("Block") },
                            leadingIcon = { Icon(Icons.Default.Block, null, tint = Color.Red) },
                            onClick = { onBlock(); showMenu = false }
                        )
                    }
                    DropdownMenuItem(
                        text = { Text("Delete", color = Color.Red) },
                        leadingIcon = { Icon(Icons.Default.Delete, null, tint = Color.Red) },
                        onClick = { onDelete(); showMenu = false }
                    )
                }
            }
        }
    }
}

@Composable
fun StatusChip(
    text: String,
    isActive: Boolean,
    color: Color = if (isActive) Color(0xFF4CAF50) else Color(0xFFF44336)
) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
