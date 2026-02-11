package com.example.inventarioapp.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.inventarioapp.ui.components.CustomizedFilledCard
import com.example.inventarioapp.ui.components.CustomizedOutlinedCard
import com.example.inventarioapp.ui.components.CustomizedOutlinedTextField
import com.example.inventarioapp.ui.utils.hideKeyboardOnTap
import com.example.inventarioapp.viewmodel.ClientViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientScreen(navController: NavController, viewModel: ClientViewModel = viewModel()){
    var nameClient by remember { mutableStateOf("") }
    var apePClient by remember { mutableStateOf("") }
    var apeMClient by remember { mutableStateOf("") }
    var telephone by remember { mutableStateOf("") }

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
            TopAppBar(
                title = { Text(text = "ClientScreen") },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.menu_button_back),
                        modifier = Modifier
                            .clickable{ navController.popBackStack() }
                            .padding(horizontal = 12.dp)
                    )
                }
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
            CustomizedFilledCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = {}
            ) {
                CustomizedButton(
                    onClick = {
                        if (nameClient.isNotBlank() && apePClient.isNotBlank()){
                            val newClient = Clients(
                                idClient = UUID.randomUUID().toString(),
                                nameClient = nameClient,
                                apePClient = apePClient,
                                apeMClient = apeMClient,
                                telephone = telephone
                            )

                            viewModel.addClient(newClient)

                            nameClient = ""
                            apePClient = ""
                            apeMClient = ""
                            telephone= ""

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
                ListedClients(listClients, navController)
            }
        }
    }
}

@Composable
fun ListedClients(listClients: List<Clients>, navController: NavController){
    LazyColumn {
        items(listClients){ client ->
            CustomizedOutlinedCard(onClick = {/*TODO: Add edit client screen route*/}) {
                Row(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = client.nameClient +" "+ client.apePClient +" "+ client.apeMClient)
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Editing Client"
                    )
                }
            }
        }
    }
}