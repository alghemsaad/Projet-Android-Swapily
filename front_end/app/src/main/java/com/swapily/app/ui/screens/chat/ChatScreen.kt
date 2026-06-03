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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.swapily.app.ui.theme.*

// --- DATA CLASS POUR LES MESSAGES ---
data class ChatMessage(
    val text: String,
    val time: String,
    val isFromMe: Boolean,
    val hasImage: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(navController: NavController, userName: String) {

    // On appelle notre "base de données" en lui donnant le nom de l'utilisateur cliqué
    val messages = getMessagesForUser(userName)

    var inputText by remember { mutableStateOf("") }

    Scaffold(
        topBar = { ChatTopBar(navController, userName) },
        bottomBar = { ChatInputBar(inputText, onValueChange = { inputText = it }) },
        containerColor = Background
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp), // Espace sur les côtés
            verticalArrangement = Arrangement.spacedBy(16.dp)

        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // 1. La carte de proposition d'échange au sommet
            item { SwapProposalCard() }

            // 2. Le diviseur de date
            item { DateDivider("Tuesday, Oct 24") }

            // 3. Les bulles de messages
            items(messages) { msg ->
                MessageBubble(message = msg)
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

// --- COMPOSANTS DE L'INTERFACE ---

@Composable
fun ChatTopBar(navController: NavController, userName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
            .statusBarsPadding() // Pour ne pas cacher l'heure du téléphone
            .height(64.dp)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = GreenPrimary)
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(GreenLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = GreenPrimary)
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Nom et Statut
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = userName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = GreenPrimary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Verified",
                        tint = GreenPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                }
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
fun SwapProposalCard() {
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
                Text("ONGOING SWAP PROPOSAL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GrayText, letterSpacing = 1.sp)
                Surface(color = GreenLight, shape = RoundedCornerShape(12.dp)) {
                    Text("Awaiting Confirmation", fontSize = 10.sp, color = GreenPrimary, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Objet 1 (Montre)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)).background(Background), contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.Build, contentDescription = null, tint = GrayText) // Placeholder image
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Fossil Watch", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GreenPrimary)
                }

                // Icône Échange
                Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(GreenPrimary), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = White)
                }

                // Objet 2 (Appareil photo)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)).background(Background), contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.Star, contentDescription = null, tint = GrayText) // Placeholder image
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Canon 90D", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GreenPrimary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* TODO */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Confirm Swap", color = White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun DateDivider(date: String) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Surface(color = GreenLight, shape = RoundedCornerShape(12.dp)) {
            Text(
                text = date,
                fontSize = 10.sp,
                color = GrayText,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun MessageBubble(message: ChatMessage) {
    // Logique d'alignement comme WhatsApp
    val arrangement = if (message.isFromMe) Arrangement.End else Arrangement.Start
    val bubbleColor = if (message.isFromMe) GreenPrimary else White
    val textColor = if (message.isFromMe) White else TextDark

    // Forme de la bulle (le petit coin pointu en bas dépend de l'expéditeur)
    val bubbleShape = if (message.isFromMe) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp)
    } else {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = arrangement
    ) {
        // Avatar pour le message reçu
        if (!message.isFromMe) {
            Box(
                modifier = Modifier.size(32.dp).clip(CircleShape).background(GreenLight).align(Alignment.Bottom),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier.fillMaxWidth(0.85f), // La bulle ne prend pas toute la largeur
            horizontalAlignment = if (message.isFromMe) Alignment.End else Alignment.Start
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

                    // Si le message contient une image (ex: l'objectif photo)
                    if (message.hasImage) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.DarkGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.CameraAlt, contentDescription = null, tint = White, modifier = Modifier.size(40.dp))
                        }
                    }
                }
            }

            // Heure du message (et double check si envoyé par moi)
            Row(
                modifier = Modifier.padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = message.time, fontSize = 10.sp, color = GrayText)
                if (message.isFromMe) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.Done, contentDescription = "Read", tint = GreenPrimary, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInputBar(text: String, onValueChange: (String) -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            // On ajoute imePadding pour que la barre remonte quand le clavier s'ouvre !
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

            // Champ de texte arrondi
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
                ),
                trailingIcon = {
                    Row {
                        Icon(Icons.Outlined.Face, contentDescription = "Emoji", tint = GrayText, modifier = Modifier.padding(end = 8.dp))
                    }
                }
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Bouton Envoyer
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(GreenPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = White)
            }
        }
    }
}
// --- FAKE DATABASE FOR MESSAGES ---
fun getMessagesForUser(userName: String): List<ChatMessage> {
    return when (userName) {
        "Elena Green" -> listOf(
            ChatMessage("Is the vintage film camera still available for the swap?", "14:02", false),
            ChatMessage("Hi Elena! Yes, it is. Are you still offering the record player?", "14:05", true)
        )
        "Sarah Miller" -> listOf(
            ChatMessage("Thanks again for the succulent pots! They look great in my living room.", "Tuesday", false),
            ChatMessage("You're very welcome, Sarah! I'm glad you like them.", "Tuesday", true)
        )
        "David Wilson" -> listOf(
            ChatMessage("The bike is in great condition. I can drop it off whenever you're ready.", "Oct 12", false),
            ChatMessage("Awesome, David! Let's meet this weekend.", "Oct 12", true)
        )
        else -> listOf( // Conversation par défaut (pour Marcus ou tout autre nom)
            ChatMessage("Hey! I saw your watch listing. I've been looking for that exact model to gift my brother. Would you be interested in the Canon camera I have listed?", "10:15 AM", false),
            ChatMessage("Hi $userName! Yes, the Canon 90D looks great. Is the lens in good condition? No scratches?", "10:18 AM", true),
            ChatMessage("It's pristine! Here's a closer shot of the glass. Always kept it under a UV filter.", "10:20 AM", false, hasImage = true),
            ChatMessage("Looks perfect. I'm happy to move forward with the swap if you are!", "10:22 AM", true)
        )
    }
}