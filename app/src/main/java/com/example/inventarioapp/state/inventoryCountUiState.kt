package com.example.inventarioapp.state

data class inventoryCountUiState(
    val productId: String = "",
    val productName: String = "",   // solo para mostrar en UI
    val countedQuantity: String = "", // String para TextField
    val userId: String = "",
    val notes: String = "",
    val isSaving: Boolean = false,
    val error: String? = null
)
