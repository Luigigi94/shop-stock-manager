package com.example.inventarioapp.repository

import android.util.Log
import com.example.inventarioapp.constants.FirestorePaths
import com.example.inventarioapp.model.Cart
import com.example.inventarioapp.model.InventoryMovements
import com.example.inventarioapp.model.Purchase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class PurchaseRepository {

    private val db by lazy {
        FirebaseFirestore.getInstance()
    }

    private val purchases = db.collection("Purchases")


    /* ---------- Purchases ---------- */

    fun observePurchase(purchaseId: String): Flow<Purchase?> = callbackFlow {
        val sub = purchases.document(purchaseId)
            .addSnapshotListener { snap, _ ->
                trySend(snap?.toObject(Purchase::class.java))
            }

        awaitClose { sub.remove() }
    }

    fun getPurchasesByUser(userId: String): Flow<List<Purchase>> = callbackFlow{
        val sub = purchases
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snap, _ ->
                val list = snap?.documents
                    ?.mapNotNull { doc ->
                        doc.toObject(Purchase::class.java)?.copy(id = doc.id)
                    }
                    ?: emptyList()
                trySend(list)
            }
        awaitClose { sub.remove() }
    }

    suspend fun confirmPurchase(
        purchase: Purchase,
        movements: List<InventoryMovements>,
        cart: Cart,
        userId: String
    ): String? {
        return try {
            val batch = db.batch()
            val purchaseRef = db.collection(FirestorePaths.Collections.PURCHASES).document(purchase.id)
            batch.set(purchaseRef, purchase)
            movements.forEach { movement ->
                val movRef = db.collection(FirestorePaths.Collections.INVENTORYMOVEMENTS).document()
                batch.set(movRef, movement)
            }

            cart.items.forEach { item ->
                val productRef = db.collection(FirestorePaths.Collections.PRODUCTS).document(item.productId)
                batch.update(productRef, "stock", FieldValue.increment(-item.quantity.toLong()))
            }
            val cartRef = db.collection(FirestorePaths.Collections.CARTS).document(userId)
            batch.delete(cartRef)

            batch.commit().await()

            purchase.id
        } catch (e: Exception) {
            Log.e("PurchaseVM", "Venta falló completa", e)
            null
        }
    }
}