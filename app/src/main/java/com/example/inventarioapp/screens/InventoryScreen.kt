package com.example.inventarioapp.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.inventarioapp.R
import com.example.inventarioapp.navigation.AppScreens
import com.example.inventarioapp.repository.PurchaseRepository
import com.example.inventarioapp.ui.components.CustomizedButton
import com.example.inventarioapp.ui.components.CustomizedEditRows
import com.example.inventarioapp.ui.components.CustomizedFilledCard
import com.example.inventarioapp.ui.components.CustomizedListOfEditables
import com.example.inventarioapp.ui.components.CustomizedTopAppBar
import com.example.inventarioapp.ui.forms.ProductForm
import com.example.inventarioapp.viewmodel.CategoryViewModel
import com.example.inventarioapp.viewmodel.ProductViewModel
import com.example.inventarioapp.viewmodel.PurchaseViewModel

@Composable
fun InventoryScreen(
    darkThemeState: MutableState<Boolean>,
    navController: NavController,
    productId: String?,
    viewModel: ProductViewModel = viewModel()
) {
    val stateProduct by viewModel.uiState.collectAsState()
    val listProducts by viewModel.products.collectAsState()

    val categoryViewModel: CategoryViewModel = viewModel()

    val categories by categoryViewModel.categories.collectAsState()

    val selectedCategory = categories.firstOrNull {
        it.idCategory == stateProduct.idCategory
    }

    LaunchedEffect(productId) {
        if (productId != null) {
            viewModel.loadProduct(productId)
        } else {
            viewModel.startCreate()
        }
    }

    Scaffold(
        topBar = {
            CustomizedTopAppBar(
                title = stringResource(R.string.menu_inventory),
                darkThemeState = darkThemeState,
                navController = navController,
                showThemeSwitch = true,
                showBack = true
            )
        },
        bottomBar = {
            BottomAppBar() {
                CustomizedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
//                        purchaseViewModel.
                    }
                ) { }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            CustomizedFilledCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = null
            ) {
                Column {
                    if (stateProduct.isEdit) {
                        ProductForm(
                            state = stateProduct,
//                            currentStock = 0,
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
                            onAddCategoryClick = {
                                navController.navigate(AppScreens.AddCategoryScreen.route)
                            },
                            isInventory = true
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        CustomizedFilledCard(onClick = {}) {
                            CustomizedEditRows(
                                onCancel = { navController.popBackStack() },
                                onDelete = { viewModel.deleteProduct() },
                                onAction = {
                                    viewModel.updateProduct()
                                },
                                isEdit = true,
                                label = stringResource(R.string.on_action_product)
                            )
                        }
                    } else {
                        CustomizedListOfEditables(
                            listProducts,
                            modifier = Modifier,
                            label = { it.nameProduct },
                            onItemClick = { navController.navigate(route = "${AppScreens.InventoryScreen.route}?productId=${it.idProduct}") }
                        )
                    }
                }
            }
        }
    }
}