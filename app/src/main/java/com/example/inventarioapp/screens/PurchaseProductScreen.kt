package com.example.inventarioapp.screens

//import com.example.inventarioapp.model.PurchaseItem
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.example.inventarioapp.model.Products
import com.example.inventarioapp.ui.components.CustomizedButton
import com.example.inventarioapp.ui.components.CustomizedExposedDropdownMenu
import com.example.inventarioapp.ui.components.CustomizedFilledCard
import com.example.inventarioapp.ui.components.CustomizedOutlinedTextField
import com.example.inventarioapp.ui.components.CustomizedTopAppBar
import com.example.inventarioapp.ui.utils.hideKeyboardOnTap
import com.example.inventarioapp.viewmodel.ProductViewModel
import com.example.inventarioapp.viewmodel.PurchaseViewModel

@Composable
fun PurchaseProductScreen(
    darkTheme: MutableState<Boolean>,
    navController: NavController,
    purchaseViewModel: PurchaseViewModel = viewModel(),
    onSave: () -> Unit = { navController.popBackStack() },
    purchaseId: String?,
    itemId: String?
){
    val productViewModel: ProductViewModel = viewModel()
    val products by productViewModel.products.collectAsState()


    var selectedProduct by remember { mutableStateOf<Products?>(null) }

    var quantityProduct by remember { mutableStateOf("1") }
    val snackbarHostState = remember { SnackbarHostState() }
    val statePurchaseItem by purchaseViewModel.cart.collectAsState()


    LaunchedEffect(itemId, statePurchaseItem, products) {
        Log.d("LaunchedEffect", "revisando que no vengan params null\n itemId: $itemId\n statePurchaseItem: $statePurchaseItem\n products: $products")
        if (itemId == null) return@LaunchedEffect
        val cart = statePurchaseItem ?: return@LaunchedEffect
        val item = cart.items.firstOrNull{ it.id == itemId } ?: return@LaunchedEffect

        selectedProduct = products.firstOrNull { it.idProduct == item.productId } ?: return@LaunchedEffect

        quantityProduct = item.quantity.toString()

        Log.d("EditMode",
            """
                itemId: $itemId
                product: $selectedProduct
                quantity: $quantityProduct
            """.trimIndent())
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
                /*if (statePurchaseItem.isEdit) {
                    Text(text = stringResource(R.string.title_edit_purchase))
                } else {*/
                    Text(text = stringResource(R.string.title_purchase))
//                }
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
                            items = products,
                            selectedItem = selectedProduct,
                            label = "Producto",
                            itemLabel = { it.nameProduct },
                            onItemSelected = { selectedProduct = it },
                            modifier = Modifier.fillMaxWidth(),
                            /*isError = statePurchaseItem.idProductError != null,
                            supportingText = statePurchaseItem.idProductError*/
                        )
                        CustomizedOutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            onValueChange = { quantityProduct = it},
//                            onValueChange = purchaseViewModel::onQuantity,
                            value = quantityProduct,
                            label = { Text(text = stringResource(R.string.label_quantity_purchase)) },
//                            onFocusLost = purchaseViewModel::onQuantityBlur
                        )
                    }
                }
                CustomizedFilledCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {}
                ) {
                    CustomizedButton(
                        onClick = {
                            /*val product = selectedProduct ?: return@CustomizedButton
                            val qty = statePurchaseItem.quantity.toIntOrNull() ?: return@CustomizedButton

                            val item = PurchaseItem(product, qty)

                            purchaseViewModel.addItem(item, selectedClient)

                            quantityProduct = ""
                            selectedProduct = null*/

//                            purchaseViewModel.addItem()
                            selectedProduct?.let {
                                val qty = quantityProduct.toInt()
                                Log.d("selectedProduct","Revisando el itemId: $itemId")
                                if (itemId == null){
                                    purchaseViewModel.addOrUpdateItem(it, qty)
                                } else {
                                    purchaseViewModel.updateItemQuantity(itemId, qty)
                                }
                                onSave()
                            }
                        }
                    ) {
                        Text(text = "Agregar al carrito")
                    }
                }
            }
        }
    }
}