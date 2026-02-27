package com.example.inventarioapp.state

import com.example.inventarioapp.model.Clients
import com.example.inventarioapp.model.Products
import com.example.inventarioapp.validators.model.ValidationResult
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
    var qtyReserve: Int = 0,
    val amount: Double = 0.0,
    val lastAmount: Double = 0.0,
    val priceAtReserve: String ="",

    val idClientError: Int? = null,
    val idProductError: Int? =  null,
    val endReserveError: Int? = null,
    val qtyReserveError: Int? = null,
    val amountError: ValidationResult.Invalid? = null,
//    val amountError: Int? = null,
    val idClientTouched: Boolean = false,
    val idProductTouched: Boolean = false,
    val endReserveTouched: Boolean = false,
    val amountTouched: Boolean = false,
    val qtyReserveTouched: Boolean = false,


    val product: Products? = null,
    val client: Clients? = null,
    val isValid: Boolean = false
)