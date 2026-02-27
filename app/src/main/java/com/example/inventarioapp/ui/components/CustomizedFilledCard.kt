package com.example.inventarioapp.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
fun CustomizedFilledCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    colors: CardColors = CardDefaults.cardColors(),
    shape: Shape = CardDefaults.shape,
    content: @Composable () -> Unit
){
    if (onClick != null) {
        Card(
            colors = colors,
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            onClick = onClick
        ) {
            Box(
                modifier = Modifier.padding(16.dp)
            ){
                content()
            }
        }
    } else {
        Card(
            colors = colors,
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier.padding(16.dp)
            ){
                content()
            }
        }

    }
}