package com.example.inventarioapp.state

data class InventoryUiState (
    val isEdit: Boolean = false,

    val idProduct: String = "",
    val productName: String = "",
    val systemQuantity: Int = 0,
    val countedQuantity: String = "",

    val qtyError: Int? = null,
    val qtyTouched: Boolean = false,
    val isValid: Boolean = false,
    val isLoading: Boolean = false
)