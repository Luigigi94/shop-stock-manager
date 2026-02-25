package com.example.inventarioapp.validators

import com.example.inventarioapp.validators.model.ValidationResult
import com.example.inventarioapp.R

object PurchaseItemValidator {
    fun idProduct(value: String?): ValidationResult {
        if (value == null)
            return ValidationResult.Invalid(R.string.error_required)
        return ValidationResult.Valid
    }

    fun quantity(value: String): ValidationResult{
        if (value.isBlank())
            return ValidationResult.Invalid(R.string.error_required)

        val quantityInt = value.toIntOrNull()
            ?: return ValidationResult.Invalid(R.string.error_invalid_number)
        if (quantityInt < 1)
            return ValidationResult.Invalid(R.string.error_min_quantity)

        return ValidationResult.Valid
    }
}