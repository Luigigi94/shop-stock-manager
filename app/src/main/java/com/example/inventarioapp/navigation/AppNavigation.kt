package com.example.inventarioapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.inventarioapp.screens.AddCategoryScreen
import com.example.inventarioapp.screens.AddProductScreen
import com.example.inventarioapp.screens.ClientScreen
import com.example.inventarioapp.screens.EditCategoryScreen
import com.example.inventarioapp.screens.EditProductScreen
import com.example.inventarioapp.screens.InvoiceScreen
import com.example.inventarioapp.screens.LoginScreen
import com.example.inventarioapp.screens.MenuScreen
import com.example.inventarioapp.screens.PurchaseProductScreen
import com.example.inventarioapp.screens.PurchaseScreen
import com.example.inventarioapp.screens.ReservesScreen
import com.example.inventarioapp.screens.SalesByUserScreen

@Composable
fun AppNavigation(darkThemeState: MutableState<Boolean>) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = AppScreens.LoginScreen.route
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
        composable(
            route = "${AppScreens.AddProductScreen.route}?productId={productId}",
            arguments = listOf(
                navArgument("productId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            AddProductScreen(
                darkThemeState,
                navController,
                productId = productId
            )
        }
        composable(
            route = "${AppScreens.AddCategoryScreen.route}?categoryId={categoryId}",
            arguments = listOf(
                navArgument("categoryId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) {backStackEntry ->

            val categoryId = backStackEntry.arguments?.getString("categoryId")
            AddCategoryScreen(
                darkThemeState,
                navController,
                categoryId = categoryId
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
        composable(route = AppScreens.ReservesScreen.route) {
            ReservesScreen(
                darkThemeState,
                navController
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
        /*composable(route = AppScreens.PurchaseScreen.route) {
            PurchaseScreen(
                darkThemeState,
                navController
            )
        }
        composable(route = AppScreens.InvoiceScreen.route) {
            InvoiceScreen(
                darkThemeState,
                navController
            )
        }*/
        /*composable(
            route = AppScreens.PurchaseProductScreen.route *//*+ "/{current_purchase}",
            arguments = listOf(navArgument(name = "current_purchase") {
                type = NavType.StringType
            })*//*
        ) {
            PurchaseProductScreen(darkThemeState, navController)
        }*/

        composable(AppScreens.PurchaseScreen.route) {
            PurchaseScreen(darkThemeState, navController)
        }
        composable(
            route = "${AppScreens.PurchaseProductScreen.route}/{purchaseId}?itemId={itemId}",
            arguments = listOf(
                navArgument("purchaseId"){ type = NavType.StringType },
                navArgument("itemId"){
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->

            val purchaseId = backStackEntry.arguments?.getString("purchaseId")
            val itemId = backStackEntry.arguments?.getString("itemId")

            PurchaseProductScreen(
                darkThemeState,
                navController,
                onSave = { navController.popBackStack() },
                purchaseId = purchaseId,
                itemId = itemId
            )
        }
        composable(
            route = "${AppScreens.InvoiceScreen.route}/{purchaseId}",
            arguments = listOf(
                navArgument("purchaseId"){
                    type = NavType.StringType
                }
            )
        ){ backStackEntry ->
            val purchaseId = backStackEntry.arguments?.getString("purchaseId")
            InvoiceScreen(
                darkThemeState = darkThemeState,
                navController = navController,
                purchaseId = purchaseId
            )
        }
        composable(
            route = "${AppScreens.SalesByUserScreen.route}/{userId}",
            arguments = listOf(
                navArgument("userId"){
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: "Admin"
            SalesByUserScreen(
                darkThemeState = darkThemeState,
                navController = navController,
                userId = userId
            )
        }
    }

}

