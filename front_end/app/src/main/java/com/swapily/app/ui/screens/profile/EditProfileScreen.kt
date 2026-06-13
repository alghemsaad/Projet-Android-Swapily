package com.swapily.app.ui.screens.profile

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.android.gms.location.LocationServices
import com.swapily.app.R
import com.swapily.app.data.model.User
import com.swapily.app.viewmodel.AuthViewModel
import com.swapily.app.ui.components.AppBottomBar
import com.swapily.app.ui.Navigation.Screen
import java.util.*

@SuppressLint("MissingPermission")
@Composable
fun EditProfileScreen(
    navController: NavController,
    viewModel: AuthViewModel
) {
    val context = LocalContext.current
    val user by viewModel.profileUser.collectAsState()
    val updateSuccess by viewModel.updateSuccess.collectAsState()
    val isLoading by viewModel.loading.collectAsState()

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    
    val bg = Color.White
    val lightGreen = Color(0xFFE5F6EA)
    val darkGreen = Color(0xFF0D5C3D)
    val inputBg = Color(0xFFF6F7F7)

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var publicProfile by remember { mutableStateOf(true) }
    var showLocation by remember { mutableStateOf(true) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.uploadImage(it) }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.all { it }
        if (granted) {
            detectLocation(context, fusedLocationClient) { city ->
                location = city
            }
        } else {
            Toast.makeText(context, "Permission refusée", Toast.LENGTH_SHORT).show()
        }
    }

    // Peupler les champs quand l'utilisateur est chargé
    LaunchedEffect(Unit) {
        viewModel.fetchUserProfile()
    }

    LaunchedEffect(user) {
        user?.let {
            fullName = it.name
            email = it.email
            location = it.location
            bio = it.bio
            publicProfile = it.publicProfile
            showLocation = it.showLocation
        }
    }

    // Gérer le succès de la mise à jour
    LaunchedEffect(updateSuccess) {
        if (updateSuccess) {
            Toast.makeText(context, "Profil mis à jour !", Toast.LENGTH_SHORT).show()
            viewModel.resetUpdateSuccess()
            navController.popBackStack()
        }
    }

    Scaffold(
        containerColor = bg,
        bottomBar = { AppBottomBar(navController) }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(lightGreen)
                    .padding(horizontal = 24.dp, vertical = 22.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, null, tint = darkGreen)
                }

                Text("Edit Profile", fontSize = 20.sp, color = darkGreen)

                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = darkGreen, strokeWidth = 2.dp)
                } else {
                    Text(
                        "Save",
                        modifier = Modifier.clickable {
                            val updatedUser = user?.copy(
                                name = fullName,
                                email = email,
                                location = location,
                                bio = bio,
                                publicProfile = publicProfile,
                                showLocation = showLocation
                            ) ?: User(
                                name = fullName,
                                email = email,
                                location = location,
                                bio = bio,
                                publicProfile = publicProfile,
                                showLocation = showLocation
                            )
                            viewModel.updateUserProfile(updatedUser)
                        },
                        color = darkGreen,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box {
                    if (user?.image.isNullOrEmpty()) {
                        Image(
                            painter = painterResource(id = R.drawable.img1),
                            contentDescription = null,
                            modifier = Modifier
                                .size(135.dp)
                                .border(6.dp, Color(0xFFBDF4D5), CircleShape)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(user?.image)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Profile Image",
                            modifier = Modifier
                                .size(135.dp)
                                .border(6.dp, Color(0xFFBDF4D5), CircleShape)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(58.dp)
                            .background(darkGreen, CircleShape)
                            .border(3.dp, Color.White, CircleShape)
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.PhotoCamera, null, tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text("Update photo", fontSize = 16.sp, letterSpacing = 1.sp)
            }

            Spacer(modifier = Modifier.height(36.dp))

            EditInput(
                label = "Full Name",
                value = fullName,
                onValueChange = { fullName = it },
                inputBg = inputBg
            )

            EditInput(
                label = "Email",
                value = email,
                onValueChange = { email = it },
                inputBg = inputBg
            )

            LocationSelector(
                label = "Location",
                value = location,
                onValueChange = { location = it },
                onDetectClick = {
                    val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    
                    if (hasFine || hasCoarse) {
                        Toast.makeText(context, "Détection en cours...", Toast.LENGTH_SHORT).show()
                        detectLocation(context, fusedLocationClient) { city ->
                            location = city
                        }
                    } else {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                },
                inputBg = inputBg
            )

            EditInput(
                label = "Short Bio",
                value = bio,
                onValueChange = { bio = it },
                inputBg = inputBg,
                height = 145.dp
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                "Privacy & Visibility",
                modifier = Modifier.padding(horizontal = 30.dp),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = darkGreen
            )

            Spacer(modifier = Modifier.height(18.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = lightGreen)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    PrivacyRow(
                        title = "Public Profile",
                        subtitle = "Allow others to find your items",
                        checked = publicProfile,
                        onCheckedChange = { publicProfile = it }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    PrivacyRow(
                        title = "Show Location",
                        subtitle = "Approximate city visible to peers",
                        checked = showLocation,
                        onCheckedChange = { showLocation = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(58.dp))

            OutlinedButton(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(2.dp, darkGreen)
            ) {
                Icon(Icons.Default.LockReset, null, tint = darkGreen)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Change Password", color = darkGreen, fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@SuppressLint("MissingPermission")
private fun detectLocation(
    context: Context,
    fusedLocationClient: com.google.android.gms.location.FusedLocationProviderClient,
    onLocationDetected: (String) -> Unit
) {
    // On demande la position actuelle plutôt que la dernière connue pour plus de précision
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
                    val city = address.locality ?: address.subAdminArea ?: "Ville Inconnue"
                    val country = address.countryName ?: ""
                    onLocationDetected("$city, $country")
                } else {
                    onLocationDetected("Localisation non trouvée")
                }
            } catch (e: Exception) {
                onLocationDetected("Erreur Geocoder")
            }
        } else {
            onLocationDetected("Position indisponible (Activez le GPS)")
        }
    }.addOnFailureListener {
        onLocationDetected("Échec de la détection")
    }
}

@Composable
fun EditInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    inputBg: Color,
    height: Dp = 88.dp,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 10.dp)
    ) {
        Text(label, fontSize = 15.sp, fontWeight = FontWeight.Medium)

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(height),
            shape = RoundedCornerShape(14.dp),
            trailingIcon = trailingIcon,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = inputBg,
                unfocusedContainerColor = inputBg,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            )
        )
    }
}

@Composable
fun LocationSelector(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    onDetectClick: () -> Unit,
    inputBg: Color
) {
    val darkGreen = Color(0xFF0D5C3D)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 10.dp)
    ) {
        Text(label, fontSize = 15.sp, fontWeight = FontWeight.Medium)

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            placeholder = { Text("Ville, Pays", color = Color.Gray) },
            leadingIcon = {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = darkGreen,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                IconButton(onClick = onDetectClick) {
                    Icon(
                        Icons.Default.MyLocation,
                        contentDescription = "Détecter",
                        tint = darkGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = inputBg,
                unfocusedContainerColor = inputBg,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            )
        )
    }
}

@Composable
fun PrivacyRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val darkGreen = Color(0xFF0D5C3D)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, fontSize = 15.sp)
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = darkGreen
            )
        )
    }
}