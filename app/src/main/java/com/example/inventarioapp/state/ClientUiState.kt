package com.example.inventarioapp.state

data class ClientUiState(
    val isLoading: Boolean = false,
    val isEdit: Boolean = false,
    val errorMessage: String? = null,
    val success: Boolean = false,

    val idClient: String = "",
    val nameClient: String = "",
    val apePClient: String = "",
    val apeMClient: String = "",
    val telephone : String = ""
)
