package com.example.inventarioapp.model

data class Cart(
    val id: String = "",
    val userId: String = "",
    val clientId: String? = null,
    val clientName: String? = null,
    val items: List<PurchaseItem> = emptyList(),
    val total: Double = 0.0,
    val updatedAt: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis(),
)