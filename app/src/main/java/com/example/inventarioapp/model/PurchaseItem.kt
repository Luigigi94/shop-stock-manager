package com.example.inventarioapp.model

data class PurchaseItem(
    val id: String = "",
    val productId: String = "",
    val productName: String = "",
    val price: Double = 0.0,
    val quantity: Int = 0,
    val subtotal: Double = 0.0
)