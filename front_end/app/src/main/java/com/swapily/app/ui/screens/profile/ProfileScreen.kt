package com.swapily.app.ui.screens.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.swapily.app.R
import com.swapily.app.data.model.Product
import com.swapily.app.data.model.User
import com.swapily.app.viewmodel.AuthViewModel
import com.swapily.app.viewmodel.ProductViewModel
import com.swapily.app.ui.components.AppBottomBar
import com.swapily.app.ui.Navigation.Screen
import com.swapily.app.ui.screens.home.ProductCard
import com.swapily.app.ui.theme.Background
import com.swapily.app.ui.theme.GrayText
import com.swapily.app.ui.theme.GreenLight
import com.swapily.app.ui.theme.GreenPrimary
import com.swapily.app.ui.theme.TextDark
import com.swapily.app.ui.theme.White
import com.swapily.app.viewmodel.SwapViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: AuthViewModel,
    productViewModel: ProductViewModel,
    swapViewModel: SwapViewModel
) {

    val user by viewModel.profileUser.collectAsState()
    val isLoading by viewModel.loading.collectAsState()
    val allProducts by productViewModel.products.collectAsState()
    val userProducts = allProducts.filter { it.userId == user?.uid }

    val allSwaps by swapViewModel.swaps.collectAsState()
    val userSwapsList = allSwaps.filter { it.senderId == user?.uid || it.receiverId == user?.uid }
    val totalSwapsCount = userSwapsList.size

    var showAllProducts by remember { mutableStateOf(false) }
    val displayedProducts = if (showAllProducts) userProducts else userProducts.take(2)
    
    var selectedTab by remember { mutableStateOf("Ongoing") }
    val favoriteProducts = allProducts.filter { user?.favorites?.contains(it.id) == true }

    LaunchedEffect(Unit) {
        viewModel.fetchUserProfile()
        swapViewModel.fetchSwaps()
    }

    Scaffold(
        containerColor = Background,
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
                TopProfileBar(
                    onEditClick = { navController.navigate(Screen.EditProfile.route) },
                    onLogoutClick = {
                        viewModel.logout()
                        swapViewModel.clearData()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) // Clear backstack
                        }
                    }
                )
            }

            item {
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = GreenPrimary)
                    }
                } else {
                    UserProfileCard(user, userProducts.size, totalSwapsCount)
                }
            }

            // --- My Products Section ---
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "My Products",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Row {
                        if (userProducts.size > 2 && !showAllProducts) {
                            TextButton(onClick = { showAllProducts = true }) {
                                Text("See All", color = GreenPrimary)
                            }
                        } else if (showAllProducts) {
                            TextButton(onClick = { showAllProducts = false }) {
                                Text("Show Less", color = GreenPrimary)
                            }
                        }
                        TextButton(onClick = { navController.navigate(Screen.AddProduct.route) }) {
                            Text("Add New", color = GreenPrimary)
                        }
                    }
                }
            }

            if (userProducts.isEmpty()) {
                item {
                    Text("No products added yet.", color = GrayText, modifier = Modifier.padding(bottom = 8.dp))
                }
            } else {
                items(displayedProducts) { product ->
                    MyProductCard(product, onEditClick = {
                        navController.navigate(Screen.EditProduct.createRoute(product.id))
                    })
                }
            }
            // ---------------------------

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "My Swaps",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    IconButton(onClick = { /* TODO: Filter */ }) {
                        Icon(Icons.Default.Tune, null, tint = GrayText)
                    }
                }
            }

            item {
                SwapTabs(selectedTab) { selectedTab = it }
            }

            item {
                when (selectedTab) {
                    "Favorites" -> {
                        if (favoriteProducts.isEmpty()) {
                            Text("No favorites yet.", color = GrayText, modifier = Modifier.padding(vertical = 20.dp))
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                favoriteProducts.forEach { product ->
                                    ProductCard(
                                        product = product,
                                        navController = navController,
                                        isFavorite = true, // It's in the favorites tab
                                        user = user,
                                        productViewModel = productViewModel,
                                        authViewModel = viewModel
                                    )
                                }
                            }
                        }
                    }
                    "Ongoing" -> {
                        val pendingSwaps = userSwapsList.filter { it.status == "PENDING" }
                        if (pendingSwaps.isEmpty()) {
                            Text("No pending swaps.", color = GrayText, modifier = Modifier.padding(vertical = 20.dp))
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                pendingSwaps.forEach { swap ->
                                    MySwapCard(swap, user?.uid ?: "", onClick = {
                                        navController.navigate(Screen.Chat.createRoute(swap.id))
                                    })
                                }
                            }
                        }
                    }
                    "History" -> {
                        val historySwaps = userSwapsList.filter { it.status != "PENDING" }
                        if (historySwaps.isEmpty()) {
                            Text("No history yet.", color = GrayText, modifier = Modifier.padding(vertical = 20.dp))
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                historySwaps.forEach { swap ->
                                    MySwapCard(swap, user?.uid ?: "", onClick = {
                                        navController.navigate(Screen.Chat.createRoute(swap.id))
                                    })
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun TopProfileBar(onEditClick: () -> Unit, onLogoutClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onEditClick) {
            Icon(Icons.Default.Edit, null, tint = GreenPrimary)
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
                color = GreenPrimary
            )
        }

        IconButton(onClick = onLogoutClick) {
            Icon(Icons.AutoMirrored.Filled.Logout, null, tint = Color.Red)
        }
    }
}

@Composable
fun UserProfileCard(user: User?, itemsCount: Int, totalSwapsCount: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(White),
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
                            .border(6.dp, GreenLight, CircleShape)
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
                            .border(6.dp, GreenLight, CircleShape)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(32.dp)
                        .background(GreenPrimary, CircleShape)
                        .border(2.dp, White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Verified, null, tint = White, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(user?.name ?: "User Name", fontSize = 25.sp, fontWeight = FontWeight.Bold, color = TextDark)

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, null, tint = GrayText, modifier = Modifier.size(20.dp))
                Text(user?.location?.ifEmpty { "Non spécifié" } ?: "Non spécifié", color = GrayText, fontSize = 17.sp)
            }

            Spacer(modifier = Modifier.height(22.dp))

            HorizontalDivider(color = GrayText.copy(alpha = 0.1f))

            Spacer(modifier = Modifier.height(18.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                StatItem(itemsCount.toString(), "Items", Modifier.weight(1f))
                val formattedSwaps = "%.1f".format(totalSwapsCount.toDouble())
                StatItem(formattedSwaps, "Swaps", Modifier.weight(1f))
                val formattedRating = "%.1f".format(user?.rating ?: 0.0)
                StatItem(formattedRating, "Reviews", Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun StatItem(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, color = GreenPrimary, fontSize = 25.sp, fontWeight = FontWeight.Bold)
        Text(label, fontSize = 15.sp, color = GrayText)
    }
}

@Composable
fun SwapTabs(selectedTab: String, onTabSelected: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(GreenLight, RoundedCornerShape(28.dp))
            .padding(5.dp)
    ) {
        val tabs = listOf("Ongoing", "History", "Favorites")
        tabs.forEach { tab ->
            val isSelected = selectedTab == tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .then(if (isSelected) Modifier.background(GreenPrimary, RoundedCornerShape(24.dp)) else Modifier)
                    .clickable { onTabSelected(tab) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    tab,
                    color = if (isSelected) White else GrayText,
                    fontSize = 16.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun MySwapCard(swap: com.swapily.app.data.model.Swap, currentUserUid: String, onClick: () -> Unit) {
    val isSender = swap.senderId == currentUserUid
    val otherPartyName = if (isSender) swap.receiverName else swap.senderName
    val otherPartyImage = if (isSender) swap.receiverImage else swap.senderImage
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(White),
        elevation = CardDefaults.cardElevation(2.dp),
        border = BorderStroke(1.dp, GrayText.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // User Profile Image (Other party)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(GreenLight),
                contentAlignment = Alignment.Center
            ) {
                if (!otherPartyImage.isNullOrEmpty()) {
                    AsyncImage(
                        model = otherPartyImage,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.Person, null, tint = GreenPrimary, modifier = Modifier.size(24.dp))
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Stack of images for the swap
            Box(modifier = Modifier.width(96.dp)) {
                // ... rest of the code ...
                // Item 1 (Left)
                AsyncImage(
                    model = swap.senderProductImage,
                    contentDescription = null,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(2.dp, White, RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.img1)
                )

                // Swap Icon Box in the middle
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .align(Alignment.Center)
                        .padding(horizontal = 8.dp) 
                        .clip(RoundedCornerShape(10.dp))
                        .background(GreenLight.copy(alpha = 0.9f))
                        .border(2.dp, White, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.SwapHoriz,
                        null,
                        tint = GreenPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Item 2 (Right)
                AsyncImage(
                    model = swap.receiverProductImage,
                    contentDescription = null,
                    modifier = Modifier
                        .size(52.dp)
                        .align(Alignment.CenterEnd)
                        .clip(RoundedCornerShape(10.dp))
                        .border(2.dp, White, RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.img2)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    otherPartyName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = GreenPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "${swap.senderProductTitle} vs ${swap.receiverProductTitle}",
                    fontSize = 13.sp,
                    color = TextDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Sync,
                        null,
                        tint = GreenPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        swap.status,
                        color = GreenPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                null,
                tint = GrayText.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun MatchBigCard(onChatClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(White),
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
                    Text("Thomas G.", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Text("☆ 4.8 • 1.2 km", fontSize = 14.sp, color = GrayText)
                }

                Text(
                    "98% MATCH",
                    color = GreenPrimary,
                    modifier = Modifier
                        .background(GreenLight.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Has:", fontSize = 16.sp, color = GrayText)
                    ProductImageBox(R.drawable.img3, "Minimalist Watch")
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("Wants:", fontSize = 16.sp, color = GrayText)
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
                colors = ButtonDefaults.buttonColors(GreenPrimary)
            ) {
                Icon(Icons.Default.ChatBubbleOutline, null, tint = White)
                Spacer(modifier = Modifier.width(10.dp))
                Text("Propose a Swap", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = White)
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
                .background(White.copy(alpha = 0.9f), RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            color = GreenPrimary,
            fontSize = 12.sp
        )
    }
}

@Composable
fun SmallMatchCard(onChatClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(White),
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
                Text("Sophie M.", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Text("85% Match • 2.5 km", color = GrayText)
            }

            Box(
                modifier = Modifier
                    .size(58.dp)
                    .background(GreenLight, CircleShape)
                    .clickable { onChatClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ArrowForward, null, tint = GreenPrimary)
            }
        }
    }
}

@Composable
fun MyProductCard(product: Product, onEditClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(White),
        elevation = CardDefaults.cardElevation(2.dp),
        border = BorderStroke(1.dp, GrayText.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = product.images.firstOrNull(),
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.img1)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    product.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    product.category,
                    fontSize = 13.sp,
                    color = GrayText
                )
            }

            IconButton(onClick = onEditClick) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = GreenPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
