package com.example.inventarioapp.screens.menu

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.inventarioapp.constants.MenuOptions
import com.example.inventarioapp.ui.components.GenericListedOptions

@Composable
fun GestionScreen(navController: NavController) {
    GenericListedOptions(
        navController = navController,
        options = MenuOptions.CATALOG_GROUP, // Pasas la lista de gestión
        title = "Administración de Catálogo"
    )
}