package com.example.inventarioapp.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun CustomizedModal(
    show: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    title: String,
    content: @Composable () -> Unit // Aquí metes tu formulario
) {
    if (show) {
        AlertDialog(
            onDismissRequest = { onDismiss() }, // Se activa al tocar fuera o dar atrás
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
            },
            title = {
                Text(text = title, style = MaterialTheme.typography.titleLarge)
            },
            text = {
                // Aquí es donde ocurre la magia: inyectas lo que quieras
                content()
            },
            shape = RoundedCornerShape(28.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}