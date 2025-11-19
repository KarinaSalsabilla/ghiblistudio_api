package com.example.glibicompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.glibicompose.ui.screens.SearchScreen
import com.example.glibicompose.ui.theme.GlibiComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GlibiComposeTheme {
                var currentScreen by remember { mutableStateOf("splash") }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (currentScreen) {
                        "splash" -> SplashScreen(
                            onNavigateToSearch = {
                                currentScreen = "search"
                            }
                        )
                        "search" -> SearchScreen()
                    }
                }
            }
        }
    }
}