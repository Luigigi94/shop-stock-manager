package com.example.inventarioapp.ui.components

import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CustomizedOutlinedTextField(
    value: String? = null,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable () -> Unit
){
    OutlinedTextField(
        modifier = modifier,
        onValueChange = onValueChange,
        value = value?: "",
        label = label
    )
}