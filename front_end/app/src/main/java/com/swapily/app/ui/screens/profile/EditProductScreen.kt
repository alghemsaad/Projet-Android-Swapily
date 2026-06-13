package com.swapily.app.ui.screens.profile

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.swapily.app.viewmodel.ProductViewModel
import com.swapily.app.ui.components.AppBottomBar
import com.swapily.app.ui.Navigation.Screen
import com.swapily.app.ui.screens.addproduct.SectionHeader

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditProductScreen(
    navController: NavController,
    productId: String,
    productViewModel: ProductViewModel
) {
    val products by productViewModel.products.collectAsState()
    val product = products.find { it.id == productId }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var lookingFor by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Electronics") }
    // Existing images (URLs)
    var existingImages by remember { mutableStateOf<List<String>>(emptyList()) }
    // New images to upload
    var newImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val context = LocalContext.current
    val isLoading by productViewModel.loading.collectAsState()
    val isSuccess by productViewModel.addProductSuccess.collectAsState() 

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(5)
    ) { uris ->
        newImageUris = (newImageUris + uris).take(5 - existingImages.size)
    }

    val primaryGreen = Color(0xFF0D5C3D)
    val lightGreen = Color(0xFFE8F8EF)
    val grayText = Color(0xFF5B5B5B)
    val sectionTitleColor = Color(0xFF707070)
    val borderColor = Color(0xFFE0E0E0)

    LaunchedEffect(product) {
        product?.let {
            title = it.title
            description = it.description
            lookingFor = it.lookingFor
            location = it.location
            selectedCategory = it.category
            existingImages = it.images
        }
    }

    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            Toast.makeText(context, "Product updated!", Toast.LENGTH_SHORT).show()
            productViewModel.resetAddProductSuccess()
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Product", color = primaryGreen, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = primaryGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF1FDF5))
            )
        },
        bottomBar = { AppBottomBar(navController) }
    ) { paddingValues ->

        if (product == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = primaryGreen)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                SectionHeader(text = "PRODUCT PHOTOS", color = sectionTitleColor)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Existing images
                    existingImages.forEach { url ->
                        Box(modifier = Modifier.size(80.dp).clip(RoundedCornerShape(10.dp))) {
                            AsyncImage(model = url, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                            IconButton(
                                onClick = { existingImages = existingImages - url },
                                modifier = Modifier.align(Alignment.TopEnd).size(24.dp).padding(4.dp).background(Color.Black.copy(0.5f), CircleShape)
                            ) {
                                Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(12.dp))
                            }
                        }
                    }
                    
                    // New images
                    newImageUris.forEach { uri ->
                        Box(modifier = Modifier.size(80.dp).clip(RoundedCornerShape(10.dp))) {
                            AsyncImage(model = uri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                            IconButton(
                                onClick = { newImageUris = newImageUris - uri },
                                modifier = Modifier.align(Alignment.TopEnd).size(24.dp).padding(4.dp).background(Color.Black.copy(0.5f), CircleShape)
                            ) {
                                Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(12.dp))
                            }
                        }
                    }

                    if (existingImages.size + newImageUris.size < 5) {
                        Box(
                            modifier = Modifier.size(80.dp).border(1.dp, borderColor, RoundedCornerShape(10.dp)).clickable {
                                galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Add, null, tint = Color.Gray)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
                SectionHeader(text = "DETAILS", color = sectionTitleColor)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("Listing title") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Item description...") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(30.dp))
                SectionHeader(text = "LOCATION", color = sectionTitleColor)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    placeholder = { Text("City, Country") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, null, tint = primaryGreen) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(30.dp))
                SectionHeader(text = "WHAT ARE YOU LOOKING FOR?", color = sectionTitleColor)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = lookingFor,
                    onValueChange = { lookingFor = it },
                    placeholder = { Text("Ex: A bike, books...") },
                    leadingIcon = { Icon(Icons.Default.SwapHoriz, null, tint = primaryGreen) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = {
                        if (title.isNotEmpty()) {
                            productViewModel.updateProduct(
                                productId = productId,
                                title = title,
                                description = description,
                                category = selectedCategory,
                                lookingFor = lookingFor,
                                location = location,
                                existingImages = existingImages,
                                newImageUris = newImageUris
                            )
                        } else {
                            Toast.makeText(context, "Title is required", Toast.LENGTH_SHORT).show()
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
                        Text("Save Changes", fontWeight = FontWeight.Bold)
                    }
                }
                
                Spacer(modifier = Modifier.height(10.dp))
                
                TextButton(
                    onClick = {
                        productViewModel.deleteProduct(productId)
                    },
                    enabled = !isLoading
                ) {
                    Text("Delete Product", color = Color.Red)
                }
            }
        }
    }
}
