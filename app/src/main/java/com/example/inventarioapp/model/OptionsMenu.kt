package com.example.inventarioapp.model

import androidx.compose.ui.graphics.vector.ImageVector

data class OptionsMenu(
    val label: String,
    val icon: ImageVector?,
    val route: String,
    val role: String?,
    val state: Boolean,
    val contentDescription: String
)