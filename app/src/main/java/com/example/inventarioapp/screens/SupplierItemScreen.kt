package com.example.inventarioapp.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.navigation.NavController

@Composable
fun SupplierItemScreen(
    darkThemeState: MutableState<Boolean>,
    navController: NavController,
    onSave: () -> Unit = { navController.popBackStack() },
    supplierPurchaseId: String?,
    itemId: String?
){

}