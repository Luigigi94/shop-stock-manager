package com.example.inventarioapp.screens

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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.inventarioapp.R
import com.example.inventarioapp.navigation.AppScreens
import com.example.inventarioapp.ui.components.CustomizedEditRows
import com.example.inventarioapp.ui.components.CustomizedListOfEditables
import com.example.inventarioapp.ui.components.CustomizedOutlinedTextField
import com.example.inventarioapp.ui.components.CustomizedTopAppBar
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
        if (isCreateMode) {
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
        val text = stringResource(R.string.categories_label_saved)
        LaunchedEffect(Unit) {
            snackbarHostState.showSnackbar(text)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CustomizedTopAppBar(
                title = stringResource(R.string.topbar_categories),
                navController = navController,
                darkThemeState = darkThemeState,
                showBack = true,
                showThemeSwitch = true
            )
        },
        floatingActionButton = {
            if (!stateCategory.isEdit) {
                FloatingActionButton(
                    onClick = { addCategoryForm = !addCategoryForm },
                    containerColor = if (addCategoryForm) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = if (addCategoryForm) Icons.Default.Close else Icons.Default.Add,
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = if (stateCategory.isEdit) stringResource(R.string.categories_label_edit) else stringResource(R.string.categories_label_list_categories),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            if (addCategoryForm || stateCategory.isEdit) {
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
                                text = if (stateCategory.isEdit) stringResource(R.string.generic_label_details) else stringResource(R.string.categories_label_new_category),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            CustomizedOutlinedTextField(
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text(text = stringResource(R.string.categories_label_name_category)) },
                                value = stateCategory.nameCategory,
                                onValueChange = viewModel::onNameCategory,
                                error = stateCategory.nameError
                            )

                            CustomizedOutlinedTextField(
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text(text = stringResource(R.string.categories_label_description_category)) },
                                value = stateCategory.descriptionCategory ?: "",
                                onValueChange = viewModel::onDescriptionCategory
                            )

                            /*Button(
                                onClick = {
                                    if (stateCategory.isEdit) {
                                        viewModel.updateCategory()
                                    } else {
                                        viewModel.addCategory()
                                        if (isRedirectedByProduct) {
                                            navController.popBackStack()
                                        } else {
                                            addCategoryForm = false
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(
                                    Icons.Default.Save,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = if (stateCategory.isEdit) stringResource(R.string.categories_label_update_category) else stringResource(R.string.categories_label_create_category),
                                    fontWeight = FontWeight.Bold
                                )
                            }*/
                            CustomizedEditRows(
                                onCancel = {
                                    if (addCategoryForm) addCategoryForm = false else
                                        navController.popBackStack()
                                },
                                onDelete = { viewModel.deleteCategory() },
                                onAction ={
                                    if (stateCategory.isEdit) {
                                        viewModel.updateCategory()
                                    } else {
                                        viewModel.addCategory()
                                        if (isRedirectedByProduct) {
                                            navController.popBackStack()
                                        } else {
                                            addCategoryForm = false
                                        }
                                    }
                                },
                                isEdit = stateCategory.isEdit,
                                label = if (stateCategory.isEdit) stringResource(R.string.categories_label_update_category) else stringResource(R.string.categories_label_create_category),
                            )
                        }
                    }
                }
            }

            // LISTA: Cada ítem es una tarjeta independiente con su propia elevación
            if (!stateCategory.isEdit && !addCategoryForm) {
                item {
                    CustomizedListOfEditables(
                        list = listCategories,
                        label = { it.nameCategory },
                        description = { it.descriptionCategory },
                        onItemClick = { category ->
                            navController.navigate("${AppScreens.AddCategoryScreen.route}?categoryId=${category.idCategory}")
                        }
                    )
                }
            }
        }
    }
}