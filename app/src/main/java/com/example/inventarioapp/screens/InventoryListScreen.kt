package com.example.inventarioapp.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Plumbing
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.inventarioapp.R
import com.example.inventarioapp.navigation.AppScreens
import com.example.inventarioapp.ui.components.CustomizedButton
import com.example.inventarioapp.ui.components.CustomizedListOfEditables
import com.example.inventarioapp.ui.components.CustomizedTopAppBar
import com.example.inventarioapp.viewmodel.InventoryViewModel

@Composable
fun InventoryListScreen(
    darkThemeState: MutableState<Boolean>,
    navController: NavController,
    inventoryViewModel: InventoryViewModel = viewModel()
){
    val listInventories by inventoryViewModel.listedInventories.collectAsState()

    val draftId by inventoryViewModel.activeDraftId.collectAsState()
    LaunchedEffect(Unit) {
        inventoryViewModel.fetchDraftId()
    }
    LaunchedEffect(draftId) {
//        navController.navigate("${AppScreens.InventoryScreen.route}?inventoryActive=${draftId.toString()}")
        draftId?.let { id ->
            navController.navigate("${AppScreens.InventoryScreen.route}?inventoryActive=$id")
            inventoryViewModel.clearActiveDraftId()
        }
    }
    Scaffold(
        topBar = {
            CustomizedTopAppBar(
                title = stringResource(R.string.topbar_inventories_listed),
                navController,
                darkThemeState,
                showThemeSwitch = true,
                showBack = true
            )
        }, bottomBar = {
            CustomizedButton(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    MaterialTheme.colorScheme.primary
                ),
                onClick = {
                    val newInv = "inv_${System.currentTimeMillis()}"
                    Log.d("INV -> onClick InventoryListScreen","Valor del queryparam: $newInv")
                    navController.navigate("${AppScreens.InventoryScreen.route}?inventoryActive=$newInv")
                }
            ) {
                Row {
                    Icon(
                        imageVector = Icons.Filled.Plumbing,
                        contentDescription = null
                    )
                    Text(
                        text = "Comenzar Nuevo Inventario",
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
                            text = stringResource(R.string.topbar_inventories_listed),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            item {
                CustomizedListOfEditables(
                    list = listInventories,
                    modifier = Modifier,
                    label = { it.userName },
                    onItemClick = {}
                )
            }
        }
    }
}