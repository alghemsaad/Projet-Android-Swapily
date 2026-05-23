package com.swapily.app.ui.screens.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.internal.CallbackManagerImpl
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.swapily.app.R
import com.swapily.app.viewmodel.AuthViewModel

@Composable
fun LoginScreen(navController: NavController) {

    val viewModel: AuthViewModel = viewModel()

    val success by viewModel.success.collectAsState()
    val registerSuccess by viewModel.registerSuccess.collectAsState()
    val error by viewModel.error.collectAsState()
    val loading by viewModel.loading.collectAsState()

    var isLoginMode by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val token = stringResource(id = R.string.default_web_client_id)

    LaunchedEffect(error) {
        if (error.isNotEmpty()) {
            android.widget.Toast.makeText(context, error, android.widget.Toast.LENGTH_LONG).show()
        }
    }

    val googleSignInOptions = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(token)
            .requestEmail()
            .build()
    }

    val googleSignInClient = remember {
        GoogleSignIn.getClient(context, googleSignInOptions)
    }

    val callbackManager = remember { CallbackManager.Factory.create() }

    val facebookLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        callbackManager.onActivityResult(64206, result.resultCode, result.data)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account?.idToken?.let { idToken ->
                viewModel.signInWithGoogle(idToken)
            }
        } catch (e: ApiException) {
            // Afficher l'erreur dans le ViewModel
            viewModel.setGoogleError("Google Sign In failed: ${e.statusCode} ${e.message}")
        }
    }

    val backgroundColor = Color(0xFFDDF0E3)
    val primaryGreen = Color(0xFF0D5C3D)
    val lightGreen = Color(0xFFC9EFD4)
    val grayText = Color(0xFF5B5B5B)

    LaunchedEffect(success) {
        if (success) {
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    LaunchedEffect(registerSuccess) {
        if (registerSuccess) {
            isLoginMode = true
            viewModel.resetRegisterSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 26.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(20.dp))

        // LOGO
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier.size(60.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(10.dp))

        // TITLE
        Text(
            text = "Welcome to Swapily",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = primaryGreen,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        // DESCRIPTION
        Text(
            text = "Trade your favorite items with your local\ncommunity, sustainably and securely.",
            fontSize = 13.sp,
            lineHeight = 20.sp,
            color = grayText,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        // CARD
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {

                // LOGIN SIGNUP SWITCH
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(
                            lightGreen,
                            RoundedCornerShape(16.dp)
                        )
                        .padding(4.dp)
                ) {

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(
                                if (isLoginMode) Color.White else Color.Transparent,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { isLoginMode = true },
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = "Login",
                            color = if (isLoginMode) primaryGreen else grayText,
                            fontWeight = if (isLoginMode) FontWeight.Bold else FontWeight.Normal
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(
                                if (!isLoginMode) Color.White else Color.Transparent,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { isLoginMode = false },
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = "Sign Up",
                            color = if (!isLoginMode) primaryGreen else grayText,
                            fontWeight = if (!isLoginMode) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (!isLoginMode) {
                    // FULL NAME
                    Text(
                        text = "Full Name",
                        color = grayText,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = {
                            Text("Your full name")
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = lightGreen,
                            unfocusedContainerColor = lightGreen,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                // EMAIL
                Text(
                    text = "Email",
                    color = grayText,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = {
                        Text("your@email.com")
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Email,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = lightGreen,
                        unfocusedContainerColor = lightGreen,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (isLoginMode) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Text(
                            text = "Password",
                            color = grayText,
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = "Forgot?",
                            color = primaryGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                } else {
                    Text(
                        text = "Password",
                        color = grayText,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // PASSWORD FIELD
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = lightGreen,
                        unfocusedContainerColor = lightGreen,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // ACTION BUTTON
                Button(
                    onClick = {
                        if (isLoginMode) {
                            viewModel.login(email, password)
                        } else {
                            viewModel.register(name, email, password)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryGreen
                    ),
                    enabled = !loading
                ) {
                    if (loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = if (isLoginMode) "Login" else "Sign Up",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (error.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // DIVIDER
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Color.LightGray
                    )

                    Text(
                        text = "  OR CONTINUE WITH  ",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )

                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Color.LightGray
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // SOCIAL BUTTONS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {

                    OutlinedButton(
                        onClick = {
                            googleSignInClient.signOut().addOnCompleteListener {
                                launcher.launch(googleSignInClient.signInIntent)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {

                            Image(
                                painter = painterResource(id = R.drawable.google),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text("Google")
                        }
                    }
                    OutlinedButton(
                        onClick = {
                            LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                                override fun onSuccess(result: LoginResult) {
                                    viewModel.signInWithFacebook(result.accessToken.token)
                                }
                                override fun onCancel() {
                                    // Gérer l'annulation si nécessaire
                                }
                                override fun onError(error: FacebookException) {
                                    viewModel.setGoogleError("Facebook login failed: ${error.message}")
                                }
                            })
                            // On utilise LoginManager.getInstance().logIn(...) qui va déclencher l'intent
                            // Pour Compose, on utilise le launcher pour lancer l'action de LoginManager
                            // Mais LoginManager de Facebook gère son propre lancement d'activité
                            // La méthode standard est de passer l'activité ou le fragment
                            LoginManager.getInstance().logInWithReadPermissions(context as android.app.Activity, listOf("email", "public_profile"))
                        },
                        modifier = Modifier
                            .weight(1.1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {

                            Image(
                                painter = painterResource(id = R.drawable.facebook),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text("Facebook")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // FOOTER TEXT
        Row(
            modifier = Modifier.clickable { isLoginMode = !isLoginMode }
        ) {

            Text(
                text = if (isLoginMode) "Don't have an account? " else "Already have an account? ",
                color = grayText,
                fontSize = 13.sp
            )

            Text(
                text = if (isLoginMode) "Create one" else "Login here",
                color = primaryGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // BOTTOM IMAGES
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Image(
                painter = painterResource(id = R.drawable.img1),
                contentDescription = null,
                modifier = Modifier
                    .weight(1f)
                    .height(90.dp)
                    .clip(RoundedCornerShape(18.dp)),
                contentScale = ContentScale.Crop
            )

            Image(
                painter = painterResource(id = R.drawable.img2),
                contentDescription = null,
                modifier = Modifier
                    .weight(1f)
                    .height(90.dp)
                    .clip(RoundedCornerShape(18.dp)),
                contentScale = ContentScale.Crop
            )

            Image(
                painter = painterResource(id = R.drawable.img3),
                contentDescription = null,
                modifier = Modifier
                    .weight(1f)
                    .height(90.dp)
                    .clip(RoundedCornerShape(18.dp)),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
    }
}