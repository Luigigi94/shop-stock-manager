package com.example.inventarioapp.navigation

sealed class    AppScreens(val route: String) {
    object LoginScreen: AppScreens(route = "LoginScreen")
    object MenuScreen: AppScreens(route = "MenuScreen")
    object AddProductScreen: AppScreens(route = "AddProductScreen")
    object AddCategoryScreen: AppScreens(route = "AddCategoryScreen")
    object ReservesScreen: AppScreens(route = "ReservesScreen")
    object ClientScreen: AppScreens(route = "ClientScreen")
//    object ClientScreen: AppScreens(
//        route = "ClientScreen?clientId={clientId}"
//    )
    object PurchaseScreen: AppScreens(route = "PurchaseScreen")
    object EditCategoryScreen: AppScreens(route = "EditCategoryScreen")
    object EditProductScreen: AppScreens(route = "EditProductScreen")
    object EditClientScreen: AppScreens(route = "EditClientScreen")
    object InvoiceScreen: AppScreens(route = "InvoiceScreen")
    object PurchaseProductScreen: AppScreens(route = "PurchaseProductScreen")
    object SalesByUserScreen: AppScreens(route = "SalesByUserScreen")
}