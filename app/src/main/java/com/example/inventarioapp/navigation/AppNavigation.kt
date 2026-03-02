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
import com.example.inventarioapp.screens.SupplierItemScreen
import com.example.inventarioapp.screens.SupplierPurchaseScreen
import com.example.inventarioapp.screens.SupplierScreen

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
            route = "${AppScreens.AddProductScreen.route}?productId={productId}&openCreateForm={isCreateMode}",
            arguments = listOf(
                navArgument("productId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("isCreateMode") {
                    type = NavType.BoolType
                    nullable = false
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            val isCreateMode = backStackEntry.arguments?.getBoolean("isCreateMode")
            AddProductScreen(
                darkThemeState,
                navController,
                productId = productId,
                isCreateMode = isCreateMode ?: false
            )
        }
        composable(
            route = "${AppScreens.AddCategoryScreen.route}?categoryId={categoryId}&openCreateForm={isCreateMode}",
            arguments = listOf(
                navArgument("categoryId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("isCreateMode") {
                    type = NavType.BoolType
                    nullable = false
                    defaultValue = false
                }
            )
        ) {backStackEntry ->

            val categoryId = backStackEntry.arguments?.getString("categoryId")
            val isCreateMode = backStackEntry.arguments?.getBoolean("isCreateMode")
            AddCategoryScreen(
                darkThemeState,
                navController,
                categoryId = categoryId,
                isCreateMode = isCreateMode ?: false
            )
        }
        composable(
            route = "${AppScreens.ClientScreen.route}?clientId={clientId}&openCreateForm={isCreateMode}",
            arguments = listOf(
                navArgument("clientId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("isCreateMode") {
                    type = NavType.BoolType
                    nullable = false
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val clientId = backStackEntry.arguments?.getString("clientId")
            val isCreateMode = backStackEntry.arguments?.getBoolean("isCreateMode")
            ClientScreen(
                darkThemeState,
                navController,
                clientId = clientId,
                isCreateMode = isCreateMode ?: false
            )
        }
        composable(
            route = "${AppScreens.ReservesScreen.route}?reserveId={reserveId}",
            arguments = listOf(
                navArgument("reserveId"){
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val reserveId = backStackEntry.arguments?.getString("reserveId")
            ReservesScreen(
                darkThemeState,
                navController,
                reserveId = reserveId
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
        composable(
            route = AppScreens.InventoryScreen.route
        ){
            InventoryScreen(
                darkThemeState,
                navController
            )
        }
        composable(
            route = "${AppScreens.SupplierScreen.route}?supplierId={supplierId}",
            arguments = listOf(
                navArgument("supplierId"){
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ){ backStackEntry ->
            val supplierId = backStackEntry.arguments?.getString("supplierId")
            SupplierScreen(
                darkThemeState = darkThemeState,
                navController = navController,
                supplierId = supplierId
            )
        }
        composable(AppScreens.SupplierPurchaseScreen.route) {
            SupplierPurchaseScreen(darkThemeState, navController)
        }
        composable(
            route = "${AppScreens.SupplierItemScreen.route}/{supplierPurchaseId}?itemId={itemId}",
            arguments = listOf(
                navArgument("supplierPurchaseId"){
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("itemId"){
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
            )
        ) {
            backStackEntry ->
            val supplierPurchaseId = backStackEntry.arguments?.getString("supplierPurchaseId")
            val itemId = backStackEntry.arguments?.getString("itemId")
            SupplierItemScreen(
                darkThemeState,
                navController,
                onSave = {navController.popBackStack()},
                supplierPurchaseId = supplierPurchaseId,
                itemId = itemId
            )
        }

        /*composable(AppScreens.PurchaseScreen.route) {
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
        }*/
    }
}

