package com.example.inventarioapp.state

import com.example.inventarioapp.model.Clients
import com.example.inventarioapp.model.Products
import com.example.inventarioapp.model.Reserves

data class ReserveUiItem(
    val reserve: Reserves,
    val client: Clients?,
    val product: Products?
)