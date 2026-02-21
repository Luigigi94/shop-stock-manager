package com.example.inventarioapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import com.example.inventarioapp.navigation.AppNavigation
import com.example.inventarioapp.ui.LocalSessionViewModel
import com.example.inventarioapp.ui.theme.InventarioAppTheme
import com.example.inventarioapp.viewmodel.SessionViewModel

class MainActivity : ComponentActivity() {
    private val themeState = mutableStateOf(false)

    private val sessionViewModel: SessionViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val darkTheme by themeState
            CompositionLocalProvider(
                LocalSessionViewModel provides sessionViewModel
            ) {
                InventarioAppTheme(darkTheme = darkTheme, dynamicColor = false) {
                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize(),
                        containerColor = MaterialTheme.colorScheme.background
                    ) {
                        AppNavigation(
                            darkThemeState = themeState
                        )
                    }
                }
            }
        }
    }
}