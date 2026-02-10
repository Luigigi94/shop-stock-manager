package com.example.inventarioapp.constants

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.ReceiptLong
import com.example.inventarioapp.model.OptionsMenu

object MenuOptions {
    val options = listOf(
        OptionsMenu(
            label = "Agregar Producto",
            icon = Icons.Default.AddBox,
            route = "AddProductScreen",
            role = null,
            state = true,
            contentDescription = "Icon Add Product"
        ),
        OptionsMenu(
            label = "Nueva Categoria",
            icon = Icons.Filled.Folder,
            route = "AddCategoryScreen",
            role = null,
            state = true,
            contentDescription = "Icon Add Category"
        ),
        OptionsMenu(
            label = "Nueva Orden",
            icon = Icons.AutoMirrored.Filled.ReceiptLong,
            route = "NewOrderScreen",
            role = null,
            state = true,
            contentDescription = "Icon New Order"
        ),
        OptionsMenu(
            label = "Registrar Compra",
            icon = Icons.Filled.PointOfSale,
            route = "PurchaseScreen",
            role = null,
            state = true,
            contentDescription = "Icon New Purchase"
        )
    )
}