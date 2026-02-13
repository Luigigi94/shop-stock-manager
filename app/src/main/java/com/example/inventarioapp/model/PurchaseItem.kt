package com.example.inventarioapp.model

data class PurchaseItem(
    var product: Products = Products(),
    var quantity: Int = 0
) {
    val subtotal: Double
        get() = product.priceProduct * quantity
}
