package com.swapily.app.ui.screens.messages

import androidx.compose.foundation.background
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

@Composable
fun MessagesScreen(
    navController: NavController,
    swapViewModel: SwapViewModel,
    authViewModel: AuthViewModel
) {
    val swaps by swapViewModel.swaps.collectAsState()
    val currentUser by authViewModel.profileUser.collectAsState()

    val userSwaps = swaps.filter { it.senderId == currentUser?.uid || it.receiverId == currentUser?.uid }

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

            HeaderSection()

            Spacer(modifier = Modifier.height(16.dp))

            if (userSwaps.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No swaps yet.", color = GrayText)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(userSwaps) { swap ->
                        val isSender = swap.senderId == currentUser?.uid
                        val otherPartyName = if (isSender) swap.receiverName else swap.senderName
                        val otherPartyImage = if (isSender) swap.receiverImage else swap.senderImage
                        val otherPartyProductImage = if (isSender) swap.receiverProductImage else swap.senderProductImage
                        
                        SwapListItem(
                            swap = swap, 
                            otherPartyName = otherPartyName, 
                            otherPartyImage = otherPartyImage,
                            otherPartyProductImage = otherPartyProductImage, 
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
fun HeaderSection() {
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
                color = GreenPrimary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Active",
                    color = White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
            Surface(
                color = GreenLight,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Archive",
                    color = GreenPrimary,
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
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = White,
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
                if (otherPartyImage.isNotEmpty()) {
                    AsyncImage(
                        model = otherPartyImage,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.Person, contentDescription = null, tint = GreenPrimary)
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
                        fontWeight = FontWeight.Bold,
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
                    color = GrayText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
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
