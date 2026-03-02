package com.example.inventarioapp.state

data class SupplierUiState(
    val isLoading: Boolean = false,
    val isEdit: Boolean = false,
    val errorMessage: String? = null,
    val success: Boolean = false,

    val idSupplier: String = "",
    val name: String = "",
    val phone: String = "",
    val identifierAccount: String = "",
    val idBank: String = "",

    val nameError: Int? = null,
    val phoneError: Int? = null,
    val identifierAccountError: Int? = null,
    val idBankError: Int? = null,

    val nameTouched: Boolean = false,
    val phoneTouched: Boolean = false,
    val identifierAccountTouched: Boolean = false,
    val idBankTouched: Boolean = false,

    val isValid: Boolean = false
)