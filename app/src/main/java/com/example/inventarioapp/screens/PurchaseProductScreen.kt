package com.example.inventarioapp.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.inventarioapp.R
import com.example.inventarioapp.model.Clients
import com.example.inventarioapp.model.Products
import com.example.inventarioapp.model.PurchaseItem
import com.example.inventarioapp.ui.components.CustomizedButton
import com.example.inventarioapp.ui.components.CustomizedExposedDropdownMenu
import com.example.inventarioapp.ui.components.CustomizedFilledCard
import com.example.inventarioapp.ui.components.CustomizedOutlinedTextField
import com.example.inventarioapp.ui.components.CustomizedTopAppBar
import com.example.inventarioapp.ui.utils.hideKeyboardOnTap
import com.example.inventarioapp.viewmodel.ClientViewModel
import com.example.inventarioapp.viewmodel.ProductViewModel
import com.example.inventarioapp.viewmodel.PurchaseViewModel

@Composable
fun PurchaseProductScreen(
    darkTheme: MutableState<Boolean>,
    navController: NavController,
    purchaseViewModel: PurchaseViewModel = viewModel()
){
    val productViewModel: ProductViewModel = viewModel()
    val clientViewModel: ClientViewModel = viewModel()

    val clients by clientViewModel.clients.collectAsState()
    val products by productViewModel.products.collectAsState()

/*    var selectedClient by remember { mutableStateOf<Clients?>(null) }
    var selectedProduct by remember { mutableStateOf<Products?>(null) }

    var quantityProduct by remember { mutableStateOf("") }*/
    val snackbarHostState = remember { SnackbarHostState() }
    val statePurchaseItem by purchaseViewModel.uiState.collectAsState()

    val selectedClient = clients.firstOrNull{
        it.idClient == statePurchaseItem.idClient
    }
    val selectedProduct = products.firstOrNull{
        it.idProduct == statePurchaseItem.idProduct
    }


    LaunchedEffect(purchaseId) {
        if (purchaseId == null){
            purchaseViewModel.startCreate()
        } else {
            purchaseViewModel.loadPurchase(purchaseId)
        }
    }

    if (statePurchaseItem.isLoading){
        CircularProgressIndicator()
        return
    }

    if (statePurchaseItem.success) {
        val text = stringResource(R.string.menu_label_add_purchase)
        LaunchedEffect(Unit) {
            snackbarHostState.showSnackbar(text)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CustomizedTopAppBar(
                title = stringResource(R.string.title_purchase_product),
                navController = navController,
                darkThemeState = darkTheme,
                showBack = true,
                showThemeSwitch = true
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Column(modifier = Modifier.padding(innerPadding).hideKeyboardOnTap(),
                verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (statePurchaseItem.isEdit) {
                    Text(text = stringResource(R.string.title_edit_purchase))
                } else {
                    Text(text = stringResource(R.string.title_purchase))
                }
                CustomizedFilledCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {}
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CustomizedExposedDropdownMenu(
                            items = clients,
                            selectedItem = selectedClient,
                            label = "Cliente",
                            itemLabel = { it.nameClient+" "+it.apePClient+" "+it.apeMClient },
                            onItemSelected = { purchaseViewModel.onIdClient(it.idClient) },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        CustomizedExposedDropdownMenu(
                            items = products,
                            selectedItem = selectedProduct,
                            label = "Producto",
                            itemLabel = { it.nameProduct },
                            onItemSelected = { purchaseViewModel.onIdProduct(it.idProduct) },
                            modifier = Modifier.fillMaxWidth(),
                            isError = statePurchaseItem.idProductError != null,
                            supportingText = statePurchaseItem.idProductError
                        )
                        CustomizedOutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            onValueChange = purchaseViewModel::onQuantity,
                            value = statePurchaseItem.quantity,
                            label = { Text(text = stringResource(R.string.label_quantity_purchase)) },
                            onFocusLost = purchaseViewModel::onQuantityBlur
                        )
                    }
                }
                CustomizedFilledCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {}
                ) {
                    CustomizedButton(
                        onClick = {
                            val product = selectedProduct ?: return@CustomizedButton
                            val qty = statePurchaseItem.quantity.toIntOrNull() ?: return@CustomizedButton

                            val item = PurchaseItem(product, qty)

                            purchaseViewModel.addItem(item, selectedClient)

                            quantityProduct = ""
                            selectedProduct = null
                            navController.popBackStack()
                        }
                    ) {
                        Text(text = "Agregar al carrito")
                    }
                }
            }
        }
    }
}