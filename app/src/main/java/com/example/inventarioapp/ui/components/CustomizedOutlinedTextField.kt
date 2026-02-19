package com.example.inventarioapp.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged

@Composable
fun CustomizedOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    label: @Composable () -> Unit,
    onFocusLost: (() -> Unit)? = null
){
    OutlinedTextField(
        modifier = modifier.onFocusChanged {
            if(!it.isFocused){
                onFocusLost?.invoke()
            }
        },
        onValueChange = onValueChange,
        value = value,
        label = label,
        isError = error != null,
        keyboardOptions = keyboardOptions,
        supportingText = {
            error?.let { Text(it) }
        }
    )
}