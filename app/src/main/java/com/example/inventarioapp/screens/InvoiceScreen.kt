package com.example.inventarioapp.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.inventarioapp.R
import com.example.inventarioapp.navigation.AppScreens
import com.example.inventarioapp.ui.components.CustomizedTopAppBar
import com.example.inventarioapp.ui.components.CustomizedElevatedCard
import com.example.inventarioapp.viewmodel.PurchaseViewModel

@Composable
fun InvoiceScreen(
    darkThemeState: MutableState<Boolean>,
    navController: NavController
){
    val parentEntry = remember(navController) {
        navController.getBackStackEntry(AppScreens.PurchaseScreen.route)
    }

    val purchaseViewModel: PurchaseViewModel = viewModel(parentEntry)

    val purchase by purchaseViewModel.lastPurchase.collectAsState()

    val client = purchase?.client
    val items = purchase?.items ?: emptyList()
    val total = purchase?.total ?: 0.0

    val clientName = client?.nameClient
    val clientApP = client?.apePClient
    val clientApM = client?.apeMClient

    LaunchedEffect(purchase) {
        Log.d("InvoiceScreen", "PURCHASE -> $purchase")
        Log.d("InvoiceScreen", "CLIENT -> ${purchase?.client}")
    }
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
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                CustomizedElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = true,
                    elevation = CardDefaults.elevatedCardElevation(16.dp),
                    colors = CardDefaults.elevatedCardColors(MaterialTheme.colorScheme.primary)
                ){
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.label_client_purchase))
                        Text("$clientName $clientApP $clientApM")
                    }
                }
            }
        }
    }
}