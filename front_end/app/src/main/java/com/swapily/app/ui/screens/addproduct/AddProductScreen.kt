package com.swapily.app.ui.screens.addproduct

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.swapily.app.R


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddProductScreen(navController: NavController) {

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var lookingFor by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Electronics") }

    val primaryGreen = Color(0xFF0D5C3D)
    val lightGreen = Color(0xFFE8F8EF)
    val grayText = Color(0xFF5B5B5B)
    val sectionTitleColor = Color(0xFF707070)
    val borderColor = Color(0xFFE0E0E0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "SwapIt",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryGreen
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = primaryGreen)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", tint = primaryGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF1FDF5)
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Default.Explore, contentDescription = "Discover") },
                    label = { Text("Discover", fontSize = 10.sp) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Default.SwapHoriz, contentDescription = "Swaps") },
                    label = { Text("Swaps", fontSize = 10.sp) }
                )
                
                // Custom Add button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = CircleShape,
                            color = primaryGreen
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add",
                                tint = Color.White,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        Text(
                            "Add",
                            fontSize = 10.sp,
                            color = primaryGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Outlined.Person, contentDescription = "Profile") },
                    label = { Text("Profile", fontSize = 10.sp) }
                )
            }
        }
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

            // HEADER
            Text(
                text = "Add a product",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = primaryGreen
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Give a second life to your items.",
                fontSize = 14.sp,
                color = grayText
            )

            Spacer(modifier = Modifier.height(30.dp))

            // 1. ITEM PHOTOS
            SectionHeader(text = "1. ITEM PHOTOS", color = sectionTitleColor)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PhotoActionCard(
                    icon = Icons.Default.AddAPhoto,
                    label = "Take a photo",
                    modifier = Modifier.weight(1f),
                    primaryGreen = primaryGreen,
                    lightGreen = lightGreen
                )
                PhotoActionCard(
                    icon = Icons.Default.Image,
                    label = "Gallery",
                    modifier = Modifier.weight(1f),
                    primaryGreen = primaryGreen,
                    lightGreen = lightGreen
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Thumbnail Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Existing Photo (Example)
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(10.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img1),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size(20.dp)
                            .background(primaryGreen, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Remove",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                // Add More placeholder
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .border(1.dp, borderColor, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add more",
                        tint = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // 2. PRODUCT DETAILS
            SectionHeader(text = "2. PRODUCT DETAILS", color = sectionTitleColor)

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("Listing title", color = Color.LightGray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = borderColor,
                    focusedBorderColor = primaryGreen
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { 
                    Text(
                        "Item description (condition, brand, history...)", 
                        color = Color.LightGray,
                        fontSize = 14.sp
                    ) 
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = borderColor,
                    focusedBorderColor = primaryGreen
                )
            )

            Spacer(modifier = Modifier.height(30.dp))

            // 3. CATEGORY
            SectionHeader(text = "3. CATEGORY", color = sectionTitleColor)

            Spacer(modifier = Modifier.height(12.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val categories = listOf("Electronics", "Clothing", "Home", "Hobbies", "Other")
                categories.forEach { category ->
                    val isSelected = category == selectedCategory
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        label = { 
                            Text(
                                category, 
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ) 
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = primaryGreen,
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFFD7F0E0),
                            labelColor = primaryGreen
                        ),
                        border = null,
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // 4. WHAT ARE YOU LOOKING FOR?
            SectionHeader(text = "4. WHAT ARE YOU LOOKING FOR?", color = sectionTitleColor)

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = lookingFor,
                onValueChange = { lookingFor = it },
                placeholder = { Text("Ex: A bike, a console, books...", color = Color.LightGray) },
                leadingIcon = {
                    Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = primaryGreen)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = borderColor,
                    focusedBorderColor = primaryGreen
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // INFO BOX
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFEBF2EF), RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Be precise to increase your chances of getting a relevant swap.",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // FOOTER VERIFIED
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Verified,
                    contentDescription = null,
                    tint = primaryGreen,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Verified by the SwapIt community",
                    fontSize = 12.sp,
                    color = grayText
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // PUBLISH BUTTON
            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryGreen
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Publish my listing",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun SectionHeader(text: String, color: Color) {
    Text(
        text = text,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Start,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = color
    )
}

@Composable
fun PhotoActionCard(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    primaryGreen: Color,
    lightGreen: Color
) {
    Box(
        modifier = modifier
            .height(125.dp)
            .background(lightGreen, RoundedCornerShape(12.dp))
            .border(1.dp, primaryGreen.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = primaryGreen,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = label,
                color = primaryGreen,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
