package com.example.inventarioapp.model

import com.google.firebase.Timestamp

data class InventoryDetail(
    val id: String = "",
    val referenceId: String = "",
    val productId: String = "",
    val productName: String = "",
    val systemQuantity: Int = 0,
    val countedQuantity: Int = 0,
    val difference: Int = 0,
    val timestamp: Timestamp? = null
)