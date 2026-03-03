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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.inventarioapp.R
import com.example.inventarioapp.model.Supplier
import com.example.inventarioapp.navigation.AppScreens
import com.example.inventarioapp.ui.LocalSessionViewModel
import com.example.inventarioapp.ui.components.CustomizedExposedDropdownMenu
import com.example.inventarioapp.ui.components.CustomizedListedSupplierPurchaseItems
import com.example.inventarioapp.ui.components.CustomizedTitleScreens
import com.example.inventarioapp.ui.components.CustomizedTopAppBar
import com.example.inventarioapp.viewmodel.SupplierPurchaseViewModel
import com.example.inventarioapp.viewmodel.SupplierViewModel
import kotlinx.coroutines.launch

@Composable
fun SupplierPurchaseScreen(
    darkThemeState: MutableState<Boolean>,
    navController: NavController,
    idUser: String?= "",
    viewModel: SupplierPurchaseViewModel = viewModel()
){
    val scope = rememberCoroutineScope()

    var isProcessing by remember { mutableStateOf(false) }
    val sessionViewModel = LocalSessionViewModel.current
    val session by sessionViewModel.session.collectAsState()

    val itemsSupplier by viewModel.currentPurchase.collectAsState()
    var expandedFab by remember { mutableStateOf(false) }

    val supplierPurchaseViewModel: SupplierPurchaseViewModel = viewModel()
//    val suppliers by supplierPurchaseViewModel.currentPurchase

    var selectedSupplier by remember { mutableStateOf<Supplier?>(null) }

    val suppliers: SupplierViewModel = viewModel()

    val supplierList by suppliers.suppliers.collectAsState()

    LaunchedEffect(idUser) {
        val userSession = session?.userName
        Log.d("SupplierPurchaseScreen","LaunchedEffect -> idUser $userSession")
        if (userSession.isNullOrBlank()) return@LaunchedEffect
        viewModel.observeSupplierCart(userSession)
        viewModel.start(userSession)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            CustomizedTopAppBar(
                title = stringResource(R.string.topbar_suppliers_purchases),
                darkThemeState = darkThemeState,
                navController = navController,
                showBack = true,
                showThemeSwitch = true
            )
        }, floatingActionButton = {
            if (expandedFab){
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FloatingActionButton(
                        onClick = {
                            val cartId = itemsSupplier?.id
                            val routeParam = "${AppScreens.SupplierItemScreen.route}/${cartId}?itemId="
                            navController.navigate(routeParam)
                        },
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AddShoppingCart,
                            contentDescription = "Agregar al carrito de compra"
                        )
                    }
                    FloatingActionButton(
                        onClick = {
                            if (!isProcessing) {
                                isProcessing = true

                                scope.launch {
                                    try {
                                        val cartConfirmed = viewModel.confirmPurchase()
                                        val routeRedir =
                                            "${AppScreens.InvoiceScreen.route}/${cartConfirmed}"

                                        if (cartConfirmed != null) {
                                            navController.navigate(routeRedir)
                                        } else {
                                            isProcessing = false
                                        }
                                    } catch (e: Exception) {
                                        isProcessing = false
                                    }
                                }
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        shape = CircleShape
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(color = Color.White)
                        } else {
                            Icon(
                                imageVector = Icons.Filled.ShoppingCartCheckout,
                                contentDescription = "Confirmar Compra"
                            )
                        }
                    }
                    FloatingActionButton(
                        onClick = { expandedFab = false },
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "close expandedFAB"
                        )
                    }
                }
            } else {
                FloatingActionButton(
                    onClick = { expandedFab = true },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,

                    ) {
                    Icon(
                        imageVector = Icons.Filled.Add, contentDescription = "open expandedFAB"
                    )
                }
            }
        }, bottomBar = {
            Text(
                text = "Total: ${itemsSupplier?.totalCost}"
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
            item{
                CustomizedTitleScreens(stringResource(R.string.topbar_suppliers_purchases))
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
                        Text(text = "Column SupplierPurchaseScreen")


                        CustomizedExposedDropdownMenu(
                            items = supplierList,
                            selectedItem = selectedSupplier,
                            label = "Proveedor",
                            itemLabel = {
                                it.name
                            },
                            onItemSelected = {
                                selectedSupplier = it
                                viewModel.setSupplier(it)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (itemsSupplier?.items?.isNotEmpty() == true) {
                            CustomizedListedSupplierPurchaseItems(
                                supplierCart = itemsSupplier ?: return@Column,
                                onEditItem = { item ->
                                    navController.navigate(route = "${AppScreens.SupplierItemScreen.route}Admin/?itemId?=${item.id}")
                                },
                                onRemoveItem = { item ->
                                    viewModel.removeItem(item.productId)
                                }
                                )
                        } else {
                            Text(
                                text = "Aquí se listarán los productos que se compraron a los proveedores",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}