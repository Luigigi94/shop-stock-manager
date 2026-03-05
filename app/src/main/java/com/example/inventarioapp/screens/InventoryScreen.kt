package com.example.inventarioapp.screens

import android.util.Log
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
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.example.inventarioapp.model.InventoryResult
import com.example.inventarioapp.navigation.AppScreens
import com.example.inventarioapp.ui.LocalSessionViewModel
import com.example.inventarioapp.ui.components.CustomizedButton
import com.example.inventarioapp.ui.components.CustomizedEditRows
import com.example.inventarioapp.ui.components.CustomizedExposedDropdownMenu
import com.example.inventarioapp.ui.components.CustomizedFAB
import com.example.inventarioapp.ui.components.CustomizedFilledCard
import com.example.inventarioapp.ui.components.CustomizedListOfEditables
import com.example.inventarioapp.ui.components.CustomizedOutlinedTextField
import com.example.inventarioapp.ui.components.CustomizedTopAppBar
import com.example.inventarioapp.ui.forms.InventoryForm
import com.example.inventarioapp.viewmodel.InventoryViewModel
import com.example.inventarioapp.viewmodel.ProductViewModel

@Composable
fun InventoryScreen(
    darkThemeState: MutableState<Boolean>,
    navController: NavController,
    inventoryId: String?
) {
    val sessionViewModel = LocalSessionViewModel.current
    val session by sessionViewModel.session.collectAsState()

    val inventoryViewModel: InventoryViewModel = viewModel()
    val inventoryItems by inventoryViewModel.itemsListed.collectAsState()

    val productViewModel: ProductViewModel = viewModel()
    val products by productViewModel.products.collectAsState()

    var selectedProductId by remember { mutableStateOf<String?>(null) }

    var addProductInventory by remember { mutableStateOf(false) }

    val stateInventoryProduct by inventoryViewModel.uiState.collectAsState()

    val confirmationResult by inventoryViewModel.confirmationResult.collectAsState()

    LaunchedEffect(inventoryId) {
        Log.d("INV -> LaunchedEffect InventoryScreen","Valor del queryParam: $inventoryId")

        inventoryId?.let { id ->
            inventoryViewModel.initViewModel(id)
        }
    }

    LaunchedEffect(confirmationResult) {
        when (confirmationResult){
            is InventoryResult.Success -> {
                navController.navigate(AppScreens.InventoryListScreen.route) {
                    popUpTo(AppScreens.MenuScreen.route) { inclusive = false }
                }
                inventoryViewModel.resetConfirmationResult()
            }
            is InventoryResult.Error -> {
                Log.e("UI_ERROR", (confirmationResult as InventoryResult.Error).message)
            }
            else -> Unit
        }
    }

    val selectedProduct = products.firstOrNull{
        it.idProduct == stateInventoryProduct.idProduct
    }

    Scaffold(
        topBar = {
            CustomizedTopAppBar(
                title = "${stringResource(R.string.topbar_Inventory)}/brrrr",
                darkThemeState = darkThemeState,
                navController = navController,
                showThemeSwitch = true,
                showBack = true
            )
        },
        floatingActionButton = {
            if(!stateInventoryProduct.isEdit) {
                CustomizedFAB(
                    onClick = { addProductInventory = !addProductInventory },
                    containerColor = if (addProductInventory) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = if (addProductInventory) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = null
                    )
                }
            }
        },
        bottomBar = {
            if (selectedProductId == null) {
                BottomAppBar {
                    CustomizedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            inventoryViewModel.confirmInventory(
                                inventoryId!!,
                                session?.userName ?: return@CustomizedButton
                            )
                        },
                        shape = CircleShape,
                        isEnabled = confirmationResult !is InventoryResult.Loading
                    ) {
                        Text("Finalizar Inventario")
                    }
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
                            text = if (stateInventoryProduct.isEdit) stringResource(R.string.inventory_label_product_edit) else stringResource(
                                R.string.inventory_label_product_add
                            ),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
            if (addProductInventory || stateInventoryProduct.isEdit){
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
                                text = if (stateInventoryProduct.isEdit) stringResource(R.string.generic_label_details) else stringResource(
                                    R.string.inventory_label_product
                                ),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            CustomizedExposedDropdownMenu(
                                items = products,
                                selectedItem = selectedProduct,
                                label = stringResource(R.string.inventory_label_product_inventory),
                                itemLabel = { it.nameProduct },
                                onItemSelected = { inventoryViewModel.onIdProduct(it.idProduct) },
                                modifier = Modifier.fillMaxWidth(),
                                /*isError = stateInventoryProduct.idCategoryError != null,
                                supportingText = state.idCategoryError*/
                            )

                            Spacer(Modifier.height(10.dp))

                            CustomizedOutlinedTextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = stateInventoryProduct.countedQuantity,
                                onValueChange = { inventoryViewModel.onQtyCounted(it) },
                                label = { Text(stringResource(R.string.inventory_label_product_quantity)) }
                            )
                            Spacer(modifier = Modifier.height(18.dp))
                            CustomizedEditRows(
                                onCancel = {
                                    if (addProductInventory) addProductInventory = false
                                    else navController.popBackStack()
                                },
                                onDelete = { /* Tu lógica de borrar */ },
                                onAction = {
                                    if (stateInventoryProduct.isEdit) {
                                        inventoryViewModel.updateCount(
                                            stateInventoryProduct.idProduct,
                                            stateInventoryProduct.countedQuantity.toIntOrNull() ?: 0
                                        )
                                        // Si es edición y vienes de otra pantalla, podrías hacer popBackStack
                                    } else {
                                        // AGREGAR NUEVO
                                        inventoryViewModel.addProductToInventory(stateInventoryProduct.idProduct)
                                        // Cerramos el formulario localmente como querías
                                        addProductInventory = false
                                    }
                                },
                                isEdit = stateInventoryProduct.isEdit,
                                label = if (stateInventoryProduct.isEdit) "Actualizar" else "Añadir al conteo"
                            )
                        }
                    }
                }
            }
            if (!stateInventoryProduct.isEdit && !addProductInventory){
                item {
                    CustomizedListOfEditables(
                        inventoryItems,
                        modifier = Modifier.fillMaxWidth(),
                        label = { it.productName },
                        onItemClick = { selectedItemFromList ->
                            selectedProductId = selectedItemFromList.idProduct
                        }
                    )
                }
            }
        }
    }
}