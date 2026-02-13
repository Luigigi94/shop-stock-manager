package com.example.inventarioapp.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.inventarioapp.ui.components.CustomizedListedPurchaseItems
import com.example.inventarioapp.ui.components.CustomizedTopAppBar
import com.example.inventarioapp.viewmodel.PurchaseViewModel

@Composable
fun InvoiceScreen(
    darkThemeState: MutableState<Boolean>,
    navController: NavController,
    current_purchase: String?
){

    val purchaseViewModel: PurchaseViewModel = viewModel()
    val cart by purchaseViewModel.cart.collectAsState()
    Scaffold(
        topBar = {
            CustomizedTopAppBar(
                title = "InvoiceScreen",
                darkThemeState = darkThemeState,
                showThemeSwitch = true,
                showBack = true
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxWidth()) {
            Text(text = "Invoice")
            CustomizedListedPurchaseItems(cart, false)
        }
    }
}