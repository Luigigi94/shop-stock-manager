package com.example.inventarioapp.model

import android.media.Image

data class Items(
    val name: String,
    val quantity: Int,
    val imageItem: Image?,
    val description: String?
)
