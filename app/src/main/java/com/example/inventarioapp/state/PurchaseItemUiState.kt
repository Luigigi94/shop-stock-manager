package com.example.inventarioapp.state
data class PurchaseItemUiState(
    val isLoading: Boolean = false,
    val isEdit: Boolean = false,
    val success: Boolean = false,
    val idProduct: String = "",
    val idClient: String = "",
    val quantity: String = "",

    val idProductError: String? = null,
    val idProductTouched: Boolean = false,
    val quantityError: String? = null,
    val quantityTouched: Boolean = false,

    val isValid: Boolean = false
)