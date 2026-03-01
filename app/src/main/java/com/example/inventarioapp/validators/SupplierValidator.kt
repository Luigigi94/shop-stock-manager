package com.example.inventarioapp.validators

import com.example.inventarioapp.R
import com.example.inventarioapp.validators.model.ValidationResult

object SupplierValidator {
    fun name(value: String): ValidationResult =
        if (value.isBlank())
            ValidationResult.Invalid(R.string.error_required)
        else
            ValidationResult.Valid

    fun telephone(value: String): ValidationResult =
        when {
            value.isBlank() ->
                ValidationResult.Invalid(R.string.error_required)

            !value.matches(Regex("^\\d{10}$")) ->
                ValidationResult.Invalid(R.string.error_long_phone)

            else ->
                ValidationResult.Valid
        }

    fun identifierAccount(value: String): ValidationResult {
        if (!value.isBlank()) return ValidationResult.Valid

        return when (value.length) {
            16, 18 -> ValidationResult.Valid
            else -> ValidationResult.Invalid(R.string.error_long_identifier_account)
        }
    }

    fun idBank(value: String): ValidationResult =
        if (value.isBlank())
            ValidationResult.Invalid(R.string.error_required)
        else
            ValidationResult.Valid
}