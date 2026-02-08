package com.example.inventarioapp.constants

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import com.example.inventarioapp.model.OptionsMenu

object MenuOptions {
    val options = listOf(
        OptionsMenu(
            label = "Agregar Producto",
            icon = Icons.Default.Add,
            route = "AddProductScreen",
            role = null,
            state = true
        ),
        OptionsMenu(
            label = "Nueva Categoria",
            icon = null,
            route = "AddCategoryScreen",
            role = null,
            state = true
        ),
        OptionsMenu(
            label = "Nueva Orden",
            icon = null,
            route = "NewOrderScreen",
            role = null,
            state = true
        ),
        OptionsMenu(
            label = "Registrar Compra",
            icon = null,
            route = "PurchaseScreen",
            role = null,
            state = true
        )
    )
}