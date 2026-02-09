package com.example.inventarioapp.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCategoryScreen(navController: NavController, text: String?){
//    var nameInput by remember { mutableStateOf() }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "EditCategoryScreen") }
            )
        }
    ) { innerPading ->
        Column(modifier = Modifier.padding(innerPading)) {
            Text(text = "Cuerpo EditCategoryScreen")
            text?.let{
                Text(it)
            }
        }

    }
}