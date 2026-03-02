package com.example.inventarioapp.model

data class SupplierPurchaseItem(
    val id: String = "",
    val productId: String = "",
    val productName: String = "",
    val cost: Double = 0.0,
    val quantity: Int = 0,
    val subtotal: Double = 0.0
)