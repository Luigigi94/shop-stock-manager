package com.example.inventarioapp.model

import com.google.firebase.Timestamp

data class Cart(
    val id: String = "",
    val userId: String = "",
    val clientId: String? = null,
    val clientName: String? = null,
    val items: List<PurchaseItem> = emptyList(),
    val total: Double = 0.0,
    val updatedAt: Timestamp? = null,
    val createdAt: Timestamp? = null,
)