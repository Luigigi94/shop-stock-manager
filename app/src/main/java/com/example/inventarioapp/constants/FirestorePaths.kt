package com.example.inventarioapp.constants

object FirestorePaths {
    object Collections {
        const val CATEGORIES = "Categories"
        const val CLIENTS = "Clients"
        const val PRODUCTS = "Products"
        const val INVENTORYMOVEMENTS = "InventoryMovements"
        const val INVENTORY_DRAFT = "InventoryDrafts"
        const val PURCHASES = "Purchases"
        const val RESERVES = "Reserves"
        const val CARTS = "Carts"

        const val SUPPLIER_PURCHASES = "SupplierPurchases"
        const val SUPPLIER = "Suppliers"
    }

    object Documents {
        const val CURRENT_PURCHASE = "current_purchase"
    }
}