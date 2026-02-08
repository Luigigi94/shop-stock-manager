package com.example.inventarioapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.inventarioapp.screens.MenuScreen
import com.example.inventarioapp.screens.LoginScreen
import com.example.inventarioapp.screens.AddProductScreen
import com.example.inventarioapp.screens.AddCategoryScreen
import com.example.inventarioapp.screens.NewOrderScreen
import com.example.inventarioapp.screens.PurchaseScreen

@Composable
fun AppNavigation(padding: Modifier) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppScreens.LoginScreen.route){
        composable(route = AppScreens.LoginScreen.route){
            LoginScreen(navController)
        }
        composable(route = AppScreens.MenuScreen.route){
            MenuScreen(navController)
        }
        composable(route = AppScreens.AddProductScreen.route){
            AddProductScreen(navController)
        }
        composable(route = AppScreens.AddCategoryScreen.route){
            AddCategoryScreen(navController)
        }
        composable(route = AppScreens.NewOrderScreen.route){
            NewOrderScreen(navController)
        }
        composable(route = AppScreens.PurchaseScreen.route){
            PurchaseScreen(navController)
        }
    }

}