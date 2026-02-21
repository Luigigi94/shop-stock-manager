package com.example.inventarioapp.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import com.example.inventarioapp.model.Categories
import com.example.inventarioapp.navigation.AppScreens
import com.example.inventarioapp.ui.components.CustomizedButton
import com.example.inventarioapp.ui.components.CustomizedEditRows
import com.example.inventarioapp.ui.components.CustomizedExposedDropdownMenu
import com.example.inventarioapp.ui.components.CustomizedFilledCard
import com.example.inventarioapp.ui.components.CustomizedListOfEditables
import com.example.inventarioapp.ui.components.CustomizedOutlinedTextField
import com.example.inventarioapp.ui.components.CustomizedTopAppBar
import com.example.inventarioapp.ui.utils.hideKeyboardOnTap
import com.example.inventarioapp.viewmodel.CategoryViewModel
import com.example.inventarioapp.viewmodel.ProductViewModel
import com.example.inventarioapp.viewmodel.PurchaseViewModel
import com.example.inventarioapp.viewmodel.StockViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    darkThemeState: MutableState<Boolean>,
    navController: NavController,
    productId: String?,
    viewModel: ProductViewModel = viewModel()
) {
//    var nameProduct by remember { mutableStateOf("") }
//    var quantityProduct by remember { mutableStateOf("") }
//    var descriptionProduct by remember { mutableStateOf("") }
//    var priceProduct by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val stateProduct by viewModel.uiState.collectAsState()
    val stockViewModel: StockViewModel = viewModel()
    val stockMap by stockViewModel.stock.collectAsState()

    LaunchedEffect(productId) {
        if (productId == null){
            viewModel.startCreate()
        } else {
            viewModel.loadProduct(productId)
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
        }) { innerPading ->

        Column(
            modifier = Modifier
                .padding(innerPading)
                .hideKeyboardOnTap()
        ) {
            if (stateProduct.isEdit){
                Text(text = stringResource(R.string.button_edit_product))
            } else{
                Text(text = stringResource(R.string.title_product))
            }
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
                        CustomizedOutlinedTextField(
                            modifier = Modifier.weight(2f),
                            onValueChange = viewModel::onQuantityProduct,
                            value = stateProduct.quantityProduct.toString(),
                            label = { Text(text = stringResource(R.string.label_quantity_product)) },
                            error = stateProduct.quantityError,
                            onFocusLost = viewModel::onQuantityBlur
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
//                    CustomizedOutlinedTextField(modifier = Modifier, onValueChange = { idCategory = it }, value = idCategory, label = { Text(text = stringResource(R.string.label_category_product)) })
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CustomizedExposedDropdownMenu(
                            items = categories,
                            selectedItem = selectedCategory,
                            label = "Categoria",
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
                    onCancel = { navController.popBackStack() },
                    onDelete = { viewModel.deleteProduct() },
                    onAction = {
                        if (stateProduct.isEdit){
                            viewModel.updateProduct()
                        } else {
                            viewModel.addProduct()
                        }
                    },
                    isEdit = stateProduct.isEdit,
                    label = "Producto"
                )
                /*CustomizedButton(
                    onClick = {
                        if (nameProduct.isNotBlank() && priceProduct.isNotBlank() && quantityProduct.isNotBlank() && selectedCategory != null) {
                            val newProduct = Products(
                                idProduct = UUID.randomUUID().toString(),
                                nameProduct = nameProduct,
                                quantityProduct = quantityProduct.toInt(),
                                descriptionProduct = descriptionProduct,
                                priceProduct = priceProduct.toDouble(),
                                statusProduct = true,
                                idCategory = selectedCategory!!.idCategory,
                                createdAt = Timestamp.now()
                            )

                            viewModel.addProduct(newProduct)

                            nameProduct = ""
                            quantityProduct = ""
                            descriptionProduct = ""
                            priceProduct = ""
                        }
                    }) {
                    Text(text = stringResource(R.string.button_add_product))
                }*/
            }
            if (!stateProduct.isEdit) {
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
