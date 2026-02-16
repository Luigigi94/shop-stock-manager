package com.example.inventarioapp.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.inventarioapp.R
import com.example.inventarioapp.navigation.AppScreens
import com.example.inventarioapp.ui.components.CustomizedButton
import com.example.inventarioapp.ui.components.CustomizedTopAppBar
import com.example.inventarioapp.ui.components.CustomizedElevatedCard
import com.example.inventarioapp.ui.components.CustomizedFilledCard
import com.example.inventarioapp.viewmodel.PurchaseViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun InvoiceScreen(
    darkThemeState: MutableState<Boolean>,
    navController: NavController
) {
    val parentEntry = remember(navController) {
        navController.getBackStackEntry(AppScreens.PurchaseScreen.route)
    }

    val purchaseViewModel: PurchaseViewModel = viewModel(parentEntry)

    val purchase by purchaseViewModel.lastPurchase.collectAsState()

    val client = purchase?.client
    val itemsPurch = purchase?.items ?: emptyList()
    val total = purchase?.total ?: 0.0
    val purchaseDate = purchase?.purchaseTimeStamp?.toDate()?.let {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it)
    }

    val clientName = client?.nameClient ?: ""
    val clientApP = client?.apePClient ?: ""
    val clientApM = client?.apeMClient ?: ""

    LaunchedEffect(purchase) {
        Log.d("InvoiceScreen", "PURCHASE -> $purchase")
        Log.d("InvoiceScreen", "CLIENT -> ${purchase?.client}")
    }
    Scaffold(
        topBar = {
            CustomizedTopAppBar(
                title = stringResource(R.string.title_invoice),
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
                    }
                ) {
                    Text(text = stringResource(R.string.label_end_purchase_invoice))
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
                            text = stringResource(R.string.label_client_invoice),
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(text = "$clientName $clientApP $clientApM")
                    }
                    Column {
                        Text(text = stringResource(R.string.label_timestamp_invoice), style = MaterialTheme.typography.labelMedium)
                        Text(text = "$purchaseDate")
                    }
                }
                Divider()

                Text(stringResource(R.string.label_products_invoice), style = MaterialTheme.typography.titleMedium)

                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("Product", modifier = Modifier.weight(2f))
                    Text("Price", modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                    Text("Qty", modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                    Text("Subtotal", modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                }
                Divider()
                LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                    items(itemsPurch) { product ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                product.product.nameProduct,
                                modifier = Modifier.weight(2f)
                            )
                            Text(
                                "$${product.product.priceProduct}",
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.End
                            )
                            Text(
                                text = "${product.product.quantityProduct}",
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.End
                            )
                            Text(
                                "$${product.subtotal}",
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }
                Divider(thickness = 2.dp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "$${total}",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
//            }
        }
    }
}

