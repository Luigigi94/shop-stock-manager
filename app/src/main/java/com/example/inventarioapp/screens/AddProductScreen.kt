package com.example.inventarioapp.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Folder
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
import com.example.inventarioapp.ui.components.CustomizedButton
import com.example.inventarioapp.ui.components.CustomizedEditRows
import com.example.inventarioapp.ui.components.CustomizedExposedDropdownMenu
import com.example.inventarioapp.ui.components.CustomizedFAB
import com.example.inventarioapp.ui.components.CustomizedFilledCard
import com.example.inventarioapp.ui.components.CustomizedListOfEditables
import com.example.inventarioapp.ui.components.CustomizedOutlinedTextField
import com.example.inventarioapp.ui.components.CustomizedTopAppBar
import com.example.inventarioapp.ui.utils.hideKeyboardOnTap
import com.example.inventarioapp.viewmodel.CategoryViewModel
import com.example.inventarioapp.viewmodel.ProductViewModel
import com.example.inventarioapp.viewmodel.StockViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    darkThemeState: MutableState<Boolean>,
    navController: NavController,
    productId: String?,
    viewModel: ProductViewModel = viewModel()
) {
    var addProductForm by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val stateProduct by viewModel.uiState.collectAsState()
    val stockViewModel: StockViewModel = viewModel()
    val stockMap by stockViewModel.stock.collectAsState()
    val currentStock = productId?.let { stockMap[it] } ?: 0

//    var quantityProduct by remember(stateProduct.idProduct) { mutableStateOf(stateProduct.quantityProduct) }


    LaunchedEffect(productId) {
        if (productId == null){
            viewModel.startCreate()
        } else {
            viewModel.loadProduct(productId)
            stockViewModel.observeStockForProducts(listOf(productId))
        }
    }

    if (stateProduct.isLoading){
        CircularProgressIndicator()
        return
    }

    LaunchedEffect(stateProduct.success) {
        if (stateProduct.success && stateProduct.isEdit){
            navController.popBackStack()
        }
    }

    val listProducts by viewModel.products.collectAsState()

    if (stateProduct.success){
        val text = stringResource(R.string.result_success_added_product)
        LaunchedEffect(Unit) {
            snackbarHostState.showSnackbar(text)
        }
    }

    val categoryViewModel: CategoryViewModel = viewModel()

    val categories by categoryViewModel.categories.collectAsState()

//    var selectedCategory by remember { mutableStateOf<Categories?>(null) }
    val selectedCategory = categories.firstOrNull{
        it.idCategory == stateProduct.idCategory
    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CustomizedTopAppBar(
                title = stringResource(R.string.menu_add_product),
                navController = navController,
                darkThemeState = darkThemeState,
                showBack = true,
                showThemeSwitch = true
            )
        }, floatingActionButton = {
            if (!stateProduct.isEdit) {
                if (addProductForm) {
                    CustomizedFAB(
                        onClick = { addProductForm = false },
                        containerColor = MaterialTheme.colorScheme.primary,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close Product Form"
                        )
                    }
                } else {
                    CustomizedFAB(
                        onClick = {
                            addProductForm = true
                            viewModel.clearForm()
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add Product Form"
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
            if (stateProduct.isEdit){
                Text(text = stringResource(R.string.button_edit_product))
            } else{
                if (addProductForm) {
                    Text(text = stringResource(R.string.title_product))
                }
            }
            if (addProductForm || stateProduct.isEdit) {
                Spacer(modifier = Modifier.height(10.dp))
                CustomizedFilledCard(
                    modifier = Modifier.fillMaxWidth(), onClick = {}) {
                    Column(
                        modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start
                    ) {
                        CustomizedOutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            onValueChange = viewModel::onNameProduct,
                            value = stateProduct.nameProduct,
                            label = { Text(text = stringResource(R.string.label_name_product)) },
                            error = stateProduct.nameError,
                            onFocusLost = viewModel::onNameBlur
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        CustomizedOutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            onValueChange = viewModel::onDescriptionProduct,
                            value = stateProduct.descriptionProduct,
                            label = { Text(text = stringResource(R.string.label_description_product)) }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row {
                            CustomizedOutlinedTextField(
                                modifier = Modifier.weight(2f),
                                onValueChange = viewModel::onPriceProduct,
                                value = stateProduct.priceProduct.toString(),
                                label = { Text(text = stringResource(R.string.label_price_product)) },
                                error = stateProduct.priceError,
                                onFocusLost = viewModel::onPriceBlur
                            )
                            if (stateProduct.isEdit) {
                                CustomizedOutlinedTextField(
                                    value = currentStock.toString(),
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text(text = stringResource(R.string.label_quantity_product)) },
                                    modifier = Modifier.weight(2f),
                                    error = null
//                                    label = { Text(text = stringResource(R.string.label_quantity_product)) }
                                )
                            } else {
                                CustomizedOutlinedTextField(
                                    modifier = Modifier.weight(2f),
                                    onValueChange = { viewModel.onQuantityProduct(it) },
                                    value = stateProduct.quantityProduct.toString(),
                                    label = { Text(text = stringResource(R.string.label_quantity_product)) },
                                    error = stateProduct.quantityError,
                                    onFocusLost = viewModel::onQuantityBlur
                                )
                            }
                            /*CustomizedOutlinedTextField(
                                modifier = Modifier.weight(2f),
                                onValueChange = viewModel::onQuantityProduct,
                                label = { Text(text = stringResource(R.string.label_quantity_product)) },
                                value = quantityProduct
                            )*/
                        }
                        Spacer(modifier = Modifier.height(10.dp))
//                    CustomizedOutlinedTextField(modifier = Modifier, onValueChange = { idCategory = it }, value = idCategory, label = { Text(text = stringResource(R.string.label_category_product)) })
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CustomizedExposedDropdownMenu(
                                items = categories,
                                selectedItem = selectedCategory,
                                label = stringResource(R.string.on_action_category),
                                itemLabel = { it.nameCategory },
                                onItemSelected = { viewModel.onIdCategory(it.idCategory) },
                                modifier = Modifier.weight(1f),
                                isError = stateProduct.idCategoryError != null,
                                supportingText = stateProduct.idCategoryError
                            )
                            CustomizedButton(
                                onClick = {
                                    navController.navigate(route = AppScreens.AddCategoryScreen.route)
                                }, modifier = Modifier.weight(0.3f)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Folder,
                                    contentDescription = "Icon Add Category"
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                CustomizedFilledCard(onClick = {}) {
                    CustomizedEditRows(
                        onCancel = { if (addProductForm) addProductForm = false else navController.popBackStack() },
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
                        label = stringResource(R.string.on_action_product)
                    )
                }
            }
            if (!stateProduct.isEdit && !addProductForm) {
                Spacer(modifier = Modifier.height(10.dp))
                CustomizedFilledCard(
                    modifier = Modifier.fillMaxWidth(), onClick = {}) {
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
