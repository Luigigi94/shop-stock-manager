package com.example.inventarioapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.inventarioapp.screens.MenuScreen
import com.example.inventarioapp.screens.LoginScreen
import com.example.inventarioapp.screens.AddProductScreen
import com.example.inventarioapp.screens.AddCategoryScreen
import com.example.inventarioapp.screens.ClientScreen
import com.example.inventarioapp.screens.PurchaseScreen
import com.example.inventarioapp.screens.EditCategoryScreen
import com.example.inventarioapp.screens.EditProductScreen
import com.example.inventarioapp.screens.ReservesScreen

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
        composable(route = AppScreens.ReservesScreen.route){
            ReservesScreen(navController)
        }
        composable(route = AppScreens.PurchaseScreen.route){
            PurchaseScreen(navController)
        }
        composable(route = AppScreens.ClientScreen.route){
            ClientScreen(navController)
        }
        composable(
            route = AppScreens.EditCategoryScreen.route+"/{categoryUUID}",
            arguments = listOf(navArgument(name = "categoryUUID"){
                type = NavType.StringType
            })
        ){
            EditCategoryScreen(navController, it.arguments?.getString("categoryUUID"))
        }
        composable(
            route = AppScreens.EditProductScreen.route+"/{productUUID}",
            arguments = listOf(navArgument(name = "productUUID"){
                type = NavType.StringType
            })
        ){
            EditProductScreen(navController, productId = it.arguments?.getString("productUUID"))
        }
    }

}