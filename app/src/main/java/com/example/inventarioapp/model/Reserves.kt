package com.example.inventarioapp.model

import com.google.firebase.Timestamp
import java.util.Date

data class Reserves(
    val idReserves: String = "",
    val idClient: String = "",
    val idProduct: String = "",
    val reservedAt: Timestamp? = null,
    val endReserve: Date? = null,
    val priceAtReserve: Double = 0.0,
    val qtyReserve: Int = 0,
    val originalQty: Int = 0,
    val amount: Double = 0.0,
    val isFinalized: Boolean = false
)
