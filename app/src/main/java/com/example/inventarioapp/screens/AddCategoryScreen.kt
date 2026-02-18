package com.example.inventarioapp.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.inventarioapp.R
import com.example.inventarioapp.navigation.AppScreens
import com.example.inventarioapp.ui.components.CustomizedEditRows
import com.example.inventarioapp.ui.components.CustomizedFilledCard
import com.example.inventarioapp.ui.components.CustomizedListOfEditables
import com.example.inventarioapp.ui.components.CustomizedOutlinedTextField
import com.example.inventarioapp.ui.components.CustomizedTopAppBar
import com.example.inventarioapp.ui.utils.hideKeyboardOnTap
import com.example.inventarioapp.viewmodel.CategoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryScreen(
    darkThemeState: MutableState<Boolean>,
    navController: NavController,
    categoryId: String?,
    viewModel: CategoryViewModel = viewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val stateCategory by viewModel.uiState.collectAsState()

    LaunchedEffect(categoryId) {
        if (categoryId == null) {
            viewModel.startCreate()
        } else {
            viewModel.loadCategory(id = categoryId)
        }
    }

    if (stateCategory.isLoading) {
        CircularProgressIndicator()
        return
    }

    LaunchedEffect(stateCategory.success) {
        if (stateCategory.success && stateCategory.isEdit) {
            navController.popBackStack()
        }
    }

    val listCategories by viewModel.categories.collectAsState()

    if (stateCategory.success) {
        val text = stringResource(R.string.result_success_added_category)
        LaunchedEffect(Unit) {
            snackbarHostState.showSnackbar(text)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CustomizedTopAppBar(
                title = stringResource(R.string.menu_add_category),
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
        ) {

            if (stateCategory.isEdit) {
                Text(text = stringResource(R.string.button_edit_category))
            } else {
                Text(text = stringResource(R.string.title_category))
            }
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
                        valueInput = stateCategory.nameCategory,
                        onValueChange = viewModel::onNameCategory
                    )
                    AddCategory(
                        texto = stringResource(R.string.label_description_category),
                        valueInput = stateCategory.descriptionCategory,
                        onValueChange = viewModel::onDescriptionCategory
                    )
                }
            }
            Spacer(Modifier.size(10.dp))
            CustomizedEditRows(
                onCancel = {
                    navController.popBackStack()
                },
                onDelete = {
                    viewModel.deleteCategory()
                },
                onAction = {
                    if (stateCategory.isEdit) {
                        viewModel.updateCategory()
                    } else {
                        viewModel.addCategory()
                    }
                },
                isEdit = stateCategory.isEdit,
                label = "Categoria"
            )
            if (!stateCategory.isEdit) {
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
                            navController.navigate(
                                route = "${AppScreens.AddCategoryScreen.route}?categoryId=${it.idCategory}"
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AddCategory(valueInput: String?, texto: String, onValueChange: (String) -> Unit) {
    Spacer(
        modifier = Modifier
            .height(10.dp)
            .fillMaxWidth()
    )
    CustomizedOutlinedTextField(
        valueInput,
        label = { Text(texto) },
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth()
    )
}

//            CustomizedFilledCard(onClick = {}) {
//                CustomizedButton(
//                    onClick = {
//                        if (nameInput.isNotBlank()) {
//                            val newCategory = Categories(
//                                nameCategory = nameInput,
//                                descriptionCategory = descriptionInput,
//                                idCategory = UUID.randomUUID().toString()
//                            )
//                            viewModel.addCategory(newCategory)
//
//                            nameInput = ""
//                            descriptionInput = ""
//                        }
//                    }) {
//                    Text(text = stringResource(R.string.button_add_category))
//                }
//            }