package com.example.inventarioapp.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent

@Composable
fun CustomizedOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    label: @Composable () -> Unit,
    readOnly: Boolean? = false,
    onFocusLost: (() -> Unit)? = null
){
    var hasFocus by remember { mutableStateOf(false) }
    OutlinedTextField(
        modifier = modifier.onFocusEvent {
            if(hasFocus && !it.isFocused){
                onFocusLost?.invoke()
            }
            hasFocus = it.isFocused
        },
        onValueChange = onValueChange,
        value = value,
        label = label,
        readOnly = readOnly ?: false,
        isError = error != null,
        keyboardOptions = keyboardOptions,
        supportingText = {
            error?.let { Text(it) }
        }
    )
}