package com.example.inventarioapp.validators.model

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(
        val errorResId: Int,
        val args: List<Any> = emptyList()
    ) : ValidationResult()
}