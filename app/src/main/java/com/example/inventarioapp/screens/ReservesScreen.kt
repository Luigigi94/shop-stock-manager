package com.example.inventarioapp.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Folder
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.inventarioapp.ui.components.CustomizedTopAppBar
import com.example.inventarioapp.viewmodel.ReserveViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.inventarioapp.R
import com.example.inventarioapp.navigation.AppScreens
import com.example.inventarioapp.ui.components.CustomizedButton
import com.example.inventarioapp.ui.components.CustomizedDatePicker
import com.example.inventarioapp.ui.components.CustomizedEditRows
import com.example.inventarioapp.ui.components.CustomizedExposedDropdownMenu
import com.example.inventarioapp.ui.components.CustomizedFAB
import com.example.inventarioapp.ui.components.CustomizedFilledCard
import com.example.inventarioapp.ui.components.CustomizedOutlinedCard
import com.example.inventarioapp.ui.components.CustomizedOutlinedTextField
import com.example.inventarioapp.ui.components.ListOfReserves
import com.example.inventarioapp.ui.utils.hideKeyboardOnTap
import com.example.inventarioapp.viewmodel.ClientViewModel
import com.example.inventarioapp.viewmodel.ProductViewModel
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservesScreen(
    darkThemeState: MutableState<Boolean>,
    navController: NavController,
    reserveId: String?,
    reserveViewModel: ReserveViewModel = viewModel()
){
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    var addReserveForm by remember { mutableStateOf(false) }

    LaunchedEffect(navBackStackEntry) {
        val result = navBackStackEntry?.savedStateHandle?.get<Boolean>("openCreateForm")
        if (result == true){
            addReserveForm = true
            navBackStackEntry?.savedStateHandle?.remove<Boolean>("openCreateForm")
        }
    }

    val today = reserveViewModel.currentDate()

    BackHandler(enabled = addReserveForm) {
        addReserveForm = false
    }
    val snackbarHostState = remember { SnackbarHostState() }

    val stateReserve by reserveViewModel.uiState.collectAsState()

    LaunchedEffect(reserveId) {
        if (reserveId == null){
            reserveViewModel.startCreate()
        } else {
            reserveViewModel.loadReserve(reserveId)
            reserveViewModel.loadHistory(reserveId)
        }
    }


    val totalPaid by reserveViewModel.totalPayments.collectAsStateWithLifecycle()

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
    val clients by clientViewModel.clients.collectAsState()
    val productViewModel: ProductViewModel = viewModel()
    val products by productViewModel.products.collectAsState()

    val selectedClient = clients.firstOrNull{
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
                    Column {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            CustomizedExposedDropdownMenu(
                                items = clients,
                                selectedItem = selectedClient,
                                label = stringResource(R.string.on_action_client),
                                itemLabel = { it.nameClient },
                                onItemSelected = { client ->
                                    reserveViewModel.onIdClient(client.idClient)
                                },
                                modifier = Modifier.weight(1f),
                                isError = stateReserve.idClientError != null,
                                supportingText = stateReserve.idClientError,
                                isReadOnly = stateReserve.isEdit
                            )
                            if (!stateReserve.isEdit) {
                                CustomizedButton(
                                    modifier = Modifier.weight(0.3f),
                                    onClick = {
                                        navController.navigate("${AppScreens.ClientScreen.route}?openCreateForm=true")
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Folder,
                                        contentDescription = "Descriptión"
                                    )
                                }
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CustomizedExposedDropdownMenu(
                                items = products,
                                selectedItem = selectedProduct,
                                label = stringResource(R.string.on_action_product),
                                itemLabel = { it.nameProduct },
                                onItemSelected = { prod ->
                                    reserveViewModel.onIdProduct(prod.idProduct)
                                },
                                modifier = Modifier,
                                isError = stateReserve.idProductError != null,
                                supportingText = stateReserve.idProductError,
                                isReadOnly = stateReserve.isEdit
                            )
                        }
                        if (stateReserve.isEdit){
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    CustomizedOutlinedTextField(
                                        modifier = Modifier.weight(1f),
                                        value = stateReserve.qtyReserve.toString(),
                                        onValueChange = { newVal ->
                                            reserveViewModel.onQtyReserve(
                                                newVal
                                            )
                                        },
                                        label = { Text(text = stringResource(R.string.label_qty_reserved)) },
                                        error = stateReserve.qtyReserveError,
                                        onFocusLost = reserveViewModel::onQtyReserveBlur,
                                        readOnly = true

                                    )
                                    CustomizedOutlinedTextField(
                                        modifier = Modifier.weight(1f),
                                        value = selectedProduct?.priceProduct.toString(),
                                        onValueChange = {},
                                        label = { Text(text = stringResource(R.string.label_price_product)) },
                                        error = stateReserve.qtyReserveError,
                                        onFocusLost = reserveViewModel::onQtyReserveBlur,
                                        readOnly = true
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    CustomizedOutlinedTextField(
                                        modifier = Modifier.weight(1f),
                                        value = stateReserve.lastAmount.toString(),
                                        onValueChange = { },
                                        label = { Text(text = stringResource(R.string.label_last_amount)) },
                                        readOnly = true
                                    )
                                    CustomizedOutlinedTextField(
                                        modifier = Modifier.weight(1f),
                                        value = totalPaid.toString(),
                                        onValueChange = { newAmount ->
                                            reserveViewModel.onAmount(
                                                newAmount
                                            )
                                        },
                                        label = { Text(text = stringResource(R.string.label_pending_amount)) },
                                        error = stateReserve.amountError?.errorResId,
                                        errorArgs = stateReserve.amountError?.args,
                                        onFocusLost = reserveViewModel::onAmountBlur,
                                        readOnly = true
                                    )
                                }
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CustomizedOutlinedTextField(
                                modifier = Modifier.weight(1f),
                                value = stateReserve.qtyReserve.toString(),
                                onValueChange = { newVal -> reserveViewModel.onQtyReserve(newVal) },
                                label = { Text(text = stringResource(R.string.label_quantity_product)) },
                                error = stateReserve.qtyReserveError,
                                onFocusLost = reserveViewModel::onQtyReserveBlur
                            )
                            CustomizedOutlinedTextField(
                                modifier = Modifier.weight(1f),
                                value = stateReserve.amount.toString(),
                                onValueChange = { newAmount -> reserveViewModel.onAmount(newAmount) },
                                label = { Text(text = stringResource(R.string.label_amount)) },
                                error = stateReserve.amountError?.errorResId,
                                errorArgs = stateReserve.amountError?.args,
                                onFocusLost = reserveViewModel::onAmountBlur
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CustomizedOutlinedTextField(
                                modifier = Modifier.weight(1f),
                                onValueChange = {},
                                value = today,
                                readOnly = true,
                                label = { Text("Inicio de Apartado") }
                            )
                            CustomizedDatePicker(
                                modifier = Modifier.weight(1f),
                                selectedDayMillis = stateReserve.endReserve?.time,
                                onDateSelected = { millis ->
                                    millis?.let {
                                        reserveViewModel.onEndReserve(Date(it))
                                    }
                                }
                            )
                        }

                        Spacer(modifier = Modifier.size(10.dp))
                        CustomizedOutlinedCard(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {}
                        ) {
                            CustomizedFilledCard(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {}
                            ) {
                                CustomizedEditRows(
                                    onCancel = { navController.popBackStack() },
                                    onDelete = {reserveViewModel.deleteReserve() },
                                    onAction = {
                                        if (stateReserve.isEdit){
                                            reserveViewModel.updateReserve()
                                        } else {
                                            reserveViewModel.addReserve()
                                            navController.popBackStack()
                                        }
                                    },
                                    isEdit = stateReserve.isEdit,
                                    label = stringResource(R.string.on_action_product)
                                )
                            }
                        }
                    }
                }
            }

            if (!stateReserve.isEdit && !addReserveForm){
                CustomizedFilledCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ListOfReserves(
                        modifier = Modifier,
                        list = listReserves,
                        reserveVM = reserveViewModel,
                        onClick = {
                            navController.navigate(
                                route = "${AppScreens.ReservesScreen.route}?reserveId=${it.idReserves}"
                            )
                        }
                    )
                }
            }
        }
    }
}