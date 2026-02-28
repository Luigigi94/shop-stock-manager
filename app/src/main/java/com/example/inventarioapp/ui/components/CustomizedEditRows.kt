package com.example.inventarioapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.inventarioapp.R

@Composable
fun CustomizedEditRows(
    onCancel: () -> Unit, onDelete: () -> Unit, onAction: () -> Unit, isEdit: Boolean, label: String
) {
    Column {
        if (isEdit) {
            Row {
                CustomizedButton(
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    onClick = onCancel
                ) {
                    Row {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(R.string.generic_label_button_back)
                        )
                    }
                }
                CustomizedButton(
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    onClick = onDelete
                ) {
                    Row {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = stringResource(R.string.generic_label_button_delete)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
        CustomizedButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            onClick = onAction
        ) {
            Row {
                Icon(
                    imageVector = if (isEdit) Icons.Filled.Edit else Icons.Filled.Save,
                    contentDescription = null
                )
                Text(
                    text = label,
                )
            }
        }
    }
}