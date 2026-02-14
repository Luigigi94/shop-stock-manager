package com.example.inventarioapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.inventarioapp.model.PurchaseItem

@Composable
fun CustomizedListedPurchaseItems(
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
                    onClick = {}, modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Column {
                        Text(text = "${item.product.nameProduct} x ${item.quantity}")
                        Text(text = "${item.subtotal}")
                    }
                }
            }
        }
    }
}