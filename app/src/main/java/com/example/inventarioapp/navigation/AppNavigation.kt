package com.example.inventarioapp.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.inventarioapp.constants.AppNavigationUUIDs
import com.example.inventarioapp.screens.MenuScreen
import com.example.inventarioapp.screens.LoginScreen
import com.example.inventarioapp.screens.AddProductScreen
import com.example.inventarioapp.screens.AddCategoryScreen
import com.example.inventarioapp.screens.ClientScreen
import com.example.inventarioapp.screens.PurchaseScreen
import com.example.inventarioapp.screens.EditCategoryScreen
import com.example.inventarioapp.screens.EditClientScreen
import com.example.inventarioapp.screens.EditProductScreen
import com.example.inventarioapp.screens.InvoiceScreen
import com.example.inventarioapp.screens.PurchaseProductScreen
import com.example.inventarioapp.screens.ReservesScreen

@Composable
fun AppNavigation(darkThemeState: MutableState<Boolean>, modifier: Modifier) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = AppScreens.LoginScreen.route,
        modifier = modifier
    ) {
        composable(route = AppScreens.LoginScreen.route) {
            LoginScreen(
                darkThemeState,
                navController
            )
        }
        composable(route = AppScreens.MenuScreen.route) {
            MenuScreen(
                darkThemeState,
                navController
            )
        }
        composable(route = AppScreens.AddProductScreen.route) {
            AddProductScreen(
                darkThemeState,
                navController
            )
        }
        composable(route = AppScreens.AddCategoryScreen.route) {
            AddCategoryScreen(
                darkThemeState,
                navController
            )
        }
        composable(route = AppScreens.ReservesScreen.route) {
            ReservesScreen(
                darkThemeState,
                navController
            )
        }
        composable(route = AppScreens.PurchaseScreen.route) {
            PurchaseScreen(
                darkThemeState,
                navController
            )
        }
        composable(
            route = "${AppScreens.ClientScreen.route}?clientId={clientId}",
            arguments = listOf(
                navArgument("clientId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val clientId = backStackEntry.arguments?.getString("clientId")
            ClientScreen(
                darkThemeState,
                navController,
                clientId = clientId
            )
        }

        composable(
            route = AppScreens.EditCategoryScreen.route + "/{categoryUUID}",
            arguments = listOf(navArgument(name = "categoryUUID") {
                type = NavType.StringType
            })
        ) {
            EditCategoryScreen(
                darkThemeState,
                navController,
                it.arguments?.getString("categoryUUID")
            )
        }
        composable(
            route = AppScreens.EditProductScreen.route + "/{productUUID}",
            arguments = listOf(navArgument(name = "productUUID") {
                type = NavType.StringType
            })
        ) {
            EditProductScreen(
                darkThemeState,
                navController,
                productId = it.arguments?.getString("productUUID")
            )
        }
        /*composable(
            route = AppScreens.EditClientScreen.route+"/{clientUUID}",
            arguments = listOf(navArgument(name = "clientUUID"){
                type = NavType.StringType
            })
        ){
            EditClientScreen(darkThemeState, navController, clientId = it.arguments?.getString("clientUUID"))
        }*/
        composable(route = AppScreens.InvoiceScreen.route) {
            InvoiceScreen(
                darkThemeState,
                navController
            )
        }
        composable(
            route = AppScreens.PurchaseProductScreen.route + "/{current_purchase}",
            arguments = listOf(navArgument(name = "current_purchase") {
                type = NavType.StringType
            })
        ) {
            PurchaseProductScreen(darkThemeState, navController)
        }
    }

}