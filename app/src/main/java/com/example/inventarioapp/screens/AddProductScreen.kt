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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.inventarioapp.viewmodel.ProductViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.inventarioapp.R
import com.example.inventarioapp.model.Categories
import com.example.inventarioapp.model.Products
import com.example.inventarioapp.navigation.AppScreens
import com.example.inventarioapp.ui.components.CustomizedButton
import com.example.inventarioapp.ui.components.CustomizedExposedDropdownMenu
import com.example.inventarioapp.ui.components.CustomizedFilledCard
import com.example.inventarioapp.ui.components.CustomizedOutlinedCard
import com.example.inventarioapp.ui.components.CustomizedOutlinedTextField
import com.example.inventarioapp.ui.components.CustomizedTopAppBar
import com.example.inventarioapp.ui.utils.hideKeyboardOnTap
import com.example.inventarioapp.viewmodel.CategoryViewModel
import com.google.firebase.Timestamp
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(darkThemeState: MutableState<Boolean>, navController: NavController, viewModel: ProductViewModel = viewModel()){
    var nameProduct by remember { mutableStateOf("") }
    var quantityProduct by remember { mutableStateOf("") }
    var descriptionProduct by remember { mutableStateOf("") }
    var priceProduct by remember { mutableStateOf("") }

    val listProducts by viewModel.products.collectAsState()
    val message by viewModel.uiMessage.collectAsState()

    val categoryViewModel: CategoryViewModel = viewModel()

    val categories by categoryViewModel.categories.collectAsState()

    var selectedCategory by remember { mutableStateOf<Categories?>(null) }

    LaunchedEffect(message) {
        message?.let {
            val text = when {
                it == "SUCCEEDED_ADD_PRODUCT" ->
                    navController.context.getString(R.string.result_success_added_product)
                it.startsWith("ERROR_ADD_PRODUCT") ->
                    navController.context.getString(R.string.result_failure_added_product)
                else -> it
            }
            Toast.makeText(navController.context, text, Toast.LENGTH_SHORT).show()
        }
    }
    Scaffold(
        topBar = {
            CustomizedTopAppBar(
                title = stringResource(R.string.menu_add_product),
                navController = navController,
                darkThemeState = darkThemeState,
                showBack = true,
                showThemeSwitch = true
            )
        }
    ) { innerPading ->

        Column(modifier = Modifier.padding(innerPading).hideKeyboardOnTap()) {
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
                    CustomizedOutlinedTextField(modifier = Modifier.fillMaxWidth(), onValueChange = { nameProduct = it }, value = nameProduct, label = { Text(text = stringResource(R.string.label_name_product)) })
                    Spacer(modifier = Modifier.height(10.dp))
                    CustomizedOutlinedTextField(modifier = Modifier.fillMaxWidth(), onValueChange = { descriptionProduct = it }, value = descriptionProduct, label = { Text(text = stringResource(R.string.label_description_product)) })
                    Spacer(modifier = Modifier.height(10.dp))
                    Row {
                        CustomizedOutlinedTextField(modifier = Modifier.weight(2f), onValueChange = { priceProduct = it }, value = priceProduct, label = { Text(text = stringResource(R.string.label_price_product)) })
                        CustomizedOutlinedTextField(modifier = Modifier.weight(2f), onValueChange = { quantityProduct = it }, value = quantityProduct, label = { Text(text = stringResource(R.string.label_quantity_product)) })
                    }
                    Spacer(modifier = Modifier.height(10.dp))
//                    CustomizedOutlinedTextField(modifier = Modifier, onValueChange = { idCategory = it }, value = idCategory, label = { Text(text = stringResource(R.string.label_category_product)) })
                    CustomizedExposedDropdownMenu(items = categories, selectedItem = selectedCategory, label = "Categoria", itemLabel = { it.nameCategory }, onItemSelected = { selectedCategory = it}, modifier = Modifier.fillMaxWidth())
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            CustomizedFilledCard(onClick = {}) {
                CustomizedButton(
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
                    }
                ) {
                    Text(text = stringResource(R.string.button_add_product))
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            CustomizedFilledCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = {}
            ) {
                ListedProducts(listProducts, navController)
            }
        }

    }
}

@Composable
fun ListedProducts(listProducts: List<Products>, navController: NavController){
    CustomizedFilledCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = {}
    ) {
        LazyColumn {
            items(listProducts) { product ->
                CustomizedOutlinedCard(onClick = { navController.navigate(route = AppScreens.EditProductScreen.route+"/"+product.idProduct)}) {
                    Row (
                        modifier = Modifier,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        Text(text = product.nameProduct)
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Editing Product"
                        )
                    }
                }
            }
        }
    }
}