package com.example.inventarioapp.model

import com.google.firebase.Timestamp

data class SupplierPurchase(
    val id: String = "",
    val supplierId: String = "",
    val supplierName: String = "",
    val items: List<SupplierPurchaseItem> = emptyList(),
    val totalCost: Double = 0.0,
    val createdAt: Timestamp? = null,
    val userId: String = ""
)