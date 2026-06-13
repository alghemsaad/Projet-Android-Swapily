package com.swapily.app.ui.screens.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.activity.ComponentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
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

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: AuthViewModel,
    productViewModel: ProductViewModel
) {

    val user by viewModel.profileUser.collectAsState()
    val isLoading by viewModel.loading.collectAsState()
    val allProducts by productViewModel.products.collectAsState()
    val userProducts = allProducts.filter { it.userId == user?.uid }
    
    var selectedTab by remember { mutableStateOf("Ongoing") }
    val favoriteProducts = allProducts.filter { user?.favorites?.contains(it.id) == true }

    LaunchedEffect(Unit) {
        viewModel.fetchUserProfile()
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
                TopProfileBar(onEditClick = {
                    navController.navigate(Screen.EditProfile.route)
                })
            }

            item {
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = GreenPrimary)
                    }
                } else {
                    UserProfileCard(user, userProducts.size)
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
                    TextButton(onClick = { navController.navigate(Screen.AddProduct.route) }) {
                        Text("Add New", color = GreenPrimary)
                    }
                }
            }

            if (userProducts.isEmpty()) {
                item {
                    Text("No products added yet.", color = GrayText, modifier = Modifier.padding(bottom = 8.dp))
                }
            } else {
                items(userProducts) { product ->
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
                    else -> MySwapCard()
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun TopProfileBar(onEditClick: () -> Unit) {
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

        IconButton(onClick = { /* TODO: Notifications */ }) {
            Icon(Icons.Default.NotificationsNone, null, tint = GreenPrimary)
        }
    }
}

@Composable
fun UserProfileCard(user: User?, itemsCount: Int) {
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
                StatItem((user?.swapsCount ?: 0).toString(), "Swaps", Modifier.weight(1f))
                StatItem((user?.rating ?: 0.0).toString(), "Reviews", Modifier.weight(1f))
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
fun MySwapCard() {
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
            // Stack of images for the swap
            Box(modifier = Modifier.width(96.dp)) {
                // Item 1 (Left)
                Image(
                    painter = painterResource(id = R.drawable.img1),
                    contentDescription = null,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(2.dp, White, RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )

                // Swap Icon Box in the middle
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .align(Alignment.Center)
                        .padding(horizontal = 8.dp) // creates overlap look
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
                Image(
                    painter = painterResource(id = R.drawable.img2),
                    contentDescription = null,
                    modifier = Modifier
                        .size(52.dp)
                        .align(Alignment.CenterEnd)
                        .clip(RoundedCornerShape(10.dp))
                        .border(2.dp, White, RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Eco Sneakers vs iPad Mini",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
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
                        "Pending validation",
                        color = GreenPrimary,
                        fontSize = 13.sp,
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
