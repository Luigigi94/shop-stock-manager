package com.example.inventarioapp.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle

@Composable
fun CustomizedOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    error: Int? = null,
    errorArgs: List<Any>? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    label: @Composable () -> Unit,
    readOnly: Boolean? = false,
    onFocusLost: (() -> Unit)? = null,
    textStyle: TextStyle = LocalTextStyle.current
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
            error?.let { id ->
                val errorMessage = if (errorArgs != null) {
                    stringResource(id = id, *errorArgs.toTypedArray())
                } else {
                    stringResource(id = id)
                }
                Text(text = errorMessage)
            }
        },
        textStyle = textStyle
    )
}