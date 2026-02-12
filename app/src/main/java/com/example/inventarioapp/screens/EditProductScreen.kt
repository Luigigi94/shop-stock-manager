package com.example.inventarioapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
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
import com.example.inventarioapp.model.Products
import com.example.inventarioapp.ui.components.CustomizedButton
import com.example.inventarioapp.ui.components.CustomizedEditRows
import com.example.inventarioapp.ui.components.CustomizedExposedDropdownMenu
import com.example.inventarioapp.ui.components.CustomizedFilledCard
import com.example.inventarioapp.ui.components.CustomizedOutlinedTextField
import com.example.inventarioapp.ui.components.CustomizedTopAppBar
import com.example.inventarioapp.ui.utils.hideKeyboardOnTap
import com.example.inventarioapp.viewmodel.CategoryViewModel
import com.example.inventarioapp.viewmodel.ProductViewModel
import com.google.firebase.Timestamp
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductScreen(
    darkThemeState: MutableState<Boolean>,
    navController: NavController,
    productId: String?,
    viewModel: ProductViewModel = viewModel()
) {
    var nameProduct by remember { mutableStateOf("") }
    var quantityProduct by remember { mutableStateOf("") }
    var descriptionProduct by remember { mutableStateOf("") }
    var priceProduct by remember { mutableStateOf("") }


    val categoryViewModel: CategoryViewModel = viewModel()

    val categories by categoryViewModel.categories.collectAsState()

    var selectedCategory by remember { mutableStateOf<Categories?>(null) }

    val product = viewModel.selectedProduct.collectAsState()

    LaunchedEffect(productId) {
        productId?.let { viewModel.loadProduct(productId) }
    }

    LaunchedEffect(product.value) {
        val current = product.value ?: return@LaunchedEffect
        nameProduct = current.nameProduct
        quantityProduct = current.quantityProduct.toString()
        descriptionProduct = current.descriptionProduct
        priceProduct = current.priceProduct.toString()
        selectedCategory = categories.find { it.idCategory == current.idCategory }
    }

    Scaffold(
        topBar = {
            CustomizedTopAppBar(
                title = stringResource(R.string.title_product),
                navController,
                darkThemeState,
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
            Text(text = stringResource(R.string.title_product))
            Spacer(modifier = Modifier.height(10.dp))
            CustomizedFilledCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = {}
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    CustomizedOutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        onValueChange = { nameProduct = it },
                        value = nameProduct,
                        label = { Text(text = stringResource(R.string.label_name_product)) })
                    Spacer(modifier = Modifier.height(10.dp))
                    CustomizedOutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        onValueChange = { descriptionProduct = it },
                        value = descriptionProduct,
                        label = { Text(text = stringResource(R.string.label_description_product)) })
                    Spacer(modifier = Modifier.height(10.dp))
                    Row {
                        CustomizedOutlinedTextField(
                            modifier = Modifier.weight(2f),
                            onValueChange = { priceProduct = it },
                            value = priceProduct,
                            label = { Text(text = stringResource(R.string.label_price_product)) })
                        CustomizedOutlinedTextField(
                            modifier = Modifier.weight(2f),
                            onValueChange = { quantityProduct = it },
                            value = quantityProduct,
                            label = { Text(text = stringResource(R.string.label_quantity_product)) })
                    }
                    Spacer(modifier = Modifier.height(10.dp))
//                    CustomizedOutlinedTextField(modifier = Modifier, onValueChange = { idCategory = it }, value = idCategory, label = { Text(text = stringResource(R.string.label_category_product)) })
                    CustomizedExposedDropdownMenu(
                        items = categories,
                        selectedItem = selectedCategory,
                        label = "Categoria",
                        itemLabel = { it.nameCategory },
                        onItemSelected = { selectedCategory = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            CustomizedFilledCard(onClick = {}) {
                CustomizedEditRows(
                    onCancel = { navController.popBackStack() },
                    onDelete = {
                        viewModel.deleteProduct(productId.toString())
                        navController.popBackStack()
                    },
                    onUpdate = {
                        if (nameProduct.isNotBlank() && priceProduct.isNotBlank() && quantityProduct.isNotBlank() && selectedCategory != null) {
                            val updatedProduct = Products(
                                idProduct = productId.toString(),
                                nameProduct = nameProduct,
                                quantityProduct = quantityProduct.toInt(),
                                descriptionProduct = descriptionProduct,
                                priceProduct = priceProduct.toDouble(),
                                statusProduct = true,
                                idCategory = selectedCategory!!.idCategory,
                                updatedAt = Timestamp.now()
                            )

                            viewModel.updateProduct(updatedProduct)
                            navController.popBackStack()
                        }
                    }
                )
//                Column {
//                    Row {
//                        CustomizedButton(
//                            modifier = Modifier.weight(2f),
//                            onClick = {
//                                navController.popBackStack()
//                            }
//                        ) {
//                            Row {
//                                Icon(
//                                    imageVector = Icons.Filled.Close,
//                                    contentDescription = stringResource(R.string.button_cancel)
//                                )
//                                Text(text = stringResource(R.string.button_cancel))
//                            }
//                        }
//                        CustomizedButton(
//                            modifier = Modifier.weight(2f),
//                            onClick = {
//                                if (productId != null) {
//                                    viewModel.deleteProduct(productId)
//                                    navController.popBackStack()
//                                } else {
//                                    /*TODO: agregar toast para decir que algo falló*/
//                                }
//                            }) {
//                            Row {
//                                Icon(
//                                    imageVector = Icons.Filled.Delete,
//                                    contentDescription = stringResource(R.string.button_delete)
//                                )
//                                Text(text = stringResource(R.string.button_delete))
//                            }
//                        }
//                    }
//
//                    CustomizedButton(
//                        onClick = {
//                            if (nameProduct.isNotBlank() && priceProduct.isNotBlank() && quantityProduct.isNotBlank() && selectedCategory != null) {
//                                val updatedProduct = Products(
//                                    idProduct = productId.toString(),
//                                    nameProduct = nameProduct,
//                                    quantityProduct = quantityProduct.toInt(),
//                                    descriptionProduct = descriptionProduct,
//                                    priceProduct = priceProduct.toDouble(),
//                                    statusProduct = true,
//                                    idCategory = selectedCategory!!.idCategory,
//                                    updatedAt = Timestamp.now()
//                                )
//
//                                viewModel.updateProduct(updatedProduct)
//                                navController.popBackStack()
//                            }
//                        },
//                        modifier = Modifier.fillMaxWidth()
//                    ) {
//                        Text(text = stringResource(R.string.button_edit_product))
//                    }
//                }
            }
        }

    }
}