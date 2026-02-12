package com.example.inventarioapp.model

data class PurchaseItem(
    val product: Products,
    val quantity: Int
){
    val subtotal: Double
        get() = product.priceProduct * quantity
}
