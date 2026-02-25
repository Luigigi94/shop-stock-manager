package com.example.inventarioapp.ui.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.inventarioapp.model.Clients
import com.example.inventarioapp.model.Products
import com.example.inventarioapp.model.Reserves

@Composable
fun ListOfReserves(
    modifier: Modifier = Modifier,
    list: List<Reserves>,
    client: Clients,
    products: Products,
    text: String,
    isChecked: Boolean,
    onClick: () -> Unit = {},
){
    CustomizedFilledCard(
        modifier = modifier
    ) {
        LazyColumn {
            items(list){edit ->
                ListItem(
                    headlineContent = { edit.}
                )
            }
        }
    }
}