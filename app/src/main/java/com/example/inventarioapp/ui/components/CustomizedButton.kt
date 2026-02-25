package com.example.inventarioapp.ui.components

import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CustomizedButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable () -> Unit
){
    Button(
        modifier = modifier,
        onClick = onClick,
    ) {
        content()
    }
}