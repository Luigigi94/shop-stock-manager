package com.example.inventarioapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*

import androidx.compose.ui.Modifier

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
    supportingText: String? = null
) {

    // Estado interno SOLO de UI (expandido/cerrado)
    var expanded by remember { mutableStateOf(false) }

    // Texto que se muestra en el campo
    val selectedText = selectedItem?.let(itemLabel) ?: ""

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
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
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            singleLine = true,
            isError = isError
        )

        /**
         * Lista desplegable
         */
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
        if (isError && supportingText != null) {
            Text(
                text = supportingText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}