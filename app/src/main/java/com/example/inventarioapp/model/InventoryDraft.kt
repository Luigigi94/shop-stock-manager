package com.example.inventarioapp.model

data class InventoryDraft(
    val items: List<InventoryCountItem> = emptyList()
)
