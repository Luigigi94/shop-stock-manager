package com.example.inventarioapp.screens.menu

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.inventarioapp.constants.MenuOptions
import com.example.inventarioapp.ui.components.GenericListedOptions

@Composable
fun StockScreen(navController: NavController){
    GenericListedOptions(
        navController = navController,
        options = MenuOptions.STOCK_GROUP, // Pasas la lista de gestión
        title = "Administración de Catálogo"
    )
}