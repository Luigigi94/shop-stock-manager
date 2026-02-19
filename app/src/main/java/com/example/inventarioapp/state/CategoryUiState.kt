package com.example.inventarioapp.state

data class CategoryUiState(
    val isLoading: Boolean = false,
    val isEdit: Boolean = false,
    val errorMessage: String? = null,
    val success: Boolean = false,

    val idCategory: String = "",
    val nameCategory: String = "",
    val descriptionCategory: String? = "",

    val nameError: String? = null,

    val nameTouched: Boolean = false,

    val isValid: Boolean = false
)