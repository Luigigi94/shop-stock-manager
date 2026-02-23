package com.example.inventarioapp.constants

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PointOfSale
import com.example.inventarioapp.R
import com.example.inventarioapp.model.OptionsMenu

object MenuOptions {
    val options = listOf(
        OptionsMenu(
            label = R.string.menu_label_add_product,
            icon = Icons.Default.AddBox,
            route = "AddProductScreen",
            role = null,
            state = true,
            contentDescription = "Icon Add Product"
        ),
        OptionsMenu(
            label = R.string.menu_label_add_category,
            icon = Icons.Filled.Folder,
            route = "AddCategoryScreen",
            role = null,
            state = true,
            contentDescription = "Icon Add Category"
        ),
        OptionsMenu(
            label = R.string.menu_label_add_order,
            icon = Icons.AutoMirrored.Filled.ReceiptLong,
            route = "ReservesScreen",
            role = null,
            state = true,
            contentDescription = "Icon New Order"
        ),
        OptionsMenu(
            label = R.string.menu_label_add_purchase,
            icon = Icons.Filled.PointOfSale,
            route = "PurchaseScreen",
            role = null,
            state = true,
            contentDescription = "Icon New Purchase"
        ),
        OptionsMenu(
            label = R.string.menu_label_add_client,
            icon = Icons.Filled.PersonAdd,
            route = "ClientScreen",
            role = null,
            state = true,
            contentDescription = "Icon Add Client"
        ),
        OptionsMenu(
            label = R.string.menu_label_sales,
            icon = Icons.Filled.Analytics,
            route = "SalesByUserScreen",
            role = null,
            state = true,
            contentDescription = "Icon Sales by User"
        ),
        OptionsMenu(
            label = R.string.menu_inventory,
            icon = Icons.Filled.Analytics,
            route = "InventoryScreen",
            role = null,
            state = true,
            contentDescription = "Icon Sales by User"
        )
    )
}