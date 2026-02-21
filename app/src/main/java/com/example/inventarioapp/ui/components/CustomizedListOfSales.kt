package com.example.inventarioapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.inventarioapp.model.Purchase

@Composable
fun CustomizedListOfSales(
    modifier: Modifier,
    list: List<Purchase>,
    onItemClick: () -> Unit
){
    CustomizedFilledCard(
        modifier = modifier,
    ) {
        LazyColumn {
            items(list) { purchases ->

                val nameShow = if (purchases.clientName != "null"){
                    purchases.clientName
                } else {
                    "Anonimo"
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CustomizedOutlinedCard(
                        onClick = {onItemClick()}
                    ) {
                        ListItem(
                            supportingContent = { Text(nameShow) },
                            headlineContent = { Text("$${purchases.total}") },
                            trailingContent = { Text(purchases.createdAt.toString()) },
                        )
                    }
                }
            }
        }
    }
}