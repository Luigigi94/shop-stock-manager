package com.example.inventarioapp.ui.utils

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager

/**
 * Oculta el teclado cuando el usuario toca fuera de un TextField.
 *
 * Uso:
 * Column(modifier = Modifier.hideKeyboardOnTap())
 */
fun Modifier.hideKeyboardOnTap(): Modifier = composed {
    val focusManager = LocalFocusManager.current

    pointerInput(Unit) {
        detectTapGestures {
            focusManager.clearFocus()
        }
    }
}