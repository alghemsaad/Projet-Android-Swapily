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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.swapily.app.ui.Navigation.Screen
import com.swapily.app.ui.theme.*

// --- DATA CLASS POUR SIMULER LES MESSAGES ---
data class ChatPreview(
    val name: String,
    val time: String,
    val lastMessage: String,
    val isUnread: Boolean = false,
    val isVerified: Boolean = false
)

@Composable
fun MessagesScreen(navController: NavController) {
    // Les données basées sur ta maquette HTML
    val chats = listOf(
        ChatPreview("Elena Green", "14:02", "Is the vintage film camera still available for the swap?", true),
        ChatPreview("Marcus Chen", "Yesterday", "I can meet you at the central park eco-market at 10 AM..."),
        ChatPreview("Sarah Miller", "Tuesday", "Thanks again for the succulent pots! They look great in my...", isVerified = true),
        ChatPreview("David Wilson", "Oct 12", "The bike is in great condition. I can drop it off whenever...")
    )

    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomNavBar() },
        containerColor = Background
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Titre et Filtres (Active / Archive)
            HeaderSection()

            Spacer(modifier = Modifier.height(16.dp))

            // Liste des messages
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(chats) { chat ->
                    ChatListItem(chat = chat, onClick = {
                        // On utilise la fonction createRoute qu'on a créée à l'étape 1
                        navController.navigate(Screen.Chat.createRoute(chat.name))
                    })
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

// --- COMPOSANTS DE L'INTERFACE ---

@Composable
fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(White) // 1. On applique le fond blanc en premier
            .statusBarsPadding() // 2. On pousse le contenu sous la barre d'état du téléphone
            .height(64.dp) // 3. On fixe la hauteur de la barre
            .padding(horizontal = 20.dp), // 4. On ajoute l'espace sur les côtés
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
            text = "Messages",
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
fun ChatListItem(chat: ChatPreview, onClick: () -> Unit) {
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
            // Avatar (Remplacé par une icône par défaut en attendant les vraies images)
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(GreenLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = GreenPrimary)
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Textes (Nom + Message)
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = chat.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (chat.isVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Verified",
                                tint = GreenPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Text(
                        text = chat.time,
                        fontSize = 12.sp,
                        fontWeight = if (chat.isUnread) FontWeight.Bold else FontWeight.Normal,
                        color = if (chat.isUnread) GreenPrimary else GrayText
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = chat.lastMessage,
                    fontSize = 14.sp,
                    fontWeight = if (chat.isUnread) FontWeight.Bold else FontWeight.Normal,
                    color = if (chat.isUnread) TextDark else GrayText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Thumbnail de l'objet échangé (Remplacé par une icône par défaut)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Background),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.ShoppingCart, contentDescription = null, tint = GrayText)
                if (chat.isUnread) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(2.dp)
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(GreenPrimary)
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(84.dp)
            .background(White)
            .padding(bottom = 16.dp), // Padding pour la zone safe du bas de l'écran
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavItem(icon = Icons.Outlined.Search, label = "Discover", isSelected = false)
        BottomNavItem(icon = Icons.Default.Refresh, label = "Swaps", isSelected = true) // Actif sur cette page
        BottomNavItem(icon = Icons.Default.AddCircle, label = "Add", isSelected = false, isAccent = true)
        BottomNavItem(icon = Icons.Outlined.Person, label = "Profile", isSelected = false)
    }
}

@Composable
fun BottomNavItem(icon: ImageVector, label: String, isSelected: Boolean, isAccent: Boolean = false) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { /* TODO: Navigation */ }
    ) {
        if (isSelected && !isAccent) {
            Box(
                modifier = Modifier
                    .background(GreenLight, shape = RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Icon(icon, contentDescription = label, tint = GreenPrimary)
            }
        } else {
            Icon(
                icon,
                contentDescription = label,
                tint = if (isAccent) GreenPrimary else GrayText,
                modifier = if (isAccent) Modifier.size(32.dp) else Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            fontSize = 12.sp,
            color = if (isSelected || isAccent) GreenPrimary else GrayText,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}