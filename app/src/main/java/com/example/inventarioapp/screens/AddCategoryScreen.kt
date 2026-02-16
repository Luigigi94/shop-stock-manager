package com.example.inventarioapp.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import com.example.inventarioapp.model.Categories
import com.example.inventarioapp.navigation.AppScreens
import com.example.inventarioapp.ui.components.CustomizedButton
import com.example.inventarioapp.ui.components.CustomizedFilledCard
import com.example.inventarioapp.ui.components.CustomizedListOfEditables
import com.example.inventarioapp.ui.components.CustomizedOutlinedCard
import com.example.inventarioapp.ui.components.CustomizedOutlinedTextField
import com.example.inventarioapp.ui.components.CustomizedTopAppBar
import com.example.inventarioapp.ui.utils.hideKeyboardOnTap
import com.example.inventarioapp.viewmodel.CategoryViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryScreen(darkThemeState: MutableState<Boolean>, navController: NavController, viewModel: CategoryViewModel = viewModel()) {
    var nameInput by remember { mutableStateOf("") }
    var descriptionInput by remember { mutableStateOf("") }

//    Lista reactiva desde viewModel (FireBase)
    val listCategories by viewModel.categories.collectAsState()
//    Mensajes para toast
    val message by viewModel.uiMessage.collectAsState()
//    Toast pendiente de hacer generico
    LaunchedEffect(message) {
        message?.let {
            val text = when {
                it == "SUCCEEDED_ADD_CATEGORY" ->
                    navController.context.getString(R.string.result_success_added_category)

                it.startsWith("ERROR_ADD_CATEGORY") ->
                    navController.context.getString(R.string.result_failure_added_category)

                else -> it
            }
            Toast.makeText(navController.context, text, Toast.LENGTH_SHORT).show()
        }
    }
    Scaffold(
        topBar = {
            CustomizedTopAppBar(
                title = stringResource(R.string.menu_add_category),
                navController = navController,
                darkThemeState = darkThemeState,
                showBack = true,
                showThemeSwitch = true
            )
        }
    ) { innerPading ->
        Column(modifier = Modifier
            .padding(innerPading)
            .hideKeyboardOnTap()) {
            Text(text = stringResource(R.string.title_category))
            Spacer(modifier = Modifier.height(10.dp))
            CustomizedFilledCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = {}
            ) {
                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    AddCategory(
                        texto = stringResource(R.string.label_name_category),
                        valueInput = nameInput,
                        onValueChange = { nameInput = it })
                    AddCategory(
                        texto = stringResource(R.string.label_description_category),
                        valueInput = descriptionInput,
                        onValueChange = { descriptionInput = it })
                }
            }
            Spacer(Modifier.size(10.dp))
            CustomizedFilledCard(onClick = {}) {
                CustomizedButton(
                    onClick = {
                        if (nameInput.isNotBlank()) {
                            val newCategory = Categories(
                                nameCategory = nameInput,
                                descriptionCategory = descriptionInput,
                                idCategory = UUID.randomUUID().toString()
                            )
                            viewModel.addCategory(newCategory)

                            nameInput = ""
                            descriptionInput = ""
                        }
                    }) {
                    Text(text = stringResource(R.string.button_add_category))
                }
            }
            Spacer(Modifier.height(10.dp))
            CustomizedFilledCard(
                modifier = Modifier.fillMaxWidth(1f),
                onClick = {}
            ) {
                CustomizedListOfEditables(
                    listCategories,
                    modifier = Modifier,
                    label = { it.nameCategory },
                    onItemClick = {
                        navController.navigate(route = AppScreens.EditProductScreen.route + "/" + it.idCategory)
                    }
                )
            }
        }
    }
}

@Composable
fun AddCategory(valueInput: String, texto: String, onValueChange: (String) -> Unit) {
    Spacer(modifier = Modifier
        .height(10.dp)
        .fillMaxWidth())
    CustomizedOutlinedTextField(
        valueInput,
        label = { Text(texto) },
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth()
    )
}