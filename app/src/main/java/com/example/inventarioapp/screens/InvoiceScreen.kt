package com.example.inventarioapp.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.inventarioapp.R
import com.example.inventarioapp.ui.components.CustomizedTopAppBar
import com.example.inventarioapp.ui.components.CustomizedElevatedCard
import com.example.inventarioapp.viewmodel.PurchaseViewModel

@Composable
fun InvoiceScreen(
    darkThemeState: MutableState<Boolean>,
    navController: NavController
){

    val purchaseViewModel: PurchaseViewModel = viewModel()
    val confirmedPurchase by purchaseViewModel.lastPurchase.collectAsState()

    val clientName = confirmedPurchase?.client?.nameClient
    val clientApP = confirmedPurchase?.client?.apePClient
    val clientApM = confirmedPurchase?.client?.apeMClient
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