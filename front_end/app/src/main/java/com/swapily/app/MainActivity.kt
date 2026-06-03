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
            SwapilyTheme {
                AppNavigation()
            }
        }
    }
}
