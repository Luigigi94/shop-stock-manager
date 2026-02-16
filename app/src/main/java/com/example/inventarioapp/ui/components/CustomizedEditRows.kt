package com.example.inventarioapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.inventarioapp.R

@Composable
fun CustomizedEditRows(
    onCancel: () -> Unit,
    onDelete: () -> Unit,
    onAction: () -> Unit,
    isEdit: Boolean,
    label: String
) {
    CustomizedFilledCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = {}
    ) {
        Column {
            if (isEdit) {
                Row {
                    CustomizedButton(
                        modifier = Modifier.weight(1f),
                        onClick = onCancel,
                        content = {
                            Row {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = stringResource(R.string.menu_button_back)
                                )
                            }
                        }
                    )
                    CustomizedButton(
                        modifier = Modifier.weight(1f),
                        onClick = onDelete,
                        content = {
                            Row {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = stringResource(R.string.button_delete)
                                )
                            }
                        }
                    )
                }
            }
            CustomizedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onAction,
                content = {
                    Row {
                        if (isEdit) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = stringResource(R.string.button_edit_client)
                            )
                            Text(
                                text = "Modificar $label"
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.Save,
                                contentDescription = stringResource(R.string.button_edit_client)
                            )
                            Text(
                                text = "Crear $label"
                            )
                        }
                    }
                }
            )

        }
    }
}