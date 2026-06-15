package com.swapily.app.ui.screens.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.swapily.app.viewmodel.SmartMatchViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: AuthViewModel,
    productViewModel: ProductViewModel,
    swapViewModel: SwapViewModel,
    smartMatchViewModel: SmartMatchViewModel = viewModel()
) {

    val user by viewModel.profileUser.collectAsState()
    val isLoading by viewModel.loading.collectAsState()
    val allProducts by productViewModel.products.collectAsState()
    val myAvailableProducts by productViewModel.myAvailableProducts.collectAsState()
    // All user products (including swapped) for stats
    val userProducts = allProducts.filter { it.userId == user?.uid }
    // Only available user products for the My Products list
    val userProductsAvailable = myAvailableProducts

    val matches by smartMatchViewModel.matches.collectAsState()
    val isMatchesLoading by smartMatchViewModel.loading.collectAsState()

    val allSwaps by swapViewModel.swaps.collectAsState()
    val swapHistory by swapViewModel.swapHistory.collectAsState()
    val userSwapsList = allSwaps.filter { it.senderId == user?.uid || it.receiverId == user?.uid }
    val totalSwapsCount = userSwapsList.size

    var showAllProducts by remember { mutableStateOf(false) }
    val displayedProducts = if (showAllProducts) userProductsAvailable else userProductsAvailable.take(2)
    
    var selectedTab by remember { mutableStateOf("Ongoing") }
    val favoriteProducts = allProducts.filter { user?.favorites?.contains(it.id) == true && it.status == "available" }

    LaunchedEffect(user) {
        user?.uid?.let { uid ->
            productViewModel.fetchMyAvailableProducts(uid)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchUserProfile()
        swapViewModel.fetchSwaps()
        swapViewModel.fetchSwapHistory()
        smartMatchViewModel.loadMatches()
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
                        if (userProductsAvailable.size > 2 && !showAllProducts) {
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

            if (userProductsAvailable.isEmpty()) {
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

            // --- Smart Match Section ---
            item {
                Text(
                    "Smart Matches",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (isMatchesLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = GreenPrimary, modifier = Modifier.size(30.dp))
                    }
                }
            } else if (matches.isEmpty()) {
                item {
                    Text(
                        "No matches found for your products yet. Try adding more details about what you're looking for!",
                        fontSize = 14.sp,
                        color = GrayText,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            } else {
                item {
                    Text(
                        "Suggested items that match your wishlist",
                        fontSize = 14.sp,
                        color = GrayText,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 12.dp)
                    ) {
                        items(matches) { match ->
                            MatchResultCard(
                                match = match,
                                onProposeClick = {
                                    navController.navigate(Screen.ProductDetail.createRoute(match.otherProduct.id))
                                }
                            )
                        }
                    }
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
                        val historySwaps = swapHistory
                        if (historySwaps.isEmpty()) {
                            Text("No history yet.", color = GrayText, modifier = Modifier.padding(vertical = 20.dp))
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                historySwaps.forEach { swap ->
                                    HistorySwapCard(swap, user?.uid ?: "", onClick = {
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

        Spacer(modifier = Modifier.weight(1f))

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
                StatItem(totalSwapsCount.toString(), "Swaps", Modifier.weight(1f))
                val formattedRating = "%.1f".format(user?.rating ?: 0.0)
                StatItem(formattedRating, "Rating", Modifier.weight(1f))
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

/**
 * History swap card: shows accepted swaps with both products info and accepted date.
 */
@Composable
fun HistorySwapCard(swap: com.swapily.app.data.model.Swap, currentUserUid: String, onClick: () -> Unit) {
    val isSender = swap.senderId == currentUserUid
    val otherPartyName = if (isSender) swap.receiverName else swap.senderName
    val otherPartyImage = if (isSender) swap.receiverImage else swap.senderImage
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    val acceptedDateStr = if (swap.acceptedAt > 0L) dateFormat.format(Date(swap.acceptedAt)) else ""

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(White),
        elevation = CardDefaults.cardElevation(2.dp),
        border = BorderStroke(1.dp, GreenLight.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Other party profile image
                Box(
                    modifier = Modifier
                        .size(40.dp)
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
                        Icon(Icons.Default.Person, null, tint = GreenPrimary, modifier = Modifier.size(20.dp))
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Swap with $otherPartyName",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (acceptedDateStr.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.CalendarToday,
                                null,
                                tint = GrayText,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Accepted $acceptedDateStr",
                                fontSize = 12.sp,
                                color = GrayText
                            )
                        }
                    }
                }

                Surface(
                    color = GreenLight,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "ACCEPTED",
                        fontSize = 10.sp,
                        color = GreenPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Both products
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sender product
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = swap.senderProductImage,
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = R.drawable.img1)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            swap.senderProductTitle,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextDark,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text("Sent", fontSize = 11.sp, color = GrayText)
                    }
                }

                Icon(
                    Icons.Default.SwapHoriz,
                    null,
                    tint = GreenPrimary,
                    modifier = Modifier.size(20.dp)
                )

                // Receiver product
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = swap.receiverProductImage,
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = R.drawable.img2)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            swap.receiverProductTitle,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextDark,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text("Received", fontSize = 11.sp, color = GrayText)
                    }
                }
            }
        }
    }
}

@Composable
fun MatchResultCard(match: com.swapily.app.data.model.MatchResult, onProposeClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(300.dp)
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    match.otherUserName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Surface(
                    color = GreenLight,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "${match.score}% MATCH",
                        color = GreenPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Has:", fontSize = 12.sp, color = GrayText, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(modifier = Modifier.height(100.dp).fillMaxWidth().clip(RoundedCornerShape(8.dp))) {
                        AsyncImage(
                            model = match.otherProduct.images.firstOrNull(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(id = R.drawable.img1)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(match.otherProduct.title, fontSize = 13.sp, maxLines = 1, fontWeight = FontWeight.Medium, color = TextDark, overflow = TextOverflow.Ellipsis)
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("Wants:", fontSize = 12.sp, color = GrayText, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(modifier = Modifier.height(100.dp).fillMaxWidth().clip(RoundedCornerShape(8.dp))) {
                        AsyncImage(
                            model = match.myProduct.images.firstOrNull(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(id = R.drawable.img1)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(match.myProduct.title, fontSize = 13.sp, maxLines = 1, fontWeight = FontWeight.Medium, color = TextDark, overflow = TextOverflow.Ellipsis)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onProposeClick,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) {
                Text("Propose a Swap", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
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
                if (!product.isAvailable) {
                    Text(
                        "SWAPPED",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = GreenPrimary,
                        modifier = Modifier
                            .background(GreenLight, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
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
