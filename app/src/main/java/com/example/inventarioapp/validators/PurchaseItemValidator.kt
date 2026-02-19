package com.example.inventarioapp.validators

import com.example.inventarioapp.validators.model.ValidationResult

object PurchaseItemValidator {
    fun idProduct(value: String?): ValidationResult {
        if (value == null)
            return ValidationResult.Invalid("Campo Obligatorio")
        return ValidationResult.Valid
    }

    fun quantity(value: String): ValidationResult{
        if (value.isBlank())
            return ValidationResult.Invalid("Campo Obligatorio")

        val quantityInt = value.toIntOrNull()
            ?: return ValidationResult.Invalid("Solo números válidos")
        if (quantityInt < 1)
            return ValidationResult.Invalid("La cantidad no puede ser 0 o menor")

        return ValidationResult.Valid
    }
}