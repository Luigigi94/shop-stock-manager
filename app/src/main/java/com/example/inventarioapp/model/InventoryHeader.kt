package com.example.inventarioapp.model

import com.google.firebase.Timestamp

data class InventoryHeader(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val createdAt: Timestamp? = null,
    val finishedAt: Timestamp? = null,
    val totalItemsCounted: Int = 0,
    val totalDiscrepancies: Int = 0,
    val notes: String = ""
)