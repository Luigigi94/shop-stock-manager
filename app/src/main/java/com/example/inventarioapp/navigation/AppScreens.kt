package com.example.inventarioapp.navigation

sealed class    AppScreens(val route: String) {
    object LoginScreen: AppScreens(route = "LoginScreen")
    object MenuScreen: AppScreens(route = "MenuScreen")
    object AddProductScreen: AppScreens(route = "AddProductScreen")
    object AddCategoryScreen: AppScreens(route = "AddCategoryScreen")
    object ReservesScreen: AppScreens(route = "ReservesScreen")
    object ClientScreen: AppScreens(route = "ClientScreen")
    object PurchaseScreen: AppScreens(route = "PurchaseScreen")
    object EditCategoryScreen: AppScreens(route = "EditCategoryScreen")
    object InvoiceScreen: AppScreens(route = "InvoiceScreen")
    object PurchaseProductScreen: AppScreens(route = "PurchaseProductScreen")
    object SalesByUserScreen: AppScreens(route = "SalesByUserScreen")
    object InventoryScreen: AppScreens(route = "InventoryScreen")
    object SupplierScreen: AppScreens(route = "SupplierScreen")
}