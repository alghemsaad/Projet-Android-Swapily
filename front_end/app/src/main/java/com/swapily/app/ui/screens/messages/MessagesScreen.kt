package com.swapily.app.ui.screens.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.swapily.app.ui.components.AppBottomBar
import com.swapily.app.ui.Navigation.Screen
import com.swapily.app.ui.theme.*
import com.swapily.app.viewmodel.SwapViewModel
import com.swapily.app.viewmodel.AuthViewModel
import com.swapily.app.data.model.Swap
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MessagesScreen(
    navController: NavController,
    swapViewModel: SwapViewModel,
    authViewModel: AuthViewModel
) {
    val swaps by swapViewModel.swaps.collectAsState()
    val currentUser by authViewModel.profileUser.collectAsState()

    var selectedTab by remember { mutableStateOf("Active") }

    val userSwaps = swaps.filter { it.senderId == currentUser?.uid || it.receiverId == currentUser?.uid }
    
    val filteredSwaps = when (selectedTab) {
        "Active" -> userSwaps.filter { it.status == "PENDING" }
        "Archive" -> userSwaps.filter { it.status == "ACCEPTED" || it.status == "REJECTED" || it.status == "COMPLETED" }
        else -> userSwaps
    }

    LaunchedEffect(Unit) {
        swapViewModel.fetchSwaps()
    }

    Scaffold(
        topBar = { TopBar() },
        bottomBar = { AppBottomBar(navController) },
        containerColor = Background
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            HeaderSection(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredSwaps.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        if (selectedTab == "Active") "No active swaps." else "No archived swaps.",
                        color = GrayText
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredSwaps) { swap ->
                        val isSender = swap.senderId == currentUser?.uid
                        val otherPartyName = if (isSender) swap.receiverName else swap.senderName
                        val otherPartyImage = if (isSender) swap.receiverImage else swap.senderImage
                        val otherPartyProductImage = if (isSender) swap.receiverProductImage else swap.senderProductImage
                        
                        SwapListItem(
                            swap = swap, 
                            otherPartyName = otherPartyName, 
                            otherPartyImage = otherPartyImage,
                            otherPartyProductImage = otherPartyProductImage, 
                            currentUserId = currentUser?.uid ?: "",
                            onClick = {
                                navController.navigate(Screen.Chat.createRoute(swap.id))
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Looking for older conversations? Search history",
                            color = GrayText,
                            fontSize = 14.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
            .statusBarsPadding()
            .height(64.dp)
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { /* TODO */ }) {
            Icon(Icons.Default.Search, contentDescription = "Search", tint = GreenPrimary)
        }

        Text(
            text = "Swapily",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = GreenPrimary
        )

        IconButton(onClick = { /* TODO */ }) {
            Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = GreenPrimary)
        }
    }
}

@Composable
fun HeaderSection(selectedTab: String, onTabSelected: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Swaps",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = GreenPrimary
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Surface(
                onClick = { onTabSelected("Active") },
                color = if (selectedTab == "Active") GreenPrimary else GreenLight,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Active",
                    color = if (selectedTab == "Active") White else GreenPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
            Surface(
                onClick = { onTabSelected("Archive") },
                color = if (selectedTab == "Archive") GreenPrimary else GreenLight,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Archive",
                    color = if (selectedTab == "Archive") White else GreenPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun SwapListItem(
    swap: Swap, 
    otherPartyName: String, 
    otherPartyImage: String,
    otherPartyProductImage: String,
    currentUserId: String,
    onClick: () -> Unit
) {
    val firebaseUid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
    val isUnread = !swap.read && swap.lastSenderId.isNotEmpty() && swap.lastSenderId != firebaseUid
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    val acceptedDateStr = if (swap.status == "ACCEPTED" && swap.acceptedAt > 0L) {
        dateFormat.format(Date(swap.acceptedAt))
    } else null

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = if (isUnread) GreenLight.copy(alpha = 0.3f) else White,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(GreenLight),
                contentAlignment = Alignment.Center
            ) {
                if (!otherPartyImage.isNullOrEmpty()) {
                    AsyncImage(
                        model = otherPartyImage,
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Default.Person, 
                        contentDescription = null, 
                        tint = GreenPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                
                if (isUnread) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(14.dp)
                            .background(GreenPrimary, CircleShape)
                            .border(2.dp, White, CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = otherPartyName,
                        fontSize = 16.sp,
                        fontWeight = if (isUnread) FontWeight.ExtraBold else FontWeight.Bold,
                        color = TextDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = swap.status,
                        fontSize = 12.sp,
                        color = GreenPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (swap.lastMessage.isNotEmpty()) swap.lastMessage else "${swap.senderProductTitle} vs ${swap.receiverProductTitle}",
                    fontSize = 14.sp,
                    color = if (isUnread) TextDark else GrayText,
                    fontWeight = if (isUnread) FontWeight.Bold else FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Show accepted date for accepted swaps
                if (acceptedDateStr != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = GrayText,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Accepted $acceptedDateStr",
                            fontSize = 11.sp,
                            color = GrayText
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Background),
                contentAlignment = Alignment.Center
            ) {
                if (otherPartyProductImage.isNotEmpty()) {
                    AsyncImage(model = otherPartyProductImage, contentDescription = null, contentScale = ContentScale.Crop)
                } else {
                    Icon(Icons.Outlined.ShoppingCart, contentDescription = null, tint = GrayText)
                }
            }
        }
    }
}
