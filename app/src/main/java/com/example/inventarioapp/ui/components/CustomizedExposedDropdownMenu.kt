package com.example.inventarioapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*

import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

/**
 * Dropdown genérico reutilizable estilo ComboBox.
 *
 * Se puede usar con Categories, Products o cualquier tipo T
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> CustomizedExposedDropdownMenu(
    items: List<T>,                     // lista de cualquier tipo
    selectedItem: T?,                   // item seleccionado
    label: String,                      // label del TextField
    itemLabel: (T) -> String,           // cómo convertir T → String visible
    onItemSelected: (T) -> Unit,        // callback cuando seleccionan
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    supportingText: Int? = null,
    isReadOnly: Boolean = false
) {

    // Estado interno SOLO de UI (expandido/cerrado)
    var expanded by remember { mutableStateOf(false) }

    // Texto que se muestra en el campo
    val selectedText = selectedItem?.let(itemLabel) ?: ""

    ExposedDropdownMenuBox(
        expanded = if (isReadOnly) false else expanded,
        onExpandedChange = { if (!isReadOnly) expanded = it },
        modifier = modifier.fillMaxWidth()
    ) {

        /**
         * Campo visual (solo lectura)
         */
        TextField(
            value = selectedText,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                if (!isReadOnly)
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
                .fillMaxWidth(),
            singleLine = true,
            isError = isError,
            enabled = !isReadOnly
        )

        /**
         * Lista desplegable
         */
        if (!isReadOnly) {
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {

                items.forEach { item ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = itemLabel(item),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        onClick = {
                            onItemSelected(item)
                            expanded = false
                        }
                    )
                }
            }
        }
        if (isError && supportingText != null) {
            Text(
                text = stringResource(id = supportingText),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}