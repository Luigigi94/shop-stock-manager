package com.example.inventarioapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.inventarioapp.model.Reserves
import com.example.inventarioapp.viewmodel.ReserveViewModel

@Composable
fun ListOfReserves(
    modifier: Modifier = Modifier,
    list: List<Reserves>,
    reserveVM: ReserveViewModel,
    onClick: (Reserves) -> Unit
) {
    list.forEach { item ->
        val client = reserveVM.getClientName(item.idClient)
        val product = reserveVM.getProductName(item.idProduct)
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onClick(item) },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            )
        ) {
            ListItem(
                headlineContent = { Text(text = product) },
                overlineContent = { Text(text = client) },
                supportingContent = { Text(text = "$${item.amount}") },
            )
        }
    }
}