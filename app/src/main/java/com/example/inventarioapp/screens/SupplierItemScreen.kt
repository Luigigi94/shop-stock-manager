package com.example.inventarioapp.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.inventarioapp.R
import com.example.inventarioapp.model.Products
import com.example.inventarioapp.ui.LocalSessionViewModel
import com.example.inventarioapp.ui.components.CustomizedEditRows
import com.example.inventarioapp.ui.components.CustomizedExposedDropdownMenu
import com.example.inventarioapp.ui.components.CustomizedOutlinedTextField
import com.example.inventarioapp.ui.components.CustomizedTitleScreens
import com.example.inventarioapp.ui.components.CustomizedTopAppBar
import com.example.inventarioapp.viewmodel.ProductViewModel
import com.example.inventarioapp.viewmodel.SupplierPurchaseViewModel

@Composable
fun SupplierItemScreen(
    darkThemeState: MutableState<Boolean>,
    navController: NavController,
    onSave: () -> Unit = { navController.popBackStack() },
    supplierPurchaseId: String?,
    itemId: String?,
    viewModel: SupplierPurchaseViewModel = viewModel()
) {
    val productViewModel: ProductViewModel = viewModel()
    val products by productViewModel.products.collectAsState()

    val sessionViewModel = LocalSessionViewModel.current
    val session by sessionViewModel.session.collectAsState()

    var selectedProduct by remember { mutableStateOf<Products?>(null) }

    var quantityProduct by remember { mutableStateOf("1") }
    val snackbarHostState = remember { SnackbarHostState() }
    val itemsSupplier by viewModel.currentPurchase.collectAsState()


    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
//        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CustomizedTopAppBar(
                title = stringResource(R.string.topbar_purchase_product),
                navController = navController,
                darkThemeState = darkThemeState,
                showBack = true,
                showThemeSwitch = true
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.popBackStack() },
                containerColor = MaterialTheme.colorScheme.errorContainer,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null
                )
            }
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
                CustomizedTitleScreens(stringResource(R.string.purchase_product_label_add))
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
                        Text(
                            text = stringResource(R.string.products_label_new_product),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        CustomizedExposedDropdownMenu(
                            items = products,
                            selectedItem = selectedProduct,
                            label = stringResource(R.string.supplier_item_product_label_product),
                            itemLabel = { it.nameProduct },
                            onItemSelected = { selectedProduct = it },
                            modifier = Modifier.fillMaxWidth(),
                            /*isError = statePurchaseItem.idProductError != null,
                        supportingText = statePurchaseItem.idProductError*/
                        )
                        CustomizedOutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            onValueChange = { quantityProduct = it },
//                            onValueChange = purchaseViewModel::onQuantity,
                            value = quantityProduct,
                            label = { Text(text = stringResource(R.string.supplier_item_product_label_quantity)) },
//                            onFocusLost = purchaseViewModel::onQuantityBlur
                        )
                        CustomizedOutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            onValueChange = { quantityProduct = it },
//                            onValueChange = purchaseViewModel::onQuantity,
                            value = quantityProduct,
                            label = { Text(text = stringResource(R.string.supplier_item_product_label_cost)) },
//                            onFocusLost = purchaseViewModel::onQuantityBlur
                        )

                        CustomizedEditRows(
                            onCancel = {},
                            onDelete = {},
                            onAction = {
                                selectedProduct?.let {
                                    val qty = quantityProduct.toInt()
                                    Log.d("selectedProduct","Revisando el itemId: $itemId")
                                    if (itemId == null){
//                                        purchaseViewModel.addOrUpdateItem(it, qty)
                                    } else {
//                                        purchaseViewModel.updateItemQuantity(itemId, qty)
                                    }
                                    onSave()
                                }
                            },
                            isEdit = false,
                            label = stringResource(R.string.supplier_item_product_label_add)
                        )
                    }
                }
            }
        }
    }
}
