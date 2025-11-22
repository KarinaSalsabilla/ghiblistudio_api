package com.example.glibicompose

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.example.glibicompose.ui.screens.DashboardScreen
import com.example.glibicompose.ui.theme.GlibiComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GlibiComposeTheme {
                var currentScreen by rememberSaveable { mutableStateOf("splash") }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (currentScreen) {
                        "splash" -> SplashScreen(
                            onNavigateToSearch = {
                                currentScreen = "dashboard"
                            }
                        )
                        "dashboard" -> DashboardScreen()
                    }
                }
            }
        }
    }

    // Handle configuration changes tanpa restart
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Activity tidak akan di-recreate saat theme berubah
    }
}