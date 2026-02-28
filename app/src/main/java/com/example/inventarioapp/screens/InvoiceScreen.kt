package com.example.inventarioapp.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.inventarioapp.R
import com.example.inventarioapp.navigation.AppScreens
import com.example.inventarioapp.ui.components.CustomizedButton
import com.example.inventarioapp.ui.components.CustomizedElevatedCard
import com.example.inventarioapp.ui.components.CustomizedTopAppBar
import com.example.inventarioapp.viewmodel.PurchaseViewModel

@Composable
fun InvoiceScreen(
    darkThemeState: MutableState<Boolean>,
    navController: NavController,
    viewModel: PurchaseViewModel = viewModel(),
    purchaseId: String?
) {
    val purchase by viewModel.purchase.collectAsState()

    LaunchedEffect(purchaseId) {
        purchaseId?.let { viewModel.observePurchase(it) }
    }
    Scaffold(
        topBar = {
            CustomizedTopAppBar(
                title = stringResource(R.string.topbar_Invoice),
                darkThemeState = darkThemeState,
                showThemeSwitch = true,
                showBack = false
            )
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.fillMaxWidth()
            ) {
                CustomizedButton(
                    onClick = {
                        navController.navigate(route = AppScreens.MenuScreen.route)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.invoice_label_end_purchase))
                }
            }
        }
    ) { innerPadding ->
        CustomizedElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(16.dp),
            elevation = CardDefaults.elevatedCardElevation(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.invoice_label_client),
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(text = "${ purchase?.clientName } ")
                    }
                    Column {
                        Text(text = stringResource(R.string.invoice_label_timestamp), style = MaterialTheme.typography.labelMedium)
                        Text(text = "purchaseDate")
                    }
                }
                HorizontalDivider()

                Text(stringResource(R.string.invoice_label_products), style = MaterialTheme.typography.titleMedium)

                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("Product", modifier = Modifier.weight(2f))
                    Text("Price", modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                    Text("Qty", modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                    Text("Subtotal", modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                }
                HorizontalDivider()
                purchase?.items?.forEach {
                    Text("${it.productName} x${it.quantity} = $${it.subtotal}")
                }
                HorizontalDivider(thickness = 2.dp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "$${purchase?.total}",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }
    }
}

