package com.example.inventarioapp.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ShoppingCartCheckout
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.inventarioapp.R
import com.example.inventarioapp.model.Clients
import com.example.inventarioapp.navigation.AppScreens
import com.example.inventarioapp.ui.LocalSessionViewModel
import com.example.inventarioapp.ui.components.CustomizedExposedDropdownMenu
import com.example.inventarioapp.ui.components.CustomizedListedPurchaseItems
import com.example.inventarioapp.ui.components.CustomizedTopAppBar
import com.example.inventarioapp.ui.utils.hideKeyboardOnTap
import com.example.inventarioapp.viewmodel.ClientViewModel
import com.example.inventarioapp.viewmodel.PurchaseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseScreen(
    darkThemeState: MutableState<Boolean>,
    navController: NavController,
    idUser: String? = "Admin",
    purchaseViewModel: PurchaseViewModel = viewModel()
) {
//    val cart by purchaseViewModel.cart.collectAsState()
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

//    purchaseViewModel.setClient(clienteSeleccionado)

    LaunchedEffect(idUser) {
        purchaseViewModel.observeCart(idUser.toString())
    }
    LaunchedEffect(Unit) {
        purchaseViewModel.start(userId = session?.userName ?: "Unlogged")
    }

    Scaffold(topBar = {
        CustomizedTopAppBar(
            title = "PurchaseScreen",
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
                        val routeParam = "${AppScreens.PurchaseProductScreen.route}/${cartId}?itemId="
                        Log.d("FABAddProduct","Revisando el navParam $routeParam")
                        navController.navigate(routeParam)
                    }) {
                    Icon(
                        imageVector = Icons.Filled.AddShoppingCart,
                        contentDescription = "Agregar al carrito de compra"
                    )
                }
                FloatingActionButton(
                    onClick = {
                        val cartConfirmed = purchaseViewModel.confirmCart()
                        val routeRedir = "${AppScreens.InvoiceScreen.route}/${cartConfirmed}"
                        Log.d("FABConfirmPurchase", "Redirect to $routeRedir")
                        navController.navigate(routeRedir)
                    }) {
                    Icon(
                        imageVector = Icons.Filled.ShoppingCartCheckout,
                        contentDescription = "Confirmar Compra"
                    )
                }
                FloatingActionButton(
                    onClick = { expandedFAB = false }) {
                    Icon(
                        imageVector = Icons.Filled.Close, contentDescription = "close expandedFAB"
                    )
                }
            }
        } else {
            FloatingActionButton(
//                    onClick = { navController.navigate(route = AppScreens.InvoiceScreen.route+"/current_purchase" ) },
                onClick = { expandedFAB = true },
                containerColor = MaterialTheme.colorScheme.primary,

                ) {
                Icon(
                    imageVector = Icons.Filled.Add, contentDescription = "open expandedFAB"
                )
            }
        }
    }, bottomBar = {
        Text(text = "Total: ${cart?.total ?: 0}")
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .hideKeyboardOnTap(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(text = stringResource(R.string.title_purchase))

            CustomizedExposedDropdownMenu(
                items = clientsWithAnonymous,
                selectedItem = selectedClient,
                label = "Cliente (Opcional)",
                itemLabel = { (it?.nameClient+" "+it?.apePClient+" "+it?.apeMClient)?: "Anonimo" },
//                onItemSelected = { selectedClient = it },
                onItemSelected = {
                    selectedClient = it
                    purchaseViewModel.setClient(it)
                },
                modifier = Modifier.fillMaxWidth(),
            )

            CustomizedListedPurchaseItems(
                navController,
                cart = cart ?: return@Column,
                onEditItem = { item ->
                    val routeRedirect = "${AppScreens.PurchaseProductScreen.route}/Admin?itemId=${item.id}"
                    Log.d("PurchaseScreen","Redirecto hacía $routeRedirect")
                    navController.navigate(route = routeRedirect)
                },
                onRemoveItem = { item ->
                    purchaseViewModel.removeItem(item.id, cart?.userId)
                }
            )
        }
    }
}