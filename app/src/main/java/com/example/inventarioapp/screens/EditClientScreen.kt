package com.example.inventarioapp.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import com.example.inventarioapp.ui.components.CustomizedButton
import com.example.inventarioapp.ui.components.CustomizedEditRows
import com.example.inventarioapp.ui.components.CustomizedFilledCard
import com.example.inventarioapp.ui.components.CustomizedOutlinedTextField
import com.example.inventarioapp.ui.components.CustomizedTopAppBar
import com.example.inventarioapp.ui.utils.hideKeyboardOnTap
import com.example.inventarioapp.viewmodel.ClientViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditClientScreen(darkThemeState: MutableState<Boolean>, navController: NavController, clientId: String?, viewModel: ClientViewModel = viewModel()){
    var nameClient by remember { mutableStateOf("") }
    var apePClient by remember { mutableStateOf("") }
    var apeMClient by remember { mutableStateOf("") }
    var telephone by remember { mutableStateOf("") }

    val client = viewModel.selectedClient.collectAsState()

    LaunchedEffect(clientId) {
        clientId?.let { viewModel.loadClient(clientId) }
    }

    LaunchedEffect(client.value) {
        val current = client.value ?: return@LaunchedEffect
        nameClient = current.nameClient
        apePClient = current.apePClient
        apeMClient = current.apeMClient
        telephone = current.telephone
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
                .hideKeyboardOnTap(),
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
                        value = nameClient,
                        label = { Text( stringResource(R.string.label_name_client)) },
                        onValueChange = { nameClient = it},
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row() {
                        CustomizedOutlinedTextField(
                            value = apePClient,
                            label = { Text( stringResource(R.string.label_apep_client)) },
                            onValueChange = { apePClient = it},
                            modifier = Modifier.weight(2f)
                        )
                        CustomizedOutlinedTextField(
                            value = apeMClient,
                            label = { Text( stringResource(R.string.label_apem_client)) },
                            onValueChange = { apeMClient = it},
                            modifier = Modifier.weight(2f)
                        )
                    }
                    CustomizedOutlinedTextField(
                        value = telephone,
                        label = { Text( stringResource(R.string.label_telephone_client)) },
                        onValueChange = { telephone = it},
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            CustomizedFilledCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = {}
            ) {
                CustomizedEditRows(
                    onCancel = { navController.popBackStack() },
                    onDelete = {
                        viewModel.deleteClient(clientId.toString())
                        navController.popBackStack()
                    },
                    onUpdate = {
                        if (nameClient.isNotBlank() && apePClient.isNotBlank()){
                            val updateClient = Clients(
                                nameClient = nameClient,
                                apePClient = apePClient,
                                apeMClient = apeMClient,
                                telephone = telephone
                            )

                            viewModel.updateClient(updateClient)
                            navController.popBackStack()
                        }
                    }
                )
            }
        }
    }
}
