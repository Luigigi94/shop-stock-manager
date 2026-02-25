package com.example.inventarioapp.validators

import com.example.inventarioapp.R
import com.example.inventarioapp.validators.model.ValidationResult

object CategoryValidator {
    fun name(value: String): ValidationResult =
        if (value.isBlank())
            ValidationResult.Invalid(R.string.error_required)
        else
            ValidationResult.Valid

}