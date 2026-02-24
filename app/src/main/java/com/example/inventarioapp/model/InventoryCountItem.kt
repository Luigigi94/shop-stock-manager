package com.example.inventarioapp.model

data class InventoryCountItem(
    val idProduct: String = "",
    val productName: String = "",
    val systemQuantity: Int = 0,
    val countedQuantity: Int = 0
) {
    val difference: Int
        get() = countedQuantity - systemQuantity
}