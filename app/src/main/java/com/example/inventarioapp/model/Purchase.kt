package com.example.inventarioapp.model

data class Purchase(
    val clients: Clients,
    val items: List<PurchaseItem>
)
