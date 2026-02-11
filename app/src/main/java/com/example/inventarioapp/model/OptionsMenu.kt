package com.example.inventarioapp.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

data class OptionsMenu(
    @StringRes val label: Int,
    val icon: ImageVector?,
    val route: String,
    val role: String?,
    val state: Boolean,
    val contentDescription: String
)