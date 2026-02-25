package com.example.inventarioapp.state

import java.util.Date

data class ReserveUiState (
    val isLoading: Boolean = false,
    val isEdit: Boolean = false,
    val errorMessage: String? = null,
    val success: Boolean = false,

    val idClient: String = "",
    val idProducts: String = "",
    val endReserve: Date? = null,
    val amount: Double = 0.0,

    val idClientError: String? = null,
    val idProductError: String? =  null,
    val endReserveError: String? = null,
    val amountError: String? = null,
    val idClientTouched: String? = null,
    val idProductTouched: String? =  null,
    val endReserveTouched: String? = null,
    val amountTouched: String? = null,

    val isValid: Boolean = false
)