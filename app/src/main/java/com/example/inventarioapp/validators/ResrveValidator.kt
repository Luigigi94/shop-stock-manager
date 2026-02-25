package com.example.inventarioapp.validators

import com.example.inventarioapp.validators.model.ValidationResult

object ResrveValidator {
    fun idClient(value: String?): ValidationResult{
        if (value == null)
            return ValidationResult.Invalid("")
    }
}