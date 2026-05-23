package com.swapily.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.swapily.app.ui.Navigation.AppNavigation
import com.swapily.app.ui.theme.SwapilyTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LoginScreen()
        }
    }
}

@Composable
fun SwapilyApp() {

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),

            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Welcome to Swapily",
                fontSize = 28.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = {}) {
                Text("Login")
=======
            SwapilyTheme {
                AppNavigation()
>>>>>>> 554d3f8 (add data login and ui app)
            }
        }
    }
}