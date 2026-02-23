package com.example.inventarioapp.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.inventarioapp.R
import com.example.inventarioapp.ui.components.CustomizedTopAppBar

@Composable
fun InventoryScreen(darkThemeState: MutableState<Boolean>, navController: NavController){
    Scaffold(
        topBar = {
            CustomizedTopAppBar(
                title = stringResource(R.string.menu_inventory),
                darkThemeState = darkThemeState,
                navController = navController,
                showThemeSwitch = true,
                showBack = true
            )
        }
    ) {innerPadding ->
        Row(
            modifier = Modifier.padding(innerPadding)
        ) {
            Text(text = "inventory")
        }
    }
}