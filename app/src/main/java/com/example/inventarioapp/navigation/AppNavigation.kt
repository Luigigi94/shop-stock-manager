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
import com.example.inventarioapp.screens.InventoryScreen
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
        composable(AppScreens.SalesByUserScreen.route) {
            SalesByUserScreen(
                darkThemeState,
                navController
            )
        }
        /*composable(
            route = "${AppScreens.InventoryScreen.route}?productId={productId}",
            arguments = listOf(
                navArgument("productId"){
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )

        ){backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            InventoryScreen(
                darkThemeState,
                navController,
                productId
            )
        }*/
        composable(
            route = AppScreens.InventoryScreen.route
        ){
            InventoryScreen(
                darkThemeState,
                navController
            )
        }
    }

}

