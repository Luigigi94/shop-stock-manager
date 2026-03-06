package com.example.inventarioapp.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.inventarioapp.R
import com.example.inventarioapp.model.InventoryDetail
import com.example.inventarioapp.ui.components.CustomizedListOfEditables
import com.example.inventarioapp.ui.components.CustomizedQuickAddModal
import com.example.inventarioapp.ui.components.CustomizedTopAppBar
import com.example.inventarioapp.viewmodel.InventoryViewModel

@Composable
fun InventoryDetailScreen(
    /* darkThemeState: MutableState<Boolean>, */
    navController: NavController,
    inventoryId: String?,
    inventoryViewModel: InventoryViewModel = viewModel()
){
    LaunchedEffect(inventoryId) {
        inventoryViewModel.getListedInventoriedItems(inventoryId ?: "")
    }

    val stateHistory by inventoryViewModel.uiStateHistory.collectAsState()
    var showModal by remember { mutableStateOf(false) }

    var selectedProduct by remember { mutableStateOf<InventoryDetail?>(null) }


    Scaffold(
        topBar = {
            CustomizedTopAppBar(
                title = stringResource(R.string.topbar_Inventory_details),
                /* darkThemeState = darkThemeState, */
                navController = navController,
                showThemeSwitch = true,
                showBack = true
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.inventory_details_label_products),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            item {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CustomizedListOfEditables(
                            list = stateHistory.products,
                            label = { it.productName },
                            description = { it.countedQuantity.toString() },
                            onItemClick = {
                                selectedProduct = it
                                showModal = true
                            }
                        )
                        CustomizedQuickAddModal(
                            show = showModal,
                            onDismiss = { showModal = false },
                            onConfirm = { showModal = false },
                            title = "Detalles del producto"
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(value = selectedProduct?.productName ?: "", onValueChange = { selectedProduct?.productName ?: "" }, label = { Text(text = "Nombre") }, readOnly = true)
                                OutlinedTextField(value = selectedProduct?.systemQuantity.toString() ?: "", onValueChange = { selectedProduct?.systemQuantity.toString() ?: "" }, label = { Text(text = "Conteo") }, readOnly = true)
                                OutlinedTextField(value = selectedProduct?.countedQuantity.toString() ?: "", onValueChange = { selectedProduct?.countedQuantity.toString() ?: "" }, label = { Text(text = "En Sistema") }, readOnly = true)
                                OutlinedTextField(value = selectedProduct?.difference.toString() ?: "", onValueChange = { selectedProduct?.difference.toString() ?: "" }, label = { Text(text = "Diferencia") }, readOnly = true)
                            }
                        }
                    }
                }
            }
        }
    }
}

/*
* countedQuantity 50 (number)

difference 30 (number)

id "inv_1772691406654_73464d0d-0169-4bf5-a1da-57436a68f700" (string)

productId "73464d0d-0169-4bf5-a1da-57436a68f700" (string)

productName "prod b" (string)

referenceId "inv_1772691406654" (string)

systemQuantity 20 (number)

timestamp 5 de marzo de 2026 a las 12:17:05 a.m. UTC-6 *
* */