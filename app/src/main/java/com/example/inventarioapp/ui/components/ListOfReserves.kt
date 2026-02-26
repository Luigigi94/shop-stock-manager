package com.example.inventarioapp.ui.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.inventarioapp.model.Reserves
import com.example.inventarioapp.viewmodel.ReserveViewModel

@Composable
fun ListOfReserves(
    modifier: Modifier = Modifier,
    list: List<Reserves>,
    reserveVM: ReserveViewModel,
    onClick: (Reserves) -> Unit
) {
    LazyColumn {
        items(list) { edit ->
            CustomizedFilledCard(
                modifier = modifier,
                onClick = { onClick(edit) }
            ) {
                val client = reserveVM.getClientName(edit.idClient)
                val product = reserveVM.getProductName(edit.idProduct)
                ListItem(
                    headlineContent = { Text(text = product) },
                    overlineContent = { Text(text = client) },
                    supportingContent = { Text(text = "$${edit.amount}") },
                )
            }
        }
    }
}