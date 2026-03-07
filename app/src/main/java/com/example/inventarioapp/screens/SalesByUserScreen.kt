package com.example.inventarioapp.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.inventarioapp.R
import com.example.inventarioapp.ui.LocalSessionViewModel
import com.example.inventarioapp.ui.components.CustomizedElevatedCard
import com.example.inventarioapp.ui.components.CustomizedListOfSales
import com.example.inventarioapp.ui.components.CustomizedReviewModal
import com.example.inventarioapp.ui.components.CustomizedTopAppBar
import com.example.inventarioapp.ui.utils.hideKeyboardOnTap
import com.example.inventarioapp.viewmodel.PurchaseViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun SalesByUserScreen(
    /* darkThemeState: MutableState<Boolean>, */
    navController: NavController,
    purchaseViewModel: PurchaseViewModel = viewModel()
) {
    val sessionViewModel = LocalSessionViewModel.current
    val session by sessionViewModel.session.collectAsState()
    val listSales by purchaseViewModel.purchasesByUser.collectAsState()
    val purchaseProduct by purchaseViewModel.selectedPurchase.collectAsState()
    var showDetailModal by remember { mutableStateOf(false) }
    var salesDate by remember { mutableStateOf("") }

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
                /* darkThemeState = darkThemeState, */
                showBack = true,
                showThemeSwitch = true
            )
        }
    ) { innerPadding ->
        CustomizedReviewModal(
            show = showDetailModal,
            onDismiss = { showDetailModal = false },
            onConfirm =  { showDetailModal = false },
            title = "Venta del $salesDate",
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        stringResource(R.string.invoice_label_products),
                        modifier = Modifier.weight(2f)
                    )
                    Text(
                        stringResource(R.string.invoice_label_price),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.End
                    )
                    Text(
                        stringResource(R.string.invoice_label_qty),
                        modifier = Modifier.weight(0.8f),
                        textAlign = TextAlign.End
                    )
                    Text(
                        stringResource(R.string.invoice_label_subtotal),
                        modifier = Modifier.weight(1.2f),
                        textAlign = TextAlign.End
                    )
                }
                HorizontalDivider()
                purchaseProduct?.items?.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = item.productName,
                            modifier = Modifier.weight(2f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "$${item.price}",
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "${item.quantity}",
                            modifier = Modifier.weight(0.8f),
                            textAlign = TextAlign.End,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "$${item.subtotal}",
                            modifier = Modifier.weight(1.2f),
                            textAlign = TextAlign.End,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                HorizontalDivider(thickness = 2.dp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "$${purchaseProduct?.total}",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                CustomizedListOfSales(
                    modifier = Modifier,
                    list = listSales,
                    onItemClick = { purchase ->
                        Log.d("Purchase -> SalesScreen", "Valor de purchase.id: ${purchase.id}")
                        val dateFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                        val formattedDate = purchase.createdAt?.toDate()?.let { dateFormatter.format(it) } ?: ""
                        salesDate = formattedDate
                        purchaseViewModel.getListedProductsBySale(purchase.id)
                        showDetailModal = true
                    }
                )
            }
        }
    }
}