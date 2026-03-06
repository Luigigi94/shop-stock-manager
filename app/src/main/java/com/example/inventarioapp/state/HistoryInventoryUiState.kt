package com.example.inventarioapp.state

import com.example.inventarioapp.model.InventoryDetail

data class HistoryInventoryUiState(
    val products: List<InventoryDetail> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)