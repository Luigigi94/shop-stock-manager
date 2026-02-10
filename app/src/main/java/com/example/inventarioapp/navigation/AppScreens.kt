package com.example.inventarioapp.navigation

sealed class AppScreens(val route: String) {
    object LoginScreen: AppScreens(route = "LoginScreen")
    object MenuScreen: AppScreens(route = "MenuScreen")
    object AddProductScreen: AppScreens(route = "AddProductScreen")
    object AddCategoryScreen: AppScreens(route = "AddCategoryScreen")
    object NewOrderScreen: AppScreens(route = "NewOrderScreen")
    object PurchaseScreen: AppScreens(route = "PurchaseScreen")
    object EditCategoryScreen: AppScreens(route = "EditCategoryScreen")
    object EditProductScreen: AppScreens(route = "EditProductScreen")
}