package com.example.inventarioapp.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.example.inventarioapp.navigation.AppScreens
import com.example.inventarioapp.ui.components.CustomizedEditRows
import com.example.inventarioapp.ui.components.CustomizedFAB
import com.example.inventarioapp.ui.components.CustomizedFilledCard
import com.example.inventarioapp.ui.components.CustomizedListOfEditables
import com.example.inventarioapp.ui.components.CustomizedOutlinedTextField
import com.example.inventarioapp.ui.components.CustomizedTopAppBar
import com.example.inventarioapp.ui.utils.PrevBackStack
import com.example.inventarioapp.ui.utils.hideKeyboardOnTap
import com.example.inventarioapp.viewmodel.CategoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryScreen(
    darkThemeState: MutableState<Boolean>,
    navController: NavController,
    categoryId: String?,
    isCreateMode: Boolean,
    viewModel: CategoryViewModel = viewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val stateCategory by viewModel.uiState.collectAsState()
    var addCategoryForm by remember { mutableStateOf(false) }
    var isRedirectedByProduct by remember { mutableStateOf(false) }

    LaunchedEffect(categoryId) {
        if (categoryId == null) {
            viewModel.startCreate()
        } else {
            viewModel.loadCategory(id = categoryId)
        }
    }

    LaunchedEffect(isCreateMode) {
        if (isCreateMode){
            addCategoryForm = true
            isRedirectedByProduct = true
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
        },
        floatingActionButton = {
            if (!stateCategory.isEdit) {
                if (addCategoryForm) {
                    CustomizedFAB(
                        onClick = {
                            if (isRedirectedByProduct){
                                PrevBackStack(navController)
                            } else {
                                addCategoryForm = false
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close Category Form"
                        )
                    }
                } else {
                    CustomizedFAB(
                        onClick = {
                            addCategoryForm = true
                            viewModel.clearForm()
                        },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add Category Form"
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

            if (stateCategory.isEdit) {
                Text(text = stringResource(R.string.button_edit_category))
            } else {
                if (addCategoryForm) {
                    Text(text = stringResource(R.string.title_category))
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            if (addCategoryForm || stateCategory.isEdit) {
                CustomizedFilledCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {}
                ) {
                    Column(
                        horizontalAlignment = Alignment.Start
                    ) {
                        CustomizedOutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(stringResource(R.string.label_name_category)) },
                            value = stateCategory.nameCategory,
                            onValueChange = viewModel::onNameCategory,
                            onFocusLost = viewModel::onNameBlur,
                            error = stateCategory.nameError
                        )
                        CustomizedOutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(stringResource(R.string.label_description_category)) },
                            value = stateCategory.descriptionCategory ?: "",
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
                            if (isRedirectedByProduct){
                                PrevBackStack(navController)
                            } else {
                                addCategoryForm = false
                            }
                        }
                    },
                    isEdit = stateCategory.isEdit,
                    label = stringResource(R.string.on_action_category)
                )
            }
            if (!stateCategory.isEdit && !addCategoryForm) {
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