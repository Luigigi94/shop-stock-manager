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
    val telephone : String = "",

    val nameError: Int? = null,
    val apePError: Int? = null,
    val telephoneError: Int? = null,

    val nameTouched: Boolean = false,
    val apePTouched: Boolean = false,
    val telephoneTouched: Boolean = false,

    val isValid: Boolean = false
)
