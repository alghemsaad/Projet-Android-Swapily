package com.swapily.app.ui.screens.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.swapily.app.R
import com.swapily.app.data.model.User
import com.swapily.app.viewmodel.AuthViewModel
import com.swapily.app.viewmodel.ProductViewModel
import com.swapily.app.ui.Navigation.Screen
import com.swapily.app.ui.theme.GreenPrimary
import com.swapily.app.ui.theme.White
import com.swapily.app.ui.theme.GrayText
import com.swapily.app.ui.theme.Background

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicProfileScreen(
    navController: NavController,
    userId: String,
    authViewModel: AuthViewModel,
    productViewModel: ProductViewModel
) {
    var user by remember { mutableStateOf<User?>(null) }
    val allProducts by productViewModel.products.collectAsState()
    val userProducts = allProducts.filter { it.userId == userId }
    
    val lightGreenBg = Color(0xFFE8F8EF)

    LaunchedEffect(userId) {
        user = authViewModel.getOtherUserProfile(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Public Profile", color = GreenPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = GreenPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { /* More options */ }) {
                        Icon(Icons.Default.MoreVert, null, tint = GreenPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = lightGreenBg)
            )
        },
        containerColor = Color.White
    ) { padding ->
        if (user == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = GreenPrimary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .background(lightGreenBg.copy(alpha = 0.3f))
            ) {
                // Header section with Avatar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(lightGreenBg)
                        .padding(bottom = 30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box {
                            AsyncImage(
                                model = user?.image.takeIf { !it.isNullOrEmpty() } ?: R.drawable.img3,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .border(4.dp, White, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(28.dp)
                                    .background(GreenPrimary, CircleShape)
                                    .border(2.dp, White, CircleShape)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(user?.name ?: "", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, null, tint = GrayText, modifier = Modifier.size(16.dp))
                            Text(user?.location ?: "No location", color = GrayText, fontSize = 14.sp)
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Surface(
                            color = White.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, GreenPrimary.copy(alpha = 0.2f))
                        ) {
                            Text(
                                "TOP SWAPPER",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GreenPrimary
                            )
                        }
                    }
                }

                // Stats Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .offset(y = (-20).dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatColumn(userProducts.size.toString(), "LISTINGS", Modifier.weight(1f))
                        VerticalDivider(modifier = Modifier.height(40.dp), color = Color.LightGray.copy(alpha = 0.5f))
                        StatColumn(user?.swapsCount.toString(), "SWAPS", Modifier.weight(1f))
                        VerticalDivider(modifier = Modifier.height(40.dp), color = Color.LightGray.copy(alpha = 0.5f))
                        StatColumn(user?.rating.toString(), "RATING", Modifier.weight(1f), isRating = true)
                    }
                }

                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    // About Section
                    Text("About ${user?.name?.split(" ")?.firstOrNull() ?: ""}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        user?.bio ?: "No bio available.",
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        lineHeight = 20.sp
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Trust Badges
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        BadgeCard(
                            icon = Icons.Default.VerifiedUser,
                            title = "Identity Verified",
                            subtitle = "SWAPILY CONFIDENCE",
                            modifier = Modifier.weight(1f)
                        )
                        BadgeCard(
                            icon = Icons.Default.Eco,
                            title = "Eco-Contributor",
                            subtitle = "LEVEL 4",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    // Reviews Section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Reviews (${user?.reviewsCount})", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("See all reviews", color = GreenPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    ReviewItem("Marc L.", "2 weeks ago", "Great swap! The camera was exactly as described. Very friendly and punctual.")
                    ReviewItem("Sarah J.", "1 month ago", "The ceramic pot is beautiful. Elena was very helpful with the exchange process.")

                    Spacer(modifier = Modifier.height(30.dp))

                    // Items for Swap
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Items for Swap", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("View All", color = GreenPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Simple grid for items (since we are in a vertical scroll, we can't use LazyVerticalGrid directly without fixed height)
                    userProducts.chunked(2).forEach { pair ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            PublicProductCard(pair[0], Modifier.weight(1f), navController)
                            if (pair.size > 1) {
                                PublicProductCard(pair[1], Modifier.weight(1f), navController)
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}

@Composable
fun StatColumn(value: String, label: String, modifier: Modifier, isRating: Boolean = false) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(value, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            if (isRating) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Default.Star, null, tint = GreenPrimary, modifier = Modifier.size(16.dp))
            }
        }
        Text(label, fontSize = 10.sp, color = GrayText, letterSpacing = 1.sp)
    }
}

@Composable
fun BadgeCard(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, modifier: Modifier) {
    Surface(
        modifier = modifier,
        color = Color(0xFFE8F8EF),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = GreenPrimary, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(subtitle, fontSize = 9.sp, color = GrayText)
            }
        }
    }
}

@Composable
fun ReviewItem(name: String, time: String, comment: String) {
    Column(modifier = Modifier.padding(bottom = 20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(32.dp).background(Color(0xFFF0F0F0), CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Person, null, modifier = Modifier.size(20.dp), tint = Color.Gray)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(name, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(time, fontSize = 11.sp, color = GrayText)
            }
            Row {
                repeat(5) { Icon(Icons.Default.Star, null, tint = GreenPrimary, modifier = Modifier.size(14.dp)) }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(comment, fontSize = 13.sp, color = Color.Gray, lineHeight = 18.sp)
    }
}

@Composable
fun PublicProductCard(product: com.swapily.app.data.model.Product, modifier: Modifier, navController: NavController) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF9F9F9))
            .clickable { navController.navigate(Screen.ProductDetail.createRoute(product.id)) }
    ) {
        Box {
            AsyncImage(
                model = product.images.firstOrNull(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentScale = ContentScale.Crop
            )
            Surface(
                modifier = Modifier.align(Alignment.TopEnd).padding(8.dp),
                color = GreenPrimary,
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    product.condition.take(4),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    color = White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Column(modifier = Modifier.padding(8.dp)) {
            Text(product.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.History, null, tint = GrayText, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Added 2 days ago", fontSize = 10.sp, color = GrayText)
            }
        }
    }
}
