package com.swapily.app.ui.screens.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.swapily.app.R
import com.swapily.app.data.model.User
import com.swapily.app.viewmodel.AuthViewModel
import com.swapily.app.ui.components.AppBottomBar
import com.swapily.app.ui.Navigation.Screen

@Composable
fun ProfileScreen(navController: NavController, viewModel: AuthViewModel = viewModel()) {

    val user by viewModel.profileUser.collectAsState()
    val isLoading by viewModel.loading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchUserProfile()
    }

    val bg = Color(0xFFE5F6EA)
    val darkGreen = Color(0xFF0D5C3D)

    Scaffold(
        containerColor = bg,
        bottomBar = { AppBottomBar(navController) }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 26.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {

            item {
                TopProfileBar(onEditClick = {
                    navController.navigate(Screen.EditProfile.route)
                })
            }

            item {
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = darkGreen)
                    }
                } else {
                    UserProfileCard(user)
                }
            }

            item {
                Text(
                    "My Swaps",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                SwapTabs()
            }

            item {
                MySwapCard()
            }

            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(Color(0xFF2D805A), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AutoAwesome, null, tint = Color.White)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        "Smart Matching",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item {
                Text(
                    "These swappers have what you're looking for and\nwant your items.",
                    fontSize = 17.sp,
                    lineHeight = 24.sp,
                    color = Color.DarkGray
                )
            }

            item {
                MatchBigCard(onChatClick = { navController.navigate(Screen.Chat.route) })
            }

            item {
                SmallMatchCard(onChatClick = { navController.navigate(Screen.Chat.route) })
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun TopProfileBar(onEditClick: () -> Unit) {
    val darkGreen = Color(0xFF0D5C3D)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onEditClick) {
            Icon(Icons.Default.Edit, null, tint = darkGreen)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                modifier = Modifier.size(42.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                "Swapily",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = darkGreen
            )
        }

        IconButton(onClick = { /* TODO: Notifications */ }) {
            Icon(Icons.Default.NotificationsNone, null, tint = darkGreen)
        }
    }
}

@Composable
fun UserProfileCard(user: User?) {
    val darkGreen = Color(0xFF0D5C3D)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box {
                if (user?.image.isNullOrEmpty()) {
                    Image(
                        painter = painterResource(id = R.drawable.img3),
                        contentDescription = null,
                        modifier = Modifier
                            .size(118.dp)
                            .border(6.dp, Color(0xFFBDF4D5), CircleShape)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(user.image)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(118.dp)
                            .border(6.dp, Color(0xFFBDF4D5), CircleShape)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(32.dp)
                        .background(darkGreen, CircleShape)
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Verified, null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(user?.name ?: "User Name", fontSize = 25.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(8.dp))

            Text("☆  4.9 (42 reviews)", fontSize = 16.sp)

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                Text(user?.location?.ifEmpty { "Non spécifié" } ?: "Non spécifié", color = Color.Gray, fontSize = 17.sp)
            }

            Spacer(modifier = Modifier.height(22.dp))

            HorizontalDivider(color = Color(0xFFEAEAEA))

            Spacer(modifier = Modifier.height(18.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                StatItem("15", "Items", Modifier.weight(1f))
                StatItem("28", "Swaps", Modifier.weight(1f))
                StatItem("12kg", "CO2 Saved", Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun StatItem(value: String, label: String, modifier: Modifier = Modifier) {
    val darkGreen = Color(0xFF0D5C3D)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, color = darkGreen, fontSize = 25.sp, fontWeight = FontWeight.Bold)
        Text(label, fontSize = 15.sp)
    }
}

@Composable
fun SwapTabs() {
    val darkGreen = Color(0xFF0D5C3D)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color(0xFFBFEFD1), RoundedCornerShape(28.dp))
            .padding(5.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(darkGreen, RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("Ongoing", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Text("History", fontSize = 18.sp)
        }
    }
}

@Composable
fun MySwapCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(White),
        elevation = CardDefaults.cardElevation(2.dp),
        border = BorderStroke(1.dp, GrayText.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Stack of images for the swap
            Box(modifier = Modifier.width(90.dp)) {
                // Item 1
                Image(
                    painter = painterResource(id = R.drawable.img1),
                    contentDescription = null,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(2.dp, White, RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                // Swap Icon in the middle
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.Center)
                        .offset(x = 0.dp)
                        .background(GreenLight, CircleShape)
                        .border(2.dp, White, CircleShape)
                        .zIndex(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.SwapHoriz,
                        null,
                        tint = GreenPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Item 2
                Image(
                    painter = painterResource(id = R.drawable.img2),
                    contentDescription = null,
                    modifier = Modifier
                        .size(56.dp)
                        .align(Alignment.CenterEnd)
                        .clip(RoundedCornerShape(8.dp))
                        .border(2.dp, White, RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Eco Sneakers vs iPad Mini",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Sync,
                        null,
                        tint = GreenPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Pending validation",
                        color = GreenPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Icon(
                Icons.Default.KeyboardArrowRight,
                null,
                tint = GrayText.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun MatchBigCard(onChatClick: () -> Unit) {
    val darkGreen = Color(0xFF0D5C3D)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(22.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.img1),
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text("Thomas G.", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("☆ 4.8 • 1.2 km", fontSize = 14.sp)
                }

                Text(
                    "98% MATCH",
                    color = darkGreen,
                    modifier = Modifier
                        .background(Color(0xFFE8EFEA), RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Has:", fontSize = 16.sp)
                    ProductImageBox(R.drawable.img3, "Minimalist Watch")
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("Wants:", fontSize = 16.sp)
                    ProductImageBox(R.drawable.img1, "Camera")
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            Button(
                onClick = onChatClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(darkGreen)
            ) {
                Icon(Icons.Default.ChatBubbleOutline, null, tint = Color.White)
                Spacer(modifier = Modifier.width(10.dp))
                Text("Propose a Swap", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ProductImageBox(image: Int, label: String) {
    Box {
        Image(
            painter = painterResource(id = image),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(185.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        Text(
            label,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
                .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            color = Color(0xFF0D5C3D),
            fontSize = 12.sp
        )
    }
}

@Composable
fun SmallMatchCard(onChatClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(22.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.img2),
                contentDescription = null,
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text("Sophie M.", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("85% Match • 2.5 km", color = Color.DarkGray)
            }

            Box(
                modifier = Modifier
                    .size(58.dp)
                    .background(Color(0xFFBFEFD1), CircleShape)
                    .clickable { onChatClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ArrowForward, null, tint = Color(0xFF0D5C3D))
            }
        }
    }
}