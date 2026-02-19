package com.example.inventarioapp.validators

import com.example.inventarioapp.validators.model.ValidationResult

object ClientValidator {
    fun name(value: String): ValidationResult =
        if (value.isBlank())
            ValidationResult.Invalid("Campo obligatorio")
        else
            ValidationResult.Valid

    fun apeP(value: String): ValidationResult =
        if (value.isBlank())
            ValidationResult.Invalid("Campo obligatorio")
        else
            ValidationResult.Valid

    fun telephone(value: String): ValidationResult =
        when {
            value.isBlank() ->
                ValidationResult.Invalid("Campo obligatorio")

            !value.matches(Regex("^\\d{10}$")) ->
                ValidationResult.Invalid("Debe tener 10 dígitos")

            else ->
                ValidationResult.Valid
        }
}