package com.swapily.app.ui.screens.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.android.gms.location.LocationServices
import com.swapily.app.R
import com.swapily.app.data.model.Product
import com.swapily.app.data.model.User
import com.swapily.app.ui.Navigation.Screen
import com.swapily.app.ui.components.AppBottomBar
import com.swapily.app.ui.theme.*
import com.swapily.app.viewmodel.ProductViewModel
import com.swapily.app.viewmodel.AuthViewModel
import java.util.Locale

@SuppressLint("MissingPermission")
@Composable
fun HomeScreen(
    navController: NavController,
    productViewModel: ProductViewModel,
    authViewModel: AuthViewModel
) {

    val context = LocalContext.current
    val user by authViewModel.profileUser.collectAsState()
    val favorites by productViewModel.favorites.collectAsState()
    
    LaunchedEffect(Unit) {
        authViewModel.fetchUserProfile()
    }

    LaunchedEffect(user) {
        productViewModel.syncFavorites(user)
    }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedLocation by remember { mutableStateOf("All") }
    var showLocationMenu by remember { mutableStateOf(false) }

    val categories = listOf("All", "Electronics", "Clothing", "Home", "Sports", "Books")
    val locations = listOf("All", "Marrakech", "Casablanca", "Rabat", "London, UK")

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.all { it }
        if (granted) {
            detectLocation(context, fusedLocationClient) { city ->
                selectedLocation = city
            }
        } else {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    val products by productViewModel.availableProducts.collectAsState()
    val isLoading by productViewModel.loading.collectAsState()

    val filteredProducts = products.filter {
        val matchesUser = it.userId != user?.uid
        val matchesSearch = it.title.contains(searchQuery, ignoreCase = true) ||
                it.description.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == "All" || it.category.equals(selectedCategory, ignoreCase = true)
        val matchesLocation = selectedLocation == "All" || 
                it.location.contains(selectedLocation, ignoreCase = true) || 
                selectedLocation.contains(it.location, ignoreCase = true)
        matchesUser && matchesSearch && matchesCategory && matchesLocation
    }

    Scaffold(
        containerColor = Background,
        bottomBar = { AppBottomBar(navController) }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {

            item {
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Default.Search, null, tint = GreenPrimary)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Swapily",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenPrimary
                        )
                    }

                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Default.NotificationsNone, null, tint = GreenPrimary)
                    }
                }
            }

            item {
                Box {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showLocationMenu = true },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocationOn, null, tint = GreenPrimary)
                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = if (selectedLocation == "All") "Select Location" else selectedLocation,
                            fontSize = 16.sp,
                            color = TextDark,
                            modifier = Modifier.weight(1f)
                        )

                        Icon(Icons.Default.KeyboardArrowDown, null, tint = GrayText)
                    }

                    DropdownMenu(
                        expanded = showLocationMenu,
                        onDismissRequest = { showLocationMenu = false },
                        modifier = Modifier.background(White)
                    ) {
                        DropdownMenuItem(
                            text = { 
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.MyLocation, null, tint = GreenPrimary, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Use Current Location")
                                }
                            },
                            onClick = {
                                showLocationMenu = false
                                val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                
                                if (hasFine || hasCoarse) {
                                    Toast.makeText(context, "Detecting...", Toast.LENGTH_SHORT).show()
                                    detectLocation(context, fusedLocationClient) { city ->
                                        selectedLocation = city
                                    }
                                } else {
                                    permissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                }
                            }
                        )

                        HorizontalDivider(color = Background)

                        locations.forEach { location ->
                            DropdownMenuItem(
                                text = { Text(location) },
                                onClick = {
                                    selectedLocation = location
                                    showLocationMenu = false
                                }
                            )
                        }
                    }
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
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(end = 20.dp)
                ) {
                    items(categories) { category ->
                        CategoryChip(
                            text = category,
                            selected = category == selectedCategory,
                            onClick = { selectedCategory = category }
                        )
                    }
                }
            }

            item {
                SmartMatchCard(onClick = { navController.navigate(Screen.Swaps.route) })
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
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                }
            }

            if (isLoading && products.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = GreenPrimary)
                    }
                }
            }

            items(filteredProducts.chunked(2)) { pair ->
                if (pair.size == 2) {
                    ProductRow(pair[0], pair[1], navController, favorites, user, productViewModel, authViewModel)
                } else {
                    ProductCard(pair[0], navController, Modifier.fillMaxWidth(0.5f), favorites.contains(pair[0].id), user, productViewModel, authViewModel)
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@SuppressLint("MissingPermission")
private fun detectLocation(
    context: Context,
    fusedLocationClient: com.google.android.gms.location.FusedLocationProviderClient,
    onLocationDetected: (String) -> Unit
) {
    fusedLocationClient.getCurrentLocation(
        com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
        null
    ).addOnSuccessListener { loc ->
        if (loc != null) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(loc.latitude, loc.longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    val city = address.locality ?: address.subAdminArea ?: "Unknown City"
                    onLocationDetected(city)
                } else {
                    onLocationDetected("Location not found")
                }
            } catch (e: Exception) {
                onLocationDetected("Geocoder Error")
            }
        } else {
            onLocationDetected("GPS Disabled")
        }
    }.addOnFailureListener {
        onLocationDetected("Detection Failed")
    }
}

@Composable
fun CategoryChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(
                if (selected) GreenPrimary else GreenLight.copy(alpha = 0.5f),
                RoundedCornerShape(24.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) White else GreenPrimary,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
    }
}

@Composable
fun SmartMatchCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(315.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = GreenPrimary
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
                    color = White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Swap your camera\nfor a drone?",
                    color = White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 34.sp
                )

                Spacer(modifier = Modifier.height(22.dp))

                Text(
                    text = "Based on your wishlist and\navailable items.",
                    color = White,
                    fontSize = 17.sp,
                    lineHeight = 23.sp
                )

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = onClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = White
                    ),
                    modifier = Modifier.height(52.dp)
                ) {
                    Text(
                        text = "View Swap",
                        color = GreenPrimary,
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
                color = White.copy(alpha = 0.28f),
                fontSize = 120.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ProductRow(
    first: Product, 
    second: Product, 
    navController: NavController,
    favorites: Set<String>,
    user: User?,
    productViewModel: ProductViewModel,
    authViewModel: AuthViewModel
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        ProductCard(first, navController, Modifier.weight(1f), favorites.contains(first.id), user, productViewModel, authViewModel)
        ProductCard(second, navController, Modifier.weight(1f), favorites.contains(second.id), user, productViewModel, authViewModel)
    }
}

@Composable
fun ProductCard(
    product: Product, 
    navController: NavController, 
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false,
    user: User? = null,
    productViewModel: ProductViewModel? = null,
    authViewModel: AuthViewModel? = null
) {
    val context = LocalContext.current
    Card(
        modifier = modifier
            .height(260.dp)
            .clickable { navController.navigate(Screen.ProductDetail.createRoute(product.id)) },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = product.images.firstOrNull(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.img1)
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .background(GreenPrimary, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Text(
                        product.condition.ifEmpty { "NEW" },
                        color = White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp)
                        .size(52.dp)
                        .background(GreenLight, CircleShape)
                        .clickable {
                            if (user == null) {
                                android.widget.Toast.makeText(context, "Please login first", android.widget.Toast.LENGTH_SHORT).show()
                            } else {
                                productViewModel?.toggleFavorite(product.id, user) { updatedUser ->
                                    authViewModel?.updateUserProfile(updatedUser)
                                    val msg = if (!isFavorite) "Added to favorites" else "Removed"
                                    android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = if (isFavorite) Color.Red else GreenPrimary
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    product.title,
                    fontSize = 20.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = TextDark
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    product.location.ifEmpty { "Marrakech" },
                    color = GrayText,
                    fontSize = 14.sp
                )
            }
        }
    }
}
