package com.example.inventarioapp.model

import com.google.firebase.Timestamp

data class InventoryCountItem(
    val idProduct: String = "",
    val productName: String = "",
    val systemQuantity: Int = 0,
    val countedQuantity: Int = 0,
    val updatedAt: Timestamp? = null
) {
    val difference: Int
        get() = countedQuantity - systemQuantity
}