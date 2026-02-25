package com.example.inventarioapp.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.inventarioapp.ui.components.CustomizedTopAppBar
import com.example.inventarioapp.viewmodel.ReserveViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.example.inventarioapp.R
import com.example.inventarioapp.ui.components.CustomizedFAB
import com.example.inventarioapp.ui.components.CustomizedFilledCard
import com.example.inventarioapp.ui.components.CustomizedListOfEditables
import com.example.inventarioapp.ui.utils.hideKeyboardOnTap
import com.example.inventarioapp.viewmodel.CategoryViewModel
import com.example.inventarioapp.viewmodel.ClientViewModel
import com.example.inventarioapp.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservesScreen(
    darkThemeState: MutableState<Boolean>,
    navController: NavController,
    reserveId: String?,
    reserveViewModel: ReserveViewModel = viewModel()
){
    var addReserveForm by remember { mutableStateOf(false) }

    BackHandler(enabled = addReserveForm) {
        // Si el usuario pulsa atrás y el form está abierto, solo cerramos el form
        addReserveForm = false
    }
    val snackbarHostState = remember { SnackbarHostState() }

    val stateReserve by reserveViewModel.uiState.collectAsState()

    LaunchedEffect(reserveId) {
        if (reserveId == null){
            reserveViewModel.startCreate()
        } /*else {
            reserveViewModel.loadReserve(reserveId)
        }*/
    }

    if (stateReserve.isLoading){
        CircularProgressIndicator()
        return
    }

    val listReserves by reserveViewModel.reserves.collectAsState()

    if (stateReserve.success){
        val text = stringResource(R.string.result_success_added_product)
        LaunchedEffect(Unit) {
            snackbarHostState.showSnackbar(text)
        }
    }

    val clientViewModel: ClientViewModel = viewModel()
    val categories by clientViewModel.clients.collectAsState()
    val productViewModel: ProductViewModel = viewModel()
    val products by productViewModel.products.collectAsState()

    val selectedClient = categories.firstOrNull{
        it.idClient == stateReserve.idClient
    }
    val selectedProduct = products.firstOrNull {
        it.idProduct == stateReserve.idProduct
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CustomizedTopAppBar(
                title = "ReservesScreen",
                navController = navController,
                darkThemeState = darkThemeState,
                showBack = true,
                showThemeSwitch = true
            )
        }, floatingActionButton = {
            if(!stateReserve.isEdit){
                if (addReserveForm){
                    CustomizedFAB(
                        onClick = { addReserveForm = false },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close Reserve Form"
                        )
                    }
                } else {
                    CustomizedFAB(
                        onClick = {
                            addReserveForm = true
                            reserveViewModel.clearForm()
                        },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add Reserve Form"
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .hideKeyboardOnTap()
        ) {
            if (stateReserve.isEdit){
                Text(text = "Edit Reserve")
            } else {
                Text(text = "New Reserve")
            }

            if (addReserveForm || stateReserve.isEdit){
                CustomizedFilledCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Add form")
                }
            }

            if (!stateReserve.isEdit && !addReserveForm){
                CustomizedFilledCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CustomizedListOfEditables(
                        listReserves,
                        modifier = Modifier,
                        label = { it.idClient },
                        onItemClick = {}
                    )
                }
            }
        }
    }
}