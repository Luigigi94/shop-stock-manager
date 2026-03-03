package com.example.inventarioapp.repository

import android.util.Log
import com.example.inventarioapp.constants.FirestorePaths
import com.example.inventarioapp.constants.MovementType
import com.example.inventarioapp.model.InventoryMovements
import com.example.inventarioapp.model.SupplierPurchase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlin.getValue


class SupplierPurchaseRepository {
    private val db by lazy { FirebaseFirestore.getInstance() }

    private val purchases =
        db.collection(FirestorePaths.Collections.SUPPLIER_PURCHASES)

    suspend fun registerSupplierPurchase(
        purchase: SupplierPurchase
    ): String? {

        return try {

            val batch = db.batch()
            val purchaseRef = purchases.document(purchase.id)
            batch.set(purchaseRef, purchase)

            purchase.items.forEach { item ->
                val movement = InventoryMovements(
                    productId = item.productId,
                    quantity = item.quantity, // 👈 positivo
                    type = MovementType.PURCHASE,
                    referenceId = purchase.id,
                    createdAt = purchase.createdAt,
                    userId = purchase.userId
                )

                val movRef = db
                    .collection(FirestorePaths.Collections.INVENTORY_MOVEMENTS)
                    .document()

                batch.set(movRef, movement)

                val productRef = db
                    .collection(FirestorePaths.Collections.PRODUCTS)
                    .document(item.productId)

                batch.update(
                    productRef,
                    "stock",
                    FieldValue.increment(item.quantity.toLong())
                )
            }

            batch.commit().await()

            purchase.id

        } catch (e: Exception) {
            Log.e("SupplierPurchaseRepo", "Error registrando compra", e)
            null
        }
    }

}