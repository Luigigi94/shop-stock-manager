package com.example.inventarioapp.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.inventarioapp.ui.components.CustomizedTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservesScreen(darkThemeState: MutableState<Boolean>, navController: NavController){
    Scaffold(
        topBar = {
            CustomizedTopAppBar(
                title = "ReservesScreen",
                navController = navController,
                darkThemeState = darkThemeState,
                showBack = true,
                showThemeSwitch = true
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Text(text = "Cuerpo ReservesScreen")
        }

    }
}