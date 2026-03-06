package com.example.inventarioapp.screens.menu

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.inventarioapp.constants.MenuOptions
import com.example.inventarioapp.ui.components.GenericListedOptions

@Composable
fun POSScreen(navController: NavController) {
    GenericListedOptions(
        navController = navController,
        options = MenuOptions.POS_OPTIONS, // Pasas la lista de ventas
        title = "Operaciones de Caja"
    )
}