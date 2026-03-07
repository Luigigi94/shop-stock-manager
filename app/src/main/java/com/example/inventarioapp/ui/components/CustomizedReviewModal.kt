package com.example.inventarioapp.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.inventarioapp.R

@Composable
fun CustomizedReviewModal(
    show: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    title: String,
    content: @Composable () -> Unit
){
    if (show){
        AlertDialog(
            onDismissRequest = { onDismiss() },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text(stringResource(R.string.generic_label_button_back))
                }
            },
            title = {
                Text(text = title, style = MaterialTheme.typography.titleLarge)
            },
            text = {
                content()
            },
            shape = RoundedCornerShape(28.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}