package com.example.inventarioapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavDestinations(val route: String, val icon: ImageVector, val label: String) {
    object POS : BottomNavDestinations("pos_tab", Icons.Default.PointOfSale, "Ventas")
    object Catalog : BottomNavDestinations("catalog_tab", Icons.Default.Folder, "Gestión")
    object Stock : BottomNavDestinations("stock_tab", Icons.Default.Inventory, "Inventario")
    object More : BottomNavDestinations("more_tab", Icons.Default.MoreHoriz, "Más")
}