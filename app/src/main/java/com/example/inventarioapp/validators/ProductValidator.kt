package com.example.inventarioapp.validators

import com.example.inventarioapp.validators.model.ValidationResult

object ProductValidator {
    fun name(value: String): ValidationResult =
        if (value.isBlank())
            ValidationResult.Invalid("Campo obligatorio")
        else
            ValidationResult.Valid

    fun quantity(value: String): ValidationResult {
        if (value.isBlank())
            return ValidationResult.Invalid("Campo obligatorio")

        val quantityInt = value.toIntOrNull()
            ?: return ValidationResult.Invalid("Solo números válidos")

        if (quantityInt < 1)
            return ValidationResult.Invalid("La cantidad no puede ser 0 o menor")

        return ValidationResult.Valid
    }

    fun price(value: String): ValidationResult{
        if (value.isBlank())
            return ValidationResult.Invalid("Campo Obligatorio")
        val priceInt = value.toDoubleOrNull()
            ?: return ValidationResult.Invalid("Solo números válidos")
        if (priceInt == 0.0)
            return ValidationResult.Invalid("La cantidad no puede ser 0 o menor")

        return ValidationResult.Valid
    }

    fun idCategory(value: String): ValidationResult{
        if (value.isBlank())
            return ValidationResult.Invalid("Campo Obligatorio")
        return ValidationResult.Valid
    }
}