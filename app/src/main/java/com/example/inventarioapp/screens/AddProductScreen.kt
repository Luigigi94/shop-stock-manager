package com.example.inventarioapp.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.inventarioapp.ui.components.CustomizedQuickAddModal
import com.example.inventarioapp.ui.components.CustomizedTopAppBar
import com.example.inventarioapp.ui.forms.CategoryForm
import com.example.inventarioapp.ui.forms.ProductForm
import com.example.inventarioapp.viewmodel.CategoryViewModel
import com.example.inventarioapp.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    /* darkThemeState: MutableState<Boolean>, */
    navController: NavController,
    productId: String?,
    isCreateMode: Boolean,
    viewModel: ProductViewModel = viewModel()
) {
    var addProductForm by remember { mutableStateOf(false) }
    /*val navBackStackEntry by navController.currentBackStackEntryAsState()

    LaunchedEffect(navBackStackEntry) {
        val result = navBackStackEntry?.savedStateHandle?.get<Boolean>("openCreateForm")
        if (result == true) {
            addProductForm = true
            navBackStackEntry?.savedStateHandle?.remove<Boolean>("openCreateForm")
        }
    }

    BackHandler(enabled = addProductForm) {
        addProductForm = false
    }*/

    var addCategoryModal by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val stateProduct by viewModel.uiState.collectAsState()
    val categoryViewModel: CategoryViewModel = viewModel()
    val stateCategory by categoryViewModel.uiState.collectAsState()

    LaunchedEffect(productId) {
        if (productId == null) {
            viewModel.startCreate()
        } else {
            viewModel.loadProduct(productId)
        }
    }

    if (stateProduct.isLoading) {
        CircularProgressIndicator()
        return
    }

    LaunchedEffect(stateProduct.success) {
        if (stateProduct.success && stateProduct.isEdit) {
            navController.popBackStack()
        }
    }

    LaunchedEffect(isCreateMode) {
        if (isCreateMode) {
            addProductForm = true
        }
    }

    val listProducts by viewModel.products.collectAsState()

    if (stateProduct.success) {
        val text = stringResource(R.string.products_label_saved)
        LaunchedEffect(Unit) {
            snackbarHostState.showSnackbar(text)
        }
    }

    val categories by categoryViewModel.categories.collectAsState()

    val selectedCategory = categories.firstOrNull {
        it.idCategory == stateProduct.idCategory
    }
    LaunchedEffect(stateCategory.success) {
        if (stateCategory.success){
            viewModel.onCategoryChange(stateCategory.idCategory)
            categoryViewModel.clearForm()
        }
        addCategoryModal = false
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CustomizedTopAppBar(
                title = stringResource(R.string.topbar_products),
                navController = navController,
                /* darkThemeState = darkThemeState, */
                showBack = true,
                showThemeSwitch = true
            )
        }, floatingActionButton = {
            if (!stateProduct.isEdit) {
                FloatingActionButton(
                    onClick = { addProductForm = !addProductForm },
                    containerColor = if (addProductForm) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = if (addProductForm) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = null
                    )
                }
            }
        }
    ) { innerPadding ->
        CustomizedQuickAddModal(
            show = addCategoryModal,
            onDismiss = { addCategoryModal = false },
            onConfirm = {
                categoryViewModel.addCategory()
                addCategoryModal = false
            },
            title = stringResource(R.string.categories_label_new_category)
        ) {
            CategoryForm(
                stateCategory = stateCategory,
                onNameCategory = categoryViewModel::onNameCategory,
                onNameBlur = categoryViewModel::onNameBlur,
                onDescriptionCategory = categoryViewModel::onDescriptionCategory
            )
        }
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
                            text = if (stateProduct.isEdit) stringResource(R.string.products_label_edit) else stringResource(
                                R.string.products_label_list_products
                            ),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
            if (addProductForm || stateProduct.isEdit) {
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
                                text = if (stateProduct.isEdit) stringResource(R.string.generic_label_details) else stringResource(
                                    R.string.products_label_new_product
                                ),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            ProductForm(
                                state = stateProduct,
                                categories = categories,
                                selectedCategory = selectedCategory,
                                onNameChange = viewModel::onNameProduct,
                                onNameBlur = viewModel::onNameBlur,
                                onDescriptionChange = viewModel::onDescriptionProduct,
                                onPriceChange = viewModel::onPriceProduct,
                                onPriceBlur = viewModel::onPriceBlur,
                                onQuantityChange = viewModel::onQuantityProduct,
                                onQuantityBlur = viewModel::onQuantityBlur,
                                onCategorySelected = viewModel::onIdCategory,
                                onAddCategoryClick = { addCategoryModal = true }
                            )
                            Spacer(modifier = Modifier.height(18.dp))
                            CustomizedEditRows(
                                onCancel = {
                                    if (addProductForm) addProductForm =
                                        false else navController.popBackStack()
                                },
                                onDelete = { viewModel.deleteProduct() },
                                onAction = {
                                    if (stateProduct.isEdit) {
                                        viewModel.updateProduct()
                                    } else {
                                        viewModel.addProduct()
                                        addProductForm = false
                                    }
                                },
                                isEdit = stateProduct.isEdit,
                                label = if (stateProduct.isEdit) stringResource(R.string.products_label_update_product) else stringResource(
                                    R.string.products_label_create_product
                                )
                            )
                        }
                    }
                }
            }

            if (!stateProduct.isEdit && !addProductForm) {
                item {
                    CustomizedListOfEditables(
                        listProducts,
                        modifier = Modifier,
                        label = { it.nameProduct },
                        onItemClick = {
                            navController.navigate(route = "${AppScreens.AddProductScreen.route}?productId=${it.idProduct}")
                        }
                    )
                }
            }
        }
    }
}