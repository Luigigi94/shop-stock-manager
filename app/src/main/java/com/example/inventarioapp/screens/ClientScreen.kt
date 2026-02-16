package com.example.inventarioapp.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.inventarioapp.R
import com.example.inventarioapp.model.Clients
import com.example.inventarioapp.navigation.AppScreens
import com.example.inventarioapp.ui.components.CustomizedButton
import com.example.inventarioapp.ui.components.CustomizedFilledCard
import com.example.inventarioapp.ui.components.CustomizedListOfEditables
import com.example.inventarioapp.ui.components.CustomizedOutlinedTextField
import com.example.inventarioapp.ui.components.CustomizedTopAppBar
import com.example.inventarioapp.ui.utils.hideKeyboardOnTap
import com.example.inventarioapp.viewmodel.ClientViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientScreen(darkThemeState: MutableState<Boolean>, navController: NavController, viewModel: ClientViewModel = viewModel()){
//    var nameClient by remember { mutableStateOf("") }
//    var apePClient by remember { mutableStateOf("") }
//    var apeMClient by remember { mutableStateOf("") }
//    var telephone by remember { mutableStateOf("") }

    val stateClient by viewModel.uiState.collectAsState()

    val listClients by viewModel.clients.collectAsState()
    val message by viewModel.uiMessage.collectAsState()

    LaunchedEffect(message) {
        message?.let {
            val text = when {
                it == "SUCCEEDED_ADD_CLIENT" ->
                    navController.context.getString(R.string.result_success_added_client)

                it.startsWith("ERROR_ADD_CLIENT") ->
                    navController.context.getString(R.string.result_failure_added_client)

                else -> it
            }
            Toast.makeText(navController.context, text, Toast.LENGTH_SHORT).show()
        }
    }
    Scaffold(
        topBar = {
            CustomizedTopAppBar(
                title = "ClientScreen",
                navController = navController,
                darkThemeState = darkThemeState,
                showBack = true,
                showThemeSwitch = true
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .hideKeyboardOnTap()
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(text = stringResource(R.string.title_client))
            CustomizedFilledCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = {}
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CustomizedOutlinedTextField(
                        value = stateClient.nameClient,
                        label = { Text( stringResource(R.string.label_name_client)) },
                        onValueChange = viewModel::onNameChange,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row {
                        CustomizedOutlinedTextField(
                            value = stateClient.apePClient,
                            label = { Text( stringResource(R.string.label_apep_client)) },
                            onValueChange = viewModel::onApePChange,
                            modifier = Modifier.weight(2f)
                        )
                        CustomizedOutlinedTextField(
                            value = stateClient.apeMClient,
                            label = { Text( stringResource(R.string.label_apem_client)) },
                            onValueChange = viewModel::onApeMChange,
                            modifier = Modifier.weight(2f)
                        )
                    }
                    CustomizedOutlinedTextField(
                        value = stateClient.telephone,
                        label = { Text( stringResource(R.string.label_telephone_client)) },
                        onValueChange = viewModel::onTelephone,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            CustomizedFilledCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = {}
            ) {
                CustomizedButton(
                    onClick = {
                        if (stateClient.nameClient.isNotBlank() && stateClient.apePClient.isNotBlank()){
                            val newClient = Clients(
                                idClient = UUID.randomUUID().toString(),
                                nameClient = stateClient.nameClient,
                                apePClient = stateClient.apePClient,
                                apeMClient = stateClient.apeMClient,
                                telephone = stateClient.telephone
                            )

                            viewModel.addClient(newClient)
                            viewModel.clearForm()

                        }
                    }
                ) {
                    Text(text = stringResource(R.string.button_add_client))
                }
            }
            CustomizedFilledCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = {}
            ) {
                CustomizedListOfEditables(
                    listClients,
                    modifier = Modifier,
                    label = { it.nameClient },
                    onItemClick = {
                        navController.navigate(route = AppScreens.EditProductScreen.route + "/" + it.idClient)
                    }
                )
            }
        }
    }
}