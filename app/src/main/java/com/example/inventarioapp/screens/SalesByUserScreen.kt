package com.example.inventarioapp.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.inventarioapp.ui.LocalSessionViewModel
import com.example.inventarioapp.ui.components.CustomizedListOfSales
import com.example.inventarioapp.ui.components.CustomizedTopAppBar
import com.example.inventarioapp.ui.utils.hideKeyboardOnTap
import com.example.inventarioapp.viewmodel.PurchaseViewModel

@Composable
fun SalesByUserScreen(
    darkThemeState: MutableState<Boolean>,
    navController: NavController,
    purchaseViewModel: PurchaseViewModel = viewModel()
){
    val sessionViewModel = LocalSessionViewModel.current
    val session by sessionViewModel.session.collectAsState()
    val listSales by purchaseViewModel.purchasesByUser.collectAsState()

    LaunchedEffect(session?.userName) {
        session?.userName?.let {
            purchaseViewModel.observePurchasesByUser(it)
        }
    }
    Scaffold(
        topBar = {
            CustomizedTopAppBar(
                title = "Ventas de ${session?.userName}",
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
            CustomizedListOfSales(
                modifier = Modifier,
                list = listSales,
                onItemClick = {}
            )
        }
    }
}