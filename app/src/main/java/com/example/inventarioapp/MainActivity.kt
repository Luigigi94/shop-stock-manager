package com.example.inventarioapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import com.example.inventarioapp.navigation.AppNavigation
import com.example.inventarioapp.ui.theme.InventarioAppTheme

class MainActivity : ComponentActivity() {
    private val themeState = mutableStateOf(false)

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val darkTheme by themeState
            InventarioAppTheme(darkTheme = darkTheme, dynamicColor = false) {
                Scaffold(
                    modifier = Modifier
                    .fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    AppNavigation(Modifier.padding(innerPadding), darkThemeState = themeState)
                }
            }
        }
    }
}