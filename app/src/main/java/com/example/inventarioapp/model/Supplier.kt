package com.example.inventarioapp.model

import com.google.firebase.Timestamp

data class Supplier(
    val id: String = "",
    val name: String = "",
    val phone: String = "",
    val identifierAccount: String = "",
    val idBank: String = "",
    val createdAt: Timestamp? = null
)