package com.example.inventarioapp.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.inventarioapp.R
import com.example.inventarioapp.model.Supplier
import com.example.inventarioapp.navigation.AppScreens
import com.example.inventarioapp.ui.LocalSessionViewModel
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

    val selectedSupplier by remember { mutableStateOf<Supplier?>(null) }

    LaunchedEffect(idUser) {
        if (idUser.isNullOrBlank()) return@LaunchedEffect
        viewModel.observeSupplierCart(idUser)
        viewModel.start(idUser)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            CustomizedTopAppBar(
                title = "${stringResource(R.string.topbar_suppliers)} titleTopbarr",
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
//                    onClick = { navController.navigate(route = AppScreens.InvoiceScreen.route+"/current_purchase" ) },
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
    ) { }
}