package com.titanflaws.erp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.titanflaws.erp.presentation.navigation.AppNavigation
import com.titanflaws.erp.presentation.navigation.AuthRoutes
import com.titanflaws.erp.ui.theme.ERPTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Check if we have a target screen from a notification
        val targetScreen = intent.getStringExtra("target_screen")
        
        setContent {
            ERPTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Initialize the app navigation
                    AppNavigation(
                        startDestination = AuthRoutes.AUTH_GRAPH // Use auth graph as starting point
                    )
                }
            }
        }
    }
}