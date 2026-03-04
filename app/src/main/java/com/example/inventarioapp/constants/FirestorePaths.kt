package com.example.inventarioapp.constants

object FirestorePaths {
    object Collections {
        const val CATEGORIES = "Categories"
        const val CLIENTS = "Clients"
        const val PRODUCTS = "Products"
        const val INVENTORY_MOVEMENTS = "InventoryMovements"
        const val INVENTORY_DRAFT = "InventoryDrafts"
        const val PURCHASES = "Purchases"
        const val RESERVES = "Reserves"
        const val CARTS = "Carts"
        const val SUPPLIER_PURCHASES = "SupplierPurchases"
        const val SUPPLIER = "Suppliers"
        const val SUPPLIER_CART = "SupplierCart"
        const val INVENTORY_LIST = "InventoryList"
    }

    object Documents {
        const val CURRENT_PURCHASE = "current_purchase"
    }
}