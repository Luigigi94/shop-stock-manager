package com.example.inventarioapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.inventarioapp.model.PurchaseItem
import com.example.inventarioapp.navigation.AppScreens
import com.example.inventarioapp.state.PurchaseItemList

@Composable
fun CustomizedListedPurchaseItems(
    navController: NavController,
    items: List<PurchaseItem>
) {
    CustomizedElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        enabled = true,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
//        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimaryContainer)
    ) {
        LazyColumn {
            items(items) { item ->
                CustomizedFilledCard(
                    onClick = {
                        navController.navigate(route = "${AppScreens.PurchaseProductScreen.route}")
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Column {
                        Text(text = "${item.productName} x ${item.quantity}")
                        Text(text = "${item.subtotal}")
                    }
                }
            }
        }
    }
}