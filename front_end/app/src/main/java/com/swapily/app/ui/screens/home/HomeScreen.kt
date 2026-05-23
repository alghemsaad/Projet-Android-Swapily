package com.swapily.app.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.swapily.app.R
import com.swapily.app.ui.Navigation.Screen

data class ProductUi(
    val title: String,
    val subtitle: String,
    val badge: String,
    val image: Int
)

@Composable
fun HomeScreen(navController: NavController) {

    val bg = Color(0xFFE5F6EA)
    val darkGreen = Color(0xFF0D5C3D)

    var searchQuery by remember { mutableStateOf("") }
    val location = "London, UK"

    val products = listOf(
        ProductUi("Analog Watch", "Verified Seller", "LIKE NEW", R.drawable.img3),
        ProductUi("Bluetooth Head...", "Eco-Contributor", "GOOD CONDITION", R.drawable.img1),
        ProductUi("Designer Glass ...", "2km away", "NEARLY NEW", R.drawable.img3),
        ProductUi("Ceramic Vase", "Top Swapper", "USED", R.drawable.img2)
    )

    val filteredProducts = products.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
        it.subtitle.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        containerColor = bg,
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 26.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {

            item {
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Search, null, tint = darkGreen)

                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = null,
                        modifier = Modifier.size(58.dp),
                        contentScale = ContentScale.Fit
                    )

                    Icon(Icons.Default.NotificationsNone, null, tint = darkGreen)
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.LocationOn, null, tint = darkGreen)
                    Spacer(modifier = Modifier.width(10.dp))
                    
                    Text(
                        text = location,
                        fontSize = 16.sp,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )
                    
                    Icon(Icons.Default.KeyboardArrowDown, null)
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(62.dp)
                        .background(Color.White.copy(alpha = 0.85f), RoundedCornerShape(18.dp)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                text = "Search for items...",
                                color = Color.Gray,
                                fontSize = 18.sp
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    Icon(
                        Icons.Default.FilterList, 
                        null, 
                        tint = Color.DarkGray,
                        modifier = Modifier.padding(end = 18.dp)
                    )
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    CategoryChip("All", true)
                    CategoryChip("Electronics", false)
                    CategoryChip("Clothing", false)
                    CategoryChip("Home", false)
                }
            }

            item {
                SmartMatchCard()
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Local New Arrivals",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        "View all",
                        color = darkGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            items(filteredProducts.chunked(2)) { pair ->
                if (pair.size == 2) {
                    ProductRow(pair[0], pair[1])
                } else {
                    ProductCard(pair[0], modifier = Modifier.fillMaxWidth(0.5f))
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun CategoryChip(text: String, selected: Boolean) {

    val darkGreen = Color(0xFF0D5C3D)

    Box(
        modifier = Modifier
            .background(
                if (selected) darkGreen else Color(0xFFEFF3F0),
                RoundedCornerShape(24.dp)
            )
            .border(
                1.dp,
                if (selected) darkGreen else Color(0xFFB8C5BD),
                RoundedCornerShape(24.dp)
            )
            .padding(horizontal = 18.dp, vertical = 11.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else Color.Black,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun SmartMatchCard() {

    val darkGreen = Color(0xFF0D5C3D)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(315.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = darkGreen
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp)
        ) {

            Column(
                modifier = Modifier.align(Alignment.TopStart)
            ) {

                Text(
                    text = "SMART MATCH",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Swap your camera\nfor a drone?",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 34.sp
                )

                Spacer(modifier = Modifier.height(22.dp))

                Text(
                    text = "Based on your wishlist and\navailable items.",
                    color = Color.White,
                    fontSize = 17.sp,
                    lineHeight = 23.sp
                )

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = {},
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),
                    modifier = Modifier.height(52.dp)
                ) {
                    Text(
                        text = "View Swap",
                        color = darkGreen,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = "⇅",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 10.dp, y = 8.dp),
                color = Color.White.copy(alpha = 0.28f),
                fontSize = 120.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
@Composable
fun ProductRow(first: ProductUi, second: ProductUi) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        ProductCard(first, Modifier.weight(1f))
        ProductCard(second, Modifier.weight(1f))
    }
}

@Composable
fun ProductCard(product: ProductUi, modifier: Modifier = Modifier) {

    val darkGreen = Color(0xFF0D5C3D)

    Card(
        modifier = modifier.height(260.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            Box {
                Image(
                    painter = painterResource(id = product.image),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .background(Color(0xFF294B37), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Text(
                        product.badge,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp)
                        .size(52.dp)
                        .background(Color(0xFFDDF0E3), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.FavoriteBorder,
                        null,
                        tint = darkGreen
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    product.title,
                    fontSize = 20.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    product.subtitle,
                    color = Color.DarkGray,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun BottomNavBar(navController: NavController) {

    val darkGreen = Color(0xFF0D5C3D)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentRoute == Screen.Home.route,
            onClick = {
                if (currentRoute != Screen.Home.route) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            },
            icon = { Icon(Icons.Default.Search, null) },
            label = { Text("Discover") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = darkGreen,
                indicatorColor = darkGreen
            )
        )

        NavigationBarItem(
            selected = currentRoute == Screen.Messages.route,
            onClick = {
                if (currentRoute != Screen.Messages.route) {
                    navController.navigate(Screen.Messages.route)
                }
            },
            icon = { Icon(Icons.Default.SwapHoriz, null) },
            label = { Text("Swaps") }
        )

        NavigationBarItem(
            selected = currentRoute == Screen.AddProduct.route,
            onClick = {
                if (currentRoute != Screen.AddProduct.route) {
                    navController.navigate(Screen.AddProduct.route)
                }
            },
            icon = { Icon(Icons.Default.AddCircle, null) },
            label = { Text("Add") }
        )

        NavigationBarItem(
            selected = currentRoute == Screen.Profile.route,
            onClick = {
                if (currentRoute != Screen.Profile.route) {
                    navController.navigate(Screen.Profile.route)
                }
            },
            icon = { Icon(Icons.Default.PersonOutline, null) },
            label = { Text("Profile") }
        )
    }
}