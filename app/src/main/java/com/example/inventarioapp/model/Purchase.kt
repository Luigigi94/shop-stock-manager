package com.example.inventarioapp.model

import com.google.firebase.Timestamp

data class Purchase(
    var idPurchase: String = "",
    var client: Clients? = null,
    var items: List<PurchaseItem> = emptyList(),
    var total: Double = 0.0,
    var confirmed: Boolean = false,
    var purchaseTimeStamp: Timestamp = Timestamp.now()
)