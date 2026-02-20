package com.example.inventarioapp.state

import com.example.inventarioapp.model.Products

data class PurchaseItemList(
    val product: Products,
    val quantity: Int
) {
    val subtotal: Double
        get() = product.priceProduct * quantity
}
