package com.example.inventarioapp.model

import com.google.firebase.Timestamp

data class Purchase(
    val id: String = "",
    val clientId: String? = null,
    val clientName: String = "Anónimo",
    val items: List<PurchaseItem> = emptyList(),
    val total: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis(),
    val userId: String = ""
)