package com.example.inventarioapp.model

import com.example.inventarioapp.constants.MovementType
import com.google.firebase.Timestamp

data class InventoryMovements(
    val id: String = "",
    val productId: String = "",
    val quantity: Int = 0,
    val type: MovementType = MovementType.SALE,
    val reason: String = "",
    val referenceId: String = "",
    val userId: String = "",
    val createdAt: Timestamp? = null,
)
