package com.example.inventarioapp.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.inventarioapp.R
import com.example.inventarioapp.navigation.AppScreens
import com.example.inventarioapp.ui.components.CustomizedEditRows
import com.example.inventarioapp.ui.components.CustomizedExposedDropdownMenu
import com.example.inventarioapp.ui.components.CustomizedListOfEditables
import com.example.inventarioapp.ui.components.CustomizedOutlinedTextField
import com.example.inventarioapp.ui.components.CustomizedTitleScreens
import com.example.inventarioapp.ui.components.CustomizedTopAppBar
import com.example.inventarioapp.viewmodel.SupplierViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplierScreen(
    darkThemeState: MutableState<Boolean>,
    navController: NavController,
    supplierId: String?,
    viewModel: SupplierViewModel = viewModel()
){
    val snackbarHostState = remember { SnackbarHostState() }

    var addSupplierForm by remember { mutableStateOf(false) }

    LaunchedEffect(supplierId) {
        if (supplierId == null){
            viewModel.startCreate()
        } else {
            viewModel.loadSupplier(supplierId)
        }
    }
    
    val stateSupplier by viewModel.uiState.collectAsState()
    
    if (stateSupplier.isLoading){
        CircularProgressIndicator()
        return
    }
    
    LaunchedEffect(stateSupplier.success) {
        if (stateSupplier.isEdit) {
            navController.popBackStack()
        }
    }
    
    val listSuppliers by viewModel.suppliers.collectAsState()
    
    if (stateSupplier.success){
        val text = stringResource(R.string.suppliers_label_saved)
        LaunchedEffect(Unit) {
            snackbarHostState.showSnackbar(text)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        snackbarHost = {SnackbarHost(snackbarHostState)},
        topBar = {
            CustomizedTopAppBar(
                title = stringResource(R.string.topbar_suppliers),
                navController = navController,
                darkThemeState = darkThemeState,
                showBack = true,
                showThemeSwitch = true
            )
        },
        floatingActionButton = {
            if (!stateSupplier.isEdit){
                FloatingActionButton(
                    onClick = { addSupplierForm = !addSupplierForm},
                    containerColor = if (addSupplierForm) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = if (addSupplierForm) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = null
                    )
                }
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
                CustomizedTitleScreens(if (stateSupplier.isEdit) stringResource(R.string.suppliers_label_edit) else stringResource(R.string.suppliers_label_list_suppliers))
            }

            if (addSupplierForm || stateSupplier.isEdit){
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
                                text = if (stateSupplier.isEdit) stringResource(R.string.generic_label_details)
                                else stringResource(R.string.suppliers_label_new_supplier),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            CustomizedOutlinedTextField(
                                value = stateSupplier.name,
                                label = { Text(stringResource(R.string.suppliers_label_name_supplier)) },
                                onValueChange = viewModel::onNameSupplier,
                                modifier = Modifier.fillMaxWidth(),
                                error = stateSupplier.nameError,
                                onFocusLost = viewModel::onNameBlur
                            )
                            CustomizedOutlinedTextField(
                                value = stateSupplier.phone,
                                label = { Text(stringResource(R.string.suppliers_label_phone_supplier)) },
                                onValueChange = viewModel::onTelephoneSupplier,
                                modifier = Modifier.fillMaxWidth(),
                                error = stateSupplier.phoneError,
                                onFocusLost = viewModel::onPhoneBlur
                            )
                            CustomizedOutlinedTextField(
                                value = stateSupplier.identifierAccount,
                                label = { Text(stringResource(R.string.suppliers_label_identifier_account_supplier)) },
                                onValueChange = viewModel::onIdentifierAccountSupplier,
                                modifier = Modifier.fillMaxWidth(),
                                error = stateSupplier.nameError,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                ),
                                onFocusLost = viewModel::onIdentifierAccountBlur
                            )
                            CustomizedOutlinedTextField(
                                value = stateSupplier.banco,
                                label = { Text(stringResource(R.string.suppliers_label_bank_supplier)) },
                                onValueChange = viewModel::onIdBankSupplier,
                                modifier = Modifier.fillMaxWidth(),
                                error = stateSupplier.bancoError,
                                onFocusLost = viewModel::onBankBlur
                            )

                            CustomizedEditRows(
                                onCancel = {
                                    if (addSupplierForm) addSupplierForm = false
                                    else navController.popBackStack()
                                },
                                onDelete = { viewModel.deleteSupplier() },
                                onAction = { if (stateSupplier.isEdit) viewModel.updateSupplier() else viewModel.addSupplier() },
                                isEdit = stateSupplier.isEdit,
                                label = if (stateSupplier.isEdit) stringResource(R.string.suppliers_label_update_supplier) else stringResource(R.string.suppliers_label_create_supplier)
                            )
                        }
                    }
                }
            }
            if (!stateSupplier.isEdit && !addSupplierForm){
                item {
                    CustomizedListOfEditables(
                        listSuppliers,
                        modifier = Modifier,
                        label = { it.name },
                        description = { it.phone },
                        onItemClick = {
                            navController.navigate(
                                route = "${AppScreens.SupplierScreen.route}?supplierId=${it.id}"
                            )
                        }
                    )
                }
            }
        }
    }
}