package com.example.inventarioapp.model

import android.media.Image

data class Products(
    val idProducts: Int,
    val nameProduct: String,
    val quantity: Int,
    val imageProduct: Image?,
    val descriptionProduct: String?
)
