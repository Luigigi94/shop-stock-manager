package com.example.inventarioapp.model

data class Cart(
    val id: String = "",                        // ID del carrito (podría ser igual a userId)
    val userId: String = "",                     // usuario dueño del carrito
    val items: List<PurchaseItem> = emptyList(),// items del carrito
    val total: Double = 0.0,                     // total calculado
    val updatedAt: Long = System.currentTimeMillis(), // última actualización
    val createdAt: Long = System.currentTimeMillis()  // creación
)