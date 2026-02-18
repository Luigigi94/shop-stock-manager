package com.example.inventarioapp.state

import com.google.firebase.Timestamp

data class ProductUiState (
    val isLoading: Boolean = false,
    val isEdit: Boolean = false,
    val success: Boolean = false,
    val errorMessage: String? = "",

    val idProduct: String = "",
    val nameProduct: String = "",
    val quantityProduct: Int = 0,
    val imageProduct: String = "",
    val descriptionProduct: String = "",
    val priceProduct: Double = 0.0,
    val statusProduct: Boolean = false,
    val idCategory: String = "",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null,
)