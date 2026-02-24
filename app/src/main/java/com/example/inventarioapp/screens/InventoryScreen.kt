package com.example.inventarioapp.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.inventarioapp.R
import com.example.inventarioapp.ui.LocalSessionViewModel
import com.example.inventarioapp.ui.components.CustomizedButton
import com.example.inventarioapp.ui.components.CustomizedFilledCard
import com.example.inventarioapp.ui.components.CustomizedListOfEditables
import com.example.inventarioapp.ui.components.CustomizedTopAppBar
import com.example.inventarioapp.ui.forms.InventoryForm
import com.example.inventarioapp.viewmodel.InventoryViewModel

@Composable
fun InventoryScreen(
    darkThemeState: MutableState<Boolean>,
    navController: NavController,
//    productId: String?,
//    productViewModel: ProductViewModel = viewModel(),
) {
    val sessionViewModel = LocalSessionViewModel.current
    val session by sessionViewModel.session.collectAsState()

    val inventoryViewModel: InventoryViewModel = viewModel()
    val inventoryItems by inventoryViewModel.items.collectAsState()

    var selectedProductId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(session) {
        val name = session?.userName

        if (!name.isNullOrBlank()){
            inventoryViewModel.loadInventory(name)
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
            if (selectedProductId == null) {
                BottomAppBar {
                    CustomizedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            inventoryViewModel.confirmInventory(session?.userName ?: "Admin")
                        }
                    ) {
                        Text("Finalizar Inventario")
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            val currentItem = inventoryItems.find { it.idProduct == selectedProductId}
            CustomizedFilledCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = null
            ) {
                Column {
                    if (currentItem != null){
                        InventoryForm(
                            item = currentItem,
                            onQtyChange = { newQty ->
                                val qty = newQty.toIntOrNull() ?: 0
                                Log.w("InventoryFlow", "Validanding values $qty")
                                inventoryViewModel.updateCount(currentItem.idProduct, qty)
                            },
                            onBack = { selectedProductId = null }
                        )
                    } else {
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
}