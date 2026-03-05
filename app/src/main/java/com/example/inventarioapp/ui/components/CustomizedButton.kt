package com.example.inventarioapp.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.Color

@Composable
fun CustomizedButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    shape: Shape = ButtonDefaults.shape,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    isEnabled: Boolean = true,
    content: @Composable () -> Unit,
){
    Button(
        modifier = modifier,
        onClick = onClick,
        shape = shape,
        colors = colors,
        enabled = isEnabled
    ) {
        content()
    }
}