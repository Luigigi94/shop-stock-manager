package com.example.inventarioapp.navigation

sealed class    AppScreens(val route: String) {
    object LoginScreen: AppScreens(route = "LoginScreen")
    object MenuScreen: AppScreens(route = "MenuScreen")
    object AddProductScreen: AppScreens(route = "AddProductScreen")
    object AddCategoryScreen: AppScreens(route = "AddCategoryScreen")
    object ReservesScreen: AppScreens(route = "ReservesScreen")
    object ClientScreen: AppScreens(route = "ClientScreen")
    object PurchaseScreen: AppScreens(route = "PurchaseScreen")
    object InvoiceScreen: AppScreens(route = "InvoiceScreen")
    object PurchaseProductScreen: AppScreens(route = "PurchaseProductScreen")
    object SalesByUserScreen: AppScreens(route = "SalesByUserScreen")
    object InventoryScreen: AppScreens(route = "InventoryScreen")
    object InventoryListScreen: AppScreens(route = "InventoryListScreen")
    object InventoryDetailScreen: AppScreens(route = "InventoryDetailScreen")
    object SupplierScreen: AppScreens(route = "SupplierScreen")
    object SupplierPurchaseScreen: AppScreens(route = "SupplierPurchaseScreen")
    object SupplierItemScreen: AppScreens(route = "SupplierItemScreen")
}