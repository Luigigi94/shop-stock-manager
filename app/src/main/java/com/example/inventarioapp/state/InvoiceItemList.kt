package com.example.inventarioapp.state

data class InvoiceItemList(
    val name: String,
    val price: Double,
    val quantity: Int
) {
    val subtotal: Double
        get() = price * quantity
}
