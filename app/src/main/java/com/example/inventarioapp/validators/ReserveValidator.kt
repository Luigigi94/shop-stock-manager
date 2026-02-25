package com.example.inventarioapp.validators

import com.example.inventarioapp.R
import com.example.inventarioapp.validators.model.ValidationResult
import java.util.Date

object ReserveValidator {
    fun idClient(value: String?): ValidationResult{
        if (value == null)
            return ValidationResult.Invalid(R.string.error_required)
        return ValidationResult.Valid
    }
    fun idProduct(value: String?): ValidationResult{
        if (value == null)
            return ValidationResult.Invalid(R.string.error_required)
        return ValidationResult.Valid
    }
    fun endReserve(value: Date?): ValidationResult{
        val today = Date()
        if (value == null)
            return ValidationResult.Invalid(R.string.error_date_required)

        if (value.before(today))
            return ValidationResult.Invalid(R.string.error_date_past)

        return ValidationResult.Valid
    }
    fun amount(value: String?, priceProduct: String): ValidationResult{
        val amountDouble = value?.toDoubleOrNull() ?: 0.0
        val priceProductDouble= priceProduct.toDouble()
        val minRequired = (priceProductDouble * 0.1)

        if (value == null)
            return ValidationResult.Invalid(R.string.error_required)

        if (amountDouble < minRequired){
            return ValidationResult.Invalid(R.string.error_min_amount_percentage, args = listOf("10%", minRequired.toString()))
        }
        return ValidationResult.Valid
    }
    fun qty(value: String?): ValidationResult{
        val qtyReserve = value?.toIntOrNull() ?: 0

        if (value == null)
            return ValidationResult.Invalid(R.string.error_required)

        if (qtyReserve < 1)
            return ValidationResult.Invalid(R.string.error_min_quantity)

        return ValidationResult.Valid
    }
}