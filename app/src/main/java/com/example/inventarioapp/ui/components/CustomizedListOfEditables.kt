package com.example.inventarioapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun <T> CustomizedListOfEditables(
    list: List<T>,
    modifier: Modifier,
    label: (T) -> String,
    onItemClick: (T) -> Unit
){
    CustomizedFilledCard(
        modifier = modifier
    ) {
        LazyColumn {
            items(list) { edited ->
                CustomizedOutlinedCard(
                    onClick = {onItemClick(edited)}
                ) {
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        Text(text = label(edited))
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Editing Product",
                        )
                    }
                }
            }
        }
    }
}