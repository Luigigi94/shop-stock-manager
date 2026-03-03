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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.example.inventarioapp.model.Clients
import com.example.inventarioapp.navigation.AppScreens
import com.example.inventarioapp.ui.LocalSessionViewModel
import com.example.inventarioapp.ui.components.CustomizedExposedDropdownMenu
import com.example.inventarioapp.ui.components.CustomizedListedPurchaseItems
import com.example.inventarioapp.ui.components.CustomizedTitleScreens
import com.example.inventarioapp.ui.components.CustomizedTopAppBar
import com.example.inventarioapp.viewmodel.ClientViewModel
import com.example.inventarioapp.viewmodel.PurchaseViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseScreen(
    darkThemeState: MutableState<Boolean>,
    navController: NavController,
    idUser: String? = "",
    purchaseViewModel: PurchaseViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    var isProcessing by remember { mutableStateOf(false) }
    val sessionViewModel = LocalSessionViewModel.current
    val session by sessionViewModel.session.collectAsState()

    val cart by purchaseViewModel.cart.collectAsState()
    var expandedFAB by remember { mutableStateOf(false) }

    val clientViewModel: ClientViewModel = viewModel()

    val clients by clientViewModel.clients.collectAsState()

    val clientsWithAnonymous = remember(clients) {
        listOf(null) + clients
    }
    var selectedClient by remember { mutableStateOf<Clients?>(null) }

    LaunchedEffect(idUser) {
        if (idUser.isNullOrBlank()) return@LaunchedEffect
        purchaseViewModel.observeCart(idUser)
    }
    LaunchedEffect(Unit) {
        purchaseViewModel.start(userId = session?.userName ?: "Unlogged")
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            CustomizedTopAppBar(
                title = stringResource(R.string.topbar_purchase),
                navController = navController,
                darkThemeState = darkThemeState,
                showBack = true,
                showThemeSwitch = true
            )
        }, floatingActionButton = {
            if (expandedFAB) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FloatingActionButton(
                        onClick = {
                            val cartId = cart?.id ?: "Admin"
                            val routeParam =
                                "${AppScreens.PurchaseProductScreen.route}/${cartId}?itemId="
                            Log.d("FABAddProduct", "Revisando el navParam $routeParam")
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
                                        val cartConfirmed = purchaseViewModel.confirmCart()
                                        val routeRedir =
                                            "${AppScreens.InvoiceScreen.route}/${cartConfirmed}"
                                        Log.d("FABConfirmPurchase", "Redirect to $routeRedir")

                                        if (cartConfirmed != null) {
                                            navController.navigate(routeRedir)
                                        } else {
                                            Log.e(
                                                "PurchaseScreen@onClick",
                                                "value cart: $cartConfirmed"
                                            )
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
                        onClick = { expandedFAB = false },
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
                    onClick = { expandedFAB = true },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    ) {
                    Icon(
                        imageVector = Icons.Filled.Add, contentDescription = "open expandedFAB"
                    )
                }
            }
        }, bottomBar = {
            Text(text = "Total: ${cart?.total ?: 0}")
        }) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                CustomizedTitleScreens(stringResource(R.string.purchase_label_listed_prods))
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
                        Text(text = stringResource(R.string.purchase_label_listed_prods))

                        CustomizedExposedDropdownMenu(
                            items = clientsWithAnonymous,
                            selectedItem = selectedClient,
                            label = "Cliente (Opcional)",
                            itemLabel = {
                                (it?.nameClient + " " + it?.apePClient + " " + it?.apeMClient)
                                    ?: "Anonimo"
                            },
//                onItemSelected = { selectedClient = it },
                            onItemSelected = {
                                selectedClient = it
                                purchaseViewModel.setClient(it)
                            },
                            modifier = Modifier.fillMaxWidth(),
                        )

                        if (cart?.items?.isNotEmpty() == true) {
                            CustomizedListedPurchaseItems(
                                cart = cart ?: return@Column,
                                onEditItem = { item ->
                                    val routeRedirect =
                                        "${AppScreens.PurchaseProductScreen.route}/Admin?itemId=${item.id}"
                                    Log.d("PurchaseScreen", "Redirecto hacía $routeRedirect")
                                    navController.navigate(route = routeRedirect)
                                },
                                onRemoveItem = { item ->
                                    purchaseViewModel.removeItem(item.id, cart?.userId)
                                }
                            )
                        } else {
                            Text(
                                text = "Aquí se listarán los productos que vas a vender",
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