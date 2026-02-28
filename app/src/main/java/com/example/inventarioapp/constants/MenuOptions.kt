package com.example.inventarioapp.constants

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PointOfSale
import com.example.inventarioapp.R
import com.example.inventarioapp.model.OptionsMenu

object MenuOptions {
    val options = listOf(
        OptionsMenu(
            label = R.string.topbar_products,
            icon = Icons.Default.AddBox,
            route = AppNavigationUUIDs.ScreenUUID.PRODUCT,
            role = null,
            state = true,
            contentDescription = "Icon Add Product"
        ),
        OptionsMenu(
            label = R.string.topbar_categories,
            icon = Icons.Filled.Folder,
            route = AppNavigationUUIDs.ScreenUUID.CATEGORY,
            role = null,
            state = true,
            contentDescription = "Icon Add Category"
        ),
        OptionsMenu(
            label = R.string.topbar_reserves,
            icon = Icons.AutoMirrored.Filled.ReceiptLong,
            route = AppNavigationUUIDs.ScreenUUID.RESERVES,
            role = null,
            state = true,
            contentDescription = "Icon New Order"
        ),
        OptionsMenu(
            label = R.string.topbar_purchase,
            icon = Icons.Filled.PointOfSale,
            route = AppNavigationUUIDs.ScreenUUID.PURCHASE,
            role = null,
            state = true,
            contentDescription = "Icon New Purchase"
        ),
        OptionsMenu(
            label = R.string.topbar_clients,
            icon = Icons.Filled.PersonAdd,
            route = AppNavigationUUIDs.ScreenUUID.CLIENT,
            role = null,
            state = true,
            contentDescription = "Icon Add Client"
        ),
        OptionsMenu(
            label = R.string.topbar_sales,
            icon = Icons.Filled.Analytics,
            route = AppNavigationUUIDs.ScreenUUID.SALESBYUSER,
            role = null,
            state = true,
            contentDescription = "Icon Sales by User"
        ),
        OptionsMenu(
            label = R.string.topbar_Inventory,
            icon = Icons.Filled.Inventory,
            route = AppNavigationUUIDs.ScreenUUID.INVENTORY,
            role = null,
            state = true,
            contentDescription = "Icon Sales by User"
        )
    )
}