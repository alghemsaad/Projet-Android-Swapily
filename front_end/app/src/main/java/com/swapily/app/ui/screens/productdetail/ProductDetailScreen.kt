package com.swapily.app.ui.screens.productdetail

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.swapily.app.R
import com.swapily.app.data.model.Product
import com.swapily.app.data.model.User
import com.swapily.app.viewmodel.ProductViewModel
import com.swapily.app.viewmodel.AuthViewModel

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import com.swapily.app.ui.Navigation.Screen

@Composable
fun ProductDetailScreen(
    navController: NavController, 
    productId: String,
    productViewModel: ProductViewModel,
    authViewModel: AuthViewModel
) {
    val products by productViewModel.products.collectAsState()
    val favorites by productViewModel.favorites.collectAsState()
    val currentUser by authViewModel.profileUser.collectAsState()
    val product = products.find { it.id == productId }
    val context = LocalContext.current
    
    var productOwner by remember { mutableStateOf<User?>(null) }
    val isFavorite = favorites.contains(productId)
    
    var showReviewDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        authViewModel.fetchUserProfile()
    }

    LaunchedEffect(currentUser) {
        productViewModel.syncFavorites(currentUser)
    }

    LaunchedEffect(product) {
        product?.let {
            productOwner = authViewModel.getOtherUserProfile(it.userId)
        }
    }
    
    val primaryGreen = Color(0xFF0D5C3D)
    val lightGreen = Color(0xFFE8F8EF)
    val grayText = Color(0xFF707070)
    val impactBg = Color(0xFF0A2E1F)

    if (product == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = primaryGreen)
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { product.images.size })

    Scaffold(
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .navigationBarsPadding(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedIconButton(
                        onClick = { /* TODO Chat */ },
                        modifier = Modifier.size(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color.LightGray)
                    ) {
                        Icon(Icons.Default.ChatBubbleOutline, contentDescription = "Chat")
                    }

                    Button(
                        onClick = { /* TODO Propose Swap */ },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryGreen)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.SwapHoriz, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Propose a Swap", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // TOP IMAGE SECTION (Carousel with HorizontalPager)
            Box(modifier = Modifier.fillMaxWidth().height(350.dp)) {
                if (product.images.isNotEmpty()) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        AsyncImage(
                            model = product.images[page],
                            contentDescription = "Product Image ${page + 1}",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(id = R.drawable.img1)
                        )
                    }
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.img1),
                        contentDescription = "Placeholder",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // Navigation Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CircleButton(icon = Icons.AutoMirrored.Filled.ArrowBack, onClick = { navController.popBackStack() })
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        CircleButton(icon = Icons.Outlined.Share, onClick = { /* TODO */ })
                        CircleButton(
                            icon = if (isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                            iconColor = if (isFavorite) Color.Red else Color.Black,
                            onClick = { 
                                if (currentUser == null) {
                                    android.widget.Toast.makeText(context, "Please login first", android.widget.Toast.LENGTH_SHORT).show()
                                } else {
                                    productViewModel.toggleFavorite(productId, currentUser) { updatedUser ->
                                        authViewModel.updateUserProfile(updatedUser)
                                        val message = if (!isFavorite) "Added to favorites" else "Removed from favorites"
                                        android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        )
                    }
                }

                // Image Indicators (if more than 1)
                if (product.images.size > 1) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        product.images.forEachIndexed { index, _ ->
                            val isSelected = pagerState.currentPage == index
                            Box(
                                modifier = Modifier
                                    .size(width = if (isSelected) 30.dp else 15.dp, height = 4.dp)
                                    .background(
                                        if (isSelected) Color.White else Color.White.copy(alpha = 0.5f),
                                        RoundedCornerShape(2.dp)
                                    )
                            )
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
                // BADGES
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    BadgeChip(text = product.condition, color = Color(0xFFD7F0E0), textColor = primaryGreen)
                    BadgeChip(text = "Eco-Friendly", color = Color(0xFFD7F0E0), textColor = primaryGreen, icon = Icons.Default.Eco)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // TITLE
                Text(
                    text = product.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                // LOCATION & CATEGORY
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = grayText, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${product.location}  •  ${product.category}", color = grayText, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // USER INFO (OWNER)
                OwnerCard(productOwner, primaryGreen, grayText, onReviewClick = { showReviewDialog = true }, onViewClick = {
                    productOwner?.let { navController.navigate(Screen.PublicProfile.createRoute(it.uid)) }
                })

                if (showReviewDialog && productOwner != null) {
                    ReviewDialog(
                        ownerName = productOwner?.name ?: "User",
                        onDismiss = { showReviewDialog = false },
                        onSubmit = { rating, comment ->
                            authViewModel.submitReview(productOwner!!.uid, rating, comment)
                            showReviewDialog = false
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // SWAP MATCH CARD
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFEEEEEE)),
                    color = Color(0xFFF9FBF9)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.QueryStats, contentDescription = null, tint = primaryGreen)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Swap Match", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            }
                            Text(text = "95%", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = primaryGreen)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        LinearProgressIndicator(
                            progress = { 0.95f },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                            color = primaryGreen,
                            trackColor = Color(0xFFE0E0E0)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "This item matches what you're looking for: \"${product.lookingFor}\".",
                            fontSize = 13.sp,
                            color = grayText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // DESCRIPTION
                Text(text = "Description", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = product.description,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    color = Color.Black.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // TAGS
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TagChip("#${product.category}")
                    TagChip("#Sustainable")
                }

                Spacer(modifier = Modifier.height(30.dp))

                // CIRCULAR IMPACT
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = impactBg
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(44.dp).background(primaryGreen.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Eco, contentDescription = null, tint = Color(0xFF67B58D))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(text = "Circular Impact", color = Color.White, fontWeight = FontWeight.Bold)
                            Text(
                                text = "Swapping this item saves approximately 4.5kg of CO2 compared to buying new.",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun OwnerCard(user: User?, primaryGreen: Color, grayText: Color, onReviewClick: () -> Unit, onViewClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                if (user?.image.isNullOrEmpty()) {
                    Image(
                        painter = painterResource(id = R.drawable.img1),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    AsyncImage(
                        model = user!!.image,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
                Box(
                    modifier = Modifier.size(16.dp).background(primaryGreen, CircleShape).align(Alignment.BottomEnd).border(1.5.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = user?.name ?: "Loading...", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onReviewClick() }
                ) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB400), modifier = Modifier.size(14.dp))
                    Text(text = " ${user?.rating ?: 0.0} (${user?.reviewsCount ?: 0} reviews)", color = grayText, fontSize = 12.sp)
                }
            }
            OutlinedButton(
                onClick = onViewClick,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.LightGray),
                modifier = Modifier.height(36.dp)
            ) {
                Text("View", color = Color.Black)
            }
        }
    }
}

@Composable
fun ReviewDialog(ownerName: String, onDismiss: () -> Unit, onSubmit: (Int, String) -> Unit) {
    var rating by remember { mutableStateOf(5) }
    var comment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Review $ownerName") },
        text = {
            Column {
                Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                    repeat(5) { index ->
                        val starIndex = index + 1
                        IconButton(onClick = { rating = starIndex }) {
                            Icon(
                                imageVector = if (starIndex <= rating) Icons.Default.Star else Icons.Outlined.StarBorder,
                                contentDescription = null,
                                tint = if (starIndex <= rating) Color(0xFFFFB400) else Color.Gray
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Your comment") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSubmit(rating, comment) }) {
                Text("Submit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun CircleButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector, 
    iconColor: Color = Color.Black,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.size(44.dp),
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.9f)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = iconColor)
        }
    }
}

@Composable
fun BadgeChip(text: String, color: Color, textColor: Color, icon: androidx.compose.ui.graphics.vector.ImageVector? = null) {
    Surface(
        color = color,
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = textColor, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(text = text, color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun TagChip(text: String) {
    Surface(
        color = Color(0xFFE8F8EF),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            color = Color(0xFF0D5C3D),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
