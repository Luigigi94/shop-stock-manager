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
    val idCategory: String? = null,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null,

    val nameError: Int? = null,
    val quantityError: Int? = null,
    val priceError: Int? = null,
    val idCategoryError: Int? = null,

    val nameTouched: Boolean = false,
    val quantityTouched: Boolean = false,
    val priceTouched: Boolean = false,
    val idCategoryTouched: Boolean = false,

    val isValid: Boolean = false
)