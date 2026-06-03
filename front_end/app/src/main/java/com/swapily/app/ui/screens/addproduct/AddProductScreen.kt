package com.swapily.app.ui.screens.addproduct

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.swapily.app.R
import com.swapily.app.ui.components.AppBottomBar
import com.swapily.app.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddProductScreen(navController: NavController, productViewModel: ProductViewModel = viewModel()) {

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var lookingFor by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Electronics") }
    var imageUris by remember { mutableStateOf<List<android.net.Uri>>(emptyList()) }

    val context = LocalContext.current
    val isLoading by productViewModel.loading.collectAsState()
    val isSuccess by productViewModel.addProductSuccess.collectAsState()
    val error by productViewModel.error.collectAsState()

    val primaryGreen = Color(0xFF0D5C3D)
    val lightGreen = Color(0xFFE8F8EF)
    val grayText = Color(0xFF5B5B5B)
    val sectionTitleColor = Color(0xFF707070)
    val borderColor = Color(0xFFE0E0E0)

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        imageUris = (imageUris + uris).take(5)
    }

    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            Toast.makeText(context, "Produit ajouté !", Toast.LENGTH_SHORT).show()
            productViewModel.resetAddProductSuccess()
            navController.popBackStack()
        }
    }

    LaunchedEffect(error) {
        if (error.isNotEmpty()) {
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(text = "Swapily", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = primaryGreen)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = primaryGreen)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Outlined.Notifications, "Notifications", tint = primaryGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF1FDF5))
            )
        },
        bottomBar = { AppBottomBar(navController) }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(text = "Add a product", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = primaryGreen)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Give a second life to your items.", fontSize = 14.sp, color = grayText)
            Spacer(modifier = Modifier.height(30.dp))

            SectionHeader(text = "1. ITEM PHOTOS", color = sectionTitleColor)
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                PhotoActionCard(
                    icon = Icons.Default.AddAPhoto,
                    label = "Take a photo",
                    modifier = Modifier.weight(1f).clickable { /* TODO Camera */ },
                    primaryGreen = primaryGreen,
                    lightGreen = lightGreen
                )
                PhotoActionCard(
                    icon = Icons.Default.Image,
                    label = "Gallery",
                    modifier = Modifier.weight(1f).clickable { galleryLauncher.launch("image/*") },
                    primaryGreen = primaryGreen,
                    lightGreen = lightGreen
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                imageUris.forEach { uri ->
                    Box(modifier = Modifier.size(80.dp).clip(RoundedCornerShape(10.dp))) {
                        AsyncImage(model = uri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                        IconButton(
                            onClick = { imageUris = imageUris - uri },
                            modifier = Modifier.align(Alignment.TopEnd).size(24.dp).padding(4.dp).background(Color.Black.copy(0.5f), CircleShape)
                        ) {
                            Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(12.dp))
                        }
                    }
                }
                if (imageUris.size < 5) {
                    Box(
                        modifier = Modifier.size(80.dp).border(1.dp, borderColor, RoundedCornerShape(10.dp)).clickable { galleryLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Add, null, tint = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
            SectionHeader(text = "2. PRODUCT DETAILS", color = sectionTitleColor)
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("Listing title", color = Color.LightGray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = borderColor, focusedBorderColor = primaryGreen)
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("Item description...", color = Color.LightGray, fontSize = 14.sp) },
                modifier = Modifier.fillMaxWidth().height(140.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = borderColor, focusedBorderColor = primaryGreen)
            )

            Spacer(modifier = Modifier.height(30.dp))
            SectionHeader(text = "3. CATEGORY", color = sectionTitleColor)
            Spacer(modifier = Modifier.height(12.dp))

            FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val categories = listOf("Electronics", "Clothing", "Home", "Hobbies", "Other")
                categories.forEach { category ->
                    val isSelected = category == selectedCategory
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        label = { Text(category, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = primaryGreen, selectedLabelColor = Color.White, containerColor = Color(0xFFD7F0E0), labelColor = primaryGreen),
                        border = null,
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
            SectionHeader(text = "4. WHAT ARE YOU LOOKING FOR?", color = sectionTitleColor)
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = lookingFor,
                onValueChange = { lookingFor = it },
                placeholder = { Text("Ex: A bike, books...", color = Color.LightGray) },
                leadingIcon = { Icon(Icons.Default.SwapHoriz, null, tint = primaryGreen) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = borderColor, focusedBorderColor = primaryGreen)
            )

            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth().background(Color(0xFFEBF2EF), RoundedCornerShape(12.dp)).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "Be precise to increase your chances.", fontSize = 12.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(40.dp))
            Button(
                onClick = { 
                    if (title.isNotEmpty() && imageUris.isNotEmpty()) {
                        productViewModel.addProduct(title, description, selectedCategory, lookingFor, imageUris)
                    } else {
                        Toast.makeText(context, "Title and at least one image required", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryGreen)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Publish my listing", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(10.dp))
                        Icon(Icons.AutoMirrored.Filled.Send, null, modifier = Modifier.size(20.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun SectionHeader(text: String, color: Color) {
    Text(text = text, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
}

@Composable
fun PhotoActionCard(icon: ImageVector, label: String, modifier: Modifier = Modifier, primaryGreen: Color, lightGreen: Color) {
    Box(modifier = modifier.height(125.dp).background(lightGreen, RoundedCornerShape(12.dp)).border(1.dp, primaryGreen.copy(alpha = 0.2f), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = icon, null, tint = primaryGreen, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = label, color = primaryGreen, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
