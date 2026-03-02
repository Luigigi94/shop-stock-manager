package com.example.inventarioapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.inventarioapp.R
import com.example.inventarioapp.navigation.AppScreens
import com.example.inventarioapp.ui.components.CustomizedEditRows
import com.example.inventarioapp.ui.components.CustomizedFAB
import com.example.inventarioapp.ui.components.CustomizedFilledCard
import com.example.inventarioapp.ui.components.CustomizedListOfEditables
import com.example.inventarioapp.ui.components.CustomizedOutlinedTextField
import com.example.inventarioapp.ui.components.CustomizedTitleScreens
import com.example.inventarioapp.ui.components.CustomizedTopAppBar
import com.example.inventarioapp.ui.utils.PrevBackStack
import com.example.inventarioapp.ui.utils.hideKeyboardOnTap
import com.example.inventarioapp.viewmodel.ClientViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientScreen(
    darkThemeState: MutableState<Boolean>,
    navController: NavController,
    clientId: String?,
    isCreateMode: Boolean,
    viewModel: ClientViewModel = viewModel()
){

    val snackbarHostState = remember { SnackbarHostState() }
    
    var addClientForm by remember { mutableStateOf(false) }
    var isRedirectedByReserve by remember { mutableStateOf(false) }

    LaunchedEffect(clientId) {
        if (clientId == null){
            viewModel.startCreate()
        } else {
            viewModel.loadClient(clientId)
        }
    }

    LaunchedEffect(isCreateMode) {
        if (isCreateMode){
            addClientForm = true
            isRedirectedByReserve = true
        }
    }

    val stateClient by viewModel.uiState.collectAsState()

    if (stateClient.isLoading){
        CircularProgressIndicator()
        return
    }

    /*LaunchedEffect(stateClient.success) {
        if (stateClient.isEdit) {
            navController.popBackStack()
        }
    }*/


    val listClients by viewModel.clients.collectAsState()

    if(stateClient.success){
        val text = stringResource(R.string.clients_label_saved)
        LaunchedEffect(Unit) {
            snackbarHostState.showSnackbar(text)
        }
    }
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        snackbarHost = {SnackbarHost(snackbarHostState)},
        topBar = {
            CustomizedTopAppBar(
                title = stringResource(R.string.topbar_clients),
                navController = navController,
                darkThemeState = darkThemeState,
                showBack = true,
                showThemeSwitch = true
            )
        },
        floatingActionButton = {
            if (!stateClient.isEdit){
                FloatingActionButton(
                    onClick = { addClientForm = !addClientForm},
                    containerColor = if (addClientForm) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = if (addClientForm) Icons.Default.Close else Icons.Default.Add,
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
                CustomizedTitleScreens(if (stateClient.isEdit) stringResource(R.string.clients_label_edit) else stringResource(R.string.client_label_list_categories))
            }
            if (addClientForm || stateClient.isEdit) {
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
                                text = if (stateClient.isEdit) stringResource(R.string.generic_label_details)
                                else stringResource(R.string.clients_label_new_client),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            CustomizedOutlinedTextField(
                                value = stateClient.nameClient,
                                label = { Text(stringResource(R.string.clients_label_name_client)) },
                                onValueChange = viewModel::onNameChange,
                                modifier = Modifier.fillMaxWidth(),
                                error = stateClient.nameError,
                                onFocusLost = viewModel::onNameBlur

                            )

                            Row {
                                CustomizedOutlinedTextField(
                                    value = stateClient.apePClient,
                                    label = { Text(stringResource(R.string.clients_label_apep_client)) },
                                    onValueChange = viewModel::onApePChange,
                                    modifier = Modifier.weight(1f),
                                    error = stateClient.apePError,
                                    onFocusLost = viewModel::onApePBlur
                                )
                                CustomizedOutlinedTextField(
                                    value = stateClient.apeMClient,
                                    label = { Text(stringResource(R.string.clients_label_apem_client)) },
                                    onValueChange = viewModel::onApeMChange,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            CustomizedOutlinedTextField(
                                value = stateClient.telephone,
                                label = { Text(stringResource(R.string.clients_label_phone_client)) },
                                onValueChange = viewModel::onTelephone,
                                modifier = Modifier.fillMaxWidth(),
                                error = stateClient.telephoneError,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Phone
                                ),
                                onFocusLost = viewModel::onTelephoneBlur
                            )

                            CustomizedEditRows(
                                onCancel = {
                                    if (addClientForm) addClientForm = false
                                    else navController.popBackStack()
                                },
                                onDelete = { viewModel.deleteClient() },
                                onAction = {
                                    if (stateClient.isEdit){
                                        viewModel.updateClient()
                                        navController.popBackStack()
                                    } else {
                                        viewModel.addClient()
                                        addClientForm = false
                                    }
                                },
                                isEdit = stateClient.isEdit,
                                label = if (stateClient.isEdit) stringResource(R.string.clients_label_update_client)
                                else stringResource(R.string.clients_label_create_client)
                            )
                        }
                    }
                }
            }
            if (!stateClient.isEdit && !addClientForm) {
                item {
                    CustomizedListOfEditables(
                        listClients,
                        modifier = Modifier,
                        label = { "${it.nameClient} ${it.apePClient}" },
                        description = { it.telephone },
                        onItemClick = {
                            navController.navigate(
                                route = "${AppScreens.ClientScreen.route}?clientId=${it.idClient}"
                            )
                        }
                    )
                }
            }
        }
    }
}
