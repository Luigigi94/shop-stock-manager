package com.example.inventarioapp.model

import com.google.firebase.Timestamp

data class Products(
    val idProduct: String = "",
    val nameProduct: String = "",
    val imageProduct: String = "",
    val descriptionProduct: String = "",
    val priceProduct: Double = 0.0,
    val statusProduct: Boolean = false,
    val idCategory: String = "",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null,
)