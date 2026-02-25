package com.example.inventarioapp.state

import com.google.firebase.Timestamp
import java.util.Date

data class ReserveUiState (
    val isLoading: Boolean = false,
    val isEdit: Boolean = false,
    val errorMessage: String? = null,
    val success: Boolean = false,

    val idReserve: String? = "",
    val idClient: String = "",
    val idProduct: String = "",
    val reservedAt: Timestamp? = null,
    val endReserve: Date? = null,
    val qtyReserve: Int = 0,
    val amount: Double = 0.0,

    val idClientError: Int? = null,
    val idProductError: Int? =  null,
    val endReserveError: Int? = null,
    val qtyReserveError: Int? = null,
    val amountError: Int? = null,
    val idClientTouched: Boolean = false,
    val idProductTouched: Boolean = false,
    val endReserveTouched: Boolean = false,
    val amountTouched: Boolean = false,
    val qtyReserveTouched: Boolean = false,

    val isValid: Boolean = false
)