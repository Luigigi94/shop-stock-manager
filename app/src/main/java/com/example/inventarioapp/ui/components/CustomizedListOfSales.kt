package com.example.inventarioapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.inventarioapp.R
import com.example.inventarioapp.model.Purchase
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun CustomizedListOfSales(
    modifier: Modifier,
    list: List<Purchase>,
    onItemClick: (Purchase) -> Unit
){
    /*CustomizedFilledCard(
        modifier = modifier,
    ) {*/
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            list.forEach { purchases ->
                val nameShow = if (purchases.clientName != "null"){
                    purchases.clientName
                } else {
                    stringResource(R.string.generic_label_anonymous_client)
                }

                val dateFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                val formattedDate = purchases.createdAt?.toDate()?.let { dateFormatter.format(it) } ?: ""

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onItemClick(purchases) },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    )
                ) {
                    ListItem(
                        headlineContent = { Text(text = nameShow) },
                        overlineContent = { Text(text = "$${purchases.total}") },
                        supportingContent = { Text(text = formattedDate) }
                    )
                }
            }
        }
        /*LazyColumn {
            items(list) { purchases ->

                val nameShow = if (purchases.clientName != "null"){
                    purchases.clientName
                } else {
                    stringResource(R.string.generic_label_anonymous_client)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CustomizedOutlinedCard(
                        onClick = {onItemClick()}
                    ) {
                        ListItem(
                            *//*supportingContent = { Text(nameShow) },
                            headlineContent = { Text("$${purchases.total}") },
                            trailingContent = { Text(purchases.createdAt.toString()) },*//*
                            headlineContent = { Text(text = nameShow) },
                            overlineContent = { Text(text = "$${purchases.total}") },
                            supportingContent = { Text(text = purchases.createdAt.toString()) }
                        )
                    }
                }
            }
        }*/
//    }
}