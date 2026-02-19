package com.example.inventarioapp.validators.model

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val message: String) : ValidationResult()
}