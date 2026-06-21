package com.swapily.app.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import coil.compose.AsyncImage
import com.swapily.app.R
import com.swapily.app.data.model.Product
import com.swapily.app.ui.theme.*
import com.swapily.app.viewmodel.AdminProductsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductsScreen(
    viewModel: AdminProductsViewModel
) {
    val products by viewModel.products.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val actionSuccess by viewModel.actionSuccess.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var productToDelete by remember { mutableStateOf("") }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    LaunchedEffect(actionSuccess) {
        if (actionSuccess) viewModel.resetActionSuccess()
    }

    val categories = listOf("All", "Electronics", "Clothing", "Home", "Sports", "Books")

    val filteredProducts = products.filter {
        val matchesSearch = it.title.contains(searchQuery, ignoreCase = true) ||
                it.description.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == "All" ||
                it.category.equals(selectedCategory, ignoreCase = true)
        matchesSearch && matchesCategory
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Background)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Products Management",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = GreenPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Search
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Search products...", color = GrayText) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = GreenPrimary) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = White,
                    unfocusedContainerColor = White,
                    focusedBorderColor = GreenPrimary,
                    unfocusedBorderColor = GreenLight
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Category filter
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    FilterChip(
                        selected = category == selectedCategory,
                        onClick = { viewModel.setCategory(category) },
                        label = { Text(category, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GreenPrimary,
                            selectedLabelColor = White,
                            containerColor = GreenLight.copy(alpha = 0.5f),
                            labelColor = GreenPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${filteredProducts.size} products",
                fontSize = 13.sp,
                color = GrayText
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredProducts.isEmpty() && !loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.ShoppingBag, null, tint = GrayText, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No products found", color = GrayText)
                    }
                }
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(filteredProducts, key = { it.id }) { product ->
                    AdminProductCard(
                        product = product,
                        onClick = { selectedProduct = product },
                        onDelete = {
                            productToDelete = product.id
                            showDeleteDialog = true
                        },
                        onArchive = { viewModel.archiveProduct(product.id) }
                    )
                }
                item { Spacer(modifier = Modifier.height(20.dp)) }
            }
        }
    }

    // Product Detail Dialog
    selectedProduct?.let { product ->
        AlertDialog(
            onDismissRequest = { selectedProduct = null },
            containerColor = White,
            title = { Text(product.title, fontWeight = FontWeight.Bold, color = TextDark) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    AsyncImage(
                        model = product.images.firstOrNull(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().height(150.dp),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = R.drawable.img1)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    DetailRow("Category", product.category)
                    DetailRow("Condition", product.condition)
                    DetailRow("Status", product.status)
                    DetailRow("Location", product.location.ifEmpty { "Not set" })
                    DetailRow("Value", "$${product.estimatedValue}")
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedProduct = null }) {
                    Text("Close", color = GreenPrimary)
                }
            }
        )
    }

    // Delete Confirmation
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = White,
            title = { Text("Delete Product", fontWeight = FontWeight.Bold, color = TextDark) },
            text = { Text("Are you sure you want to delete this product?", color = TextDark) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteProduct(productToDelete)
                    showDeleteDialog = false
                }) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = GrayText)
                }
            }
        )
    }
}

@Composable
fun AdminProductCard(
    product: Product,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onArchive: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = product.images.firstOrNull(),
                contentDescription = null,
                modifier = Modifier.size(60.dp).background(Color.LightGray, RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.img1)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = product.category,
                    fontSize = 12.sp,
                    color = GrayText
                )
                Spacer(modifier = Modifier.height(4.dp))
                StatusChip(
                    text = product.status.replaceFirstChar { it.uppercase() },
                    isActive = product.status == "available",
                    color = when (product.status) {
                        "available" -> Color(0xFF4CAF50)
                        "swapped" -> Color(0xFFFF9800)
                        "archived" -> Color(0xFF9E9E9E)
                        else -> GrayText
                    }
                )
            }

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, null, tint = GrayText)
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(White)
                ) {
                    DropdownMenuItem(
                        text = { Text("Archive") },
                        leadingIcon = { Icon(Icons.Default.Archive, null, tint = Color(0xFFFF9800)) },
                        onClick = { onArchive(); showMenu = false }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete", color = Color.Red) },
                        leadingIcon = { Icon(Icons.Default.Delete, null, tint = Color.Red) },
                        onClick = { onDelete(); showMenu = false }
                    )
                }
            }
        }
    }
}
