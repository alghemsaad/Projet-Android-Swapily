package com.swapily.app.ui.screens.chat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.swapily.app.data.model.Swap
import com.swapily.app.data.model.Message
import com.swapily.app.viewmodel.SwapViewModel
import com.swapily.app.viewmodel.AuthViewModel
import com.swapily.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController, 
    swapId: String,
    swapViewModel: SwapViewModel,
    authViewModel: AuthViewModel
) {
    val swaps by swapViewModel.swaps.collectAsState()
    val swap = swaps.find { it.id == swapId }
    val messages by swapViewModel.messages.collectAsState()
    val currentUser by authViewModel.profileUser.collectAsState()

    var inputText by remember { mutableStateOf("") }

    LaunchedEffect(swapId, messages) {
        val firebaseUid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
        if (messages.isNotEmpty() && swap?.read == false && swap.lastSenderId != firebaseUid) {
             swapViewModel.markAsRead(swapId)
        }
    }

    LaunchedEffect(swapId) {
        swapViewModel.fetchMessages(swapId)
    }

    val otherPartyName = if (swap?.senderId == currentUser?.uid) swap?.receiverName else swap?.senderName
    val otherPartyImage = if (swap?.senderId == currentUser?.uid) swap?.receiverImage else swap?.senderImage

    Scaffold(
        topBar = { ChatTopBar(navController, otherPartyName ?: "Chat", otherPartyImage ?: "") },
        bottomBar = { 
            ChatInputBar(
                text = inputText, 
                onValueChange = { inputText = it },
                onSend = {
                    if (inputText.isNotEmpty()) {
                        swapViewModel.sendMessage(swapId, inputText)
                        inputText = ""
                    }
                }
            ) 
        },
        containerColor = Background
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            if (swap != null) {
                item { 
                    SwapProposalCard(
                        swap = swap, 
                        isReceiver = swap.receiverId == currentUser?.uid,
                        onAccept = { swapViewModel.updateSwapStatus(swap.id, "ACCEPTED") },
                        onReject = { swapViewModel.updateSwapStatus(swap.id, "REJECTED") }
                    ) 
                }
            }

            items(messages) { msg ->
                MessageBubble(message = msg, isFromMe = msg.senderId == currentUser?.uid)
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun ChatTopBar(navController: NavController, userName: String, userImage: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
            .statusBarsPadding()
            .height(64.dp)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = GreenPrimary)
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(GreenLight),
                contentAlignment = Alignment.Center
            ) {
                if (userImage.isNotEmpty()) {
                    AsyncImage(
                        model = userImage,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.Person, contentDescription = null, tint = GreenPrimary)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(text = userName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = GreenPrimary)
                Text(
                    text = "Active now",
                    fontSize = 12.sp,
                    color = GrayText
                )
            }
        }

        IconButton(onClick = { /* TODO */ }) {
            Icon(Icons.Default.MoreVert, contentDescription = "More", tint = GreenPrimary)
        }
    }
}

@Composable
fun SwapProposalCard(
    swap: Swap, 
    isReceiver: Boolean,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, GreenLight)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("SWAP PROPOSAL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GrayText, letterSpacing = 1.sp)
                Surface(color = GreenLight, shape = RoundedCornerShape(12.dp)) {
                    Text(swap.status, fontSize = 10.sp, color = GreenPrimary, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProductThumbnail(swap.senderProductTitle, swap.senderProductImage)
                Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(GreenPrimary), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = White)
                }
                ProductThumbnail(swap.receiverProductTitle, swap.receiverProductImage)
            }

            if (isReceiver && swap.status == "PENDING") {
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                        border = BorderStroke(1.dp, Color.Red),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Reject")
                    }
                    Button(
                        onClick = onAccept,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Accept", color = White)
                    }
                }
            }
        }
    }
}

@Composable
fun ProductThumbnail(title: String, imageUrl: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)).background(Background), contentAlignment = Alignment.Center) {
            if (imageUrl.isNotEmpty()) {
                AsyncImage(model = imageUrl, contentDescription = null, contentScale = ContentScale.Crop)
            } else {
                Icon(Icons.Outlined.Build, contentDescription = null, tint = GrayText)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GreenPrimary, maxLines = 1)
    }
}

@Composable
fun MessageBubble(message: Message, isFromMe: Boolean) {
    val arrangement = if (isFromMe) Arrangement.End else Arrangement.Start
    val bubbleColor = if (isFromMe) GreenPrimary else White
    val textColor = if (isFromMe) White else TextDark

    val bubbleShape = if (isFromMe) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp)
    } else {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = arrangement
    ) {
        if (!isFromMe) {
            Box(
                modifier = Modifier.size(32.dp).clip(CircleShape).background(GreenLight).align(Alignment.Bottom),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier.fillMaxWidth(0.85f),
            horizontalAlignment = if (isFromMe) Alignment.End else Alignment.Start
        ) {
            Surface(
                color = bubbleColor,
                shape = bubbleShape,
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = message.text,
                        color = textColor,
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInputBar(text: String, onValueChange: (String) -> Unit, onSend: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .navigationBarsPadding(),
        color = White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* TODO */ }) {
                Icon(Icons.Default.AddCircle, contentDescription = "Add", tint = GreenPrimary)
            }

            TextField(
                value = text,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp))
                    .border(1.dp, GreenLight, RoundedCornerShape(24.dp)),
                placeholder = { Text("Type a message...", color = GrayText) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Background,
                    unfocusedContainerColor = Background,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onSend,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(GreenPrimary)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = White)
            }
        }
    }
}
