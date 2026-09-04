package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.OverComerViewModel
import com.example.ui.screens.MainAppScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Firebase Authentication Manager safely and robustly
        try {
            com.example.network.FirebaseAuthManager.initialize(this)
        } catch (t: Throwable) {
            android.util.Log.e("MainActivity", "Failed to initialize Firebase gracefully: ${t.message}. Falling back entirely to offline sandbox mode.")
        }
        
        // Supports full edge-to-edge transparent drawing
        enableEdgeToEdge()
        
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: OverComerViewModel = viewModel()
                    MainAppScreen(viewModel = viewModel)
                }
            }
        }
    }
}
