package com.example.inventarioapp.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.inventarioapp.model.Purchase
import com.example.inventarioapp.ui.components.CustomizedTopAppBar
import com.example.inventarioapp.ui.utils.hideKeyboardOnTap
import com.example.inventarioapp.viewmodel.PurchaseViewModel

@Composable
fun SalesByUserScreen(
    darkThemeState: MutableState<Boolean>,
    navController: NavController,
    userId: String? = "Admin",
    purchaseViewModel: PurchaseViewModel = viewModel()
){
    val userName = "Administrador"
    val listSales by purchaseViewModel.purchasesByUser.collectAsState()
    Scaffold(
        topBar = {
            CustomizedTopAppBar(
                title = "Ventas de $userName",
                navController = navController,
                darkThemeState = darkThemeState,
                showBack = true,
                showThemeSwitch = true
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .hideKeyboardOnTap()
        ) {
            ListSalesByUser(listSales)
        }
    }
}

@Composable
fun ListSalesByUser(sales: List<Purchase>){
    Column {
        LazyColumn {
            items(sales) {edited ->
                Text(text = edited.items.toString())
            }
        }
    }
}