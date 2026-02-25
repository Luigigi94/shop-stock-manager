package com.example.inventarioapp.validators

import com.example.inventarioapp.R
import com.example.inventarioapp.validators.model.ValidationResult

object ClientValidator {
    fun name(value: String): ValidationResult =
        if (value.isBlank())
            ValidationResult.Invalid(R.string.error_required)
        else
            ValidationResult.Valid

    fun apeP(value: String): ValidationResult =
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
}