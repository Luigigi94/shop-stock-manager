package com.example.inventarioapp.screens

import android.graphics.pdf.models.ListItem
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseScreen(darkThemeState: MutableState<Boolean>, navController: NavController){
    val productViewModel: ProductViewModel = viewModel()
    val clientViewModel: ClientViewModel = viewModel()
    val purchaseViewModel: PurchaseViewModel = viewModel()

    val clients by clientViewModel.clients.collectAsState()
    val products by productViewModel.products.collectAsState()

    var selectedClient by remember { mutableStateOf<Clients?>(null) }
    var selectedProduct by remember { mutableStateOf<Products?>(null) }

    var quantityProduct by remember { mutableStateOf("") }
    val cart by purchaseViewModel.cart.collectAsState()
//    val total by purchaseViewModel.total

    Scaffold(
        topBar = {
            CustomizedTopAppBar(
                title = "PurchaseScreen",
                navController = navController,
                darkThemeState = darkThemeState,
                showBack = true,
                showThemeSwitch = true
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* confirmar compra */ },
                containerColor = MaterialTheme.colorScheme.primary,

            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCartCheckout,
                    contentDescription = "Confirmar Compra"
                )
            }
        },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).hideKeyboardOnTap(),
            verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(text = stringResource(R.string.title_purchase))
            CustomizedFilledCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = {}
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CustomizedExposedDropdownMenu(items = clients, selectedItem = selectedClient, label = "Cliente", itemLabel = { it.nameClient+" "+it.apePClient+" "+it.apeMClient }, onItemSelected = { selectedClient = it }, modifier = Modifier.fillMaxWidth())
                    CustomizedExposedDropdownMenu(items = products, selectedItem = selectedProduct, label = "Producto", itemLabel = { it.nameProduct }, onItemSelected = { selectedProduct = it }, modifier = Modifier.fillMaxWidth())
                    CustomizedOutlinedTextField(modifier = Modifier.fillMaxWidth(), onValueChange = { quantityProduct = it }, value = quantityProduct, label = { Text(text = stringResource(R.string.label_quantity_purchase))})
                }
            }
            CustomizedFilledCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = {}
            ) {
                CustomizedButton(
                    onClick = {
                        val product = selectedProduct ?: return@CustomizedButton
                        val qty = quantityProduct.toIntOrNull() ?: return@CustomizedButton

                        val item = PurchaseItem(product, qty)

                        purchaseViewModel.addItem(item, selectedClient)

                        quantityProduct = ""
                        selectedProduct = null
                    }
                ) {
                    Text(text = "Agregar al carrito")
                }
            }
            ListedPurchaseItems(cart)
        }
    }
}

@Composable
fun ListedPurchaseItems(
    items: List<PurchaseItem>
){
    CustomizedFilledCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = {}
    ) {
        LazyColumn {
            items(items) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column() {
                        Text(text = "${item.product.nameProduct} x ${item.quantity}")
                        Text(text = "${item.subtotal}")
                    }
                }

            }
        }
    }
}