package com.example.inventarioapp.model

sealed class InventoryResult {
    object Idle : InventoryResult()
    object Loading : InventoryResult()
    object Success : InventoryResult()
    data class Error(val message: String) : InventoryResult()
}