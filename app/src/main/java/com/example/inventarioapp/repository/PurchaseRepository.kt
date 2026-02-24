package com.example.inventarioapp.repository

import android.util.Log
import com.example.inventarioapp.constants.MovementType
import com.example.inventarioapp.model.Clients
import com.example.inventarioapp.model.InventoryMovements
import com.example.inventarioapp.model.Products
import com.example.inventarioapp.model.Purchase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class PurchaseRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val purchases = db.collection("Purchases")


    /* ---------- Purchases ---------- */

    fun observePurchase(purchaseId: String): Flow<Purchase?> = callbackFlow {
        val sub = purchases.document(purchaseId)
            .addSnapshotListener { snap, _ ->
                trySend(snap?.toObject(Purchase::class.java))
            }

        awaitClose { sub.remove() }
    }

    suspend fun createPurchase(purchase: Purchase): String {
        val doc = purchases.document()
        doc.set(purchase.copy(id = doc.id)).await()
        return doc.id
    }

    suspend fun savePurchase(purchase: Purchase) {
        purchases.document(purchase.id).set(purchase).await()
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








//            .addSnapshotListener { snap, _ ->
//                val stock = snap?.documents
//                    ?.mapNotNull { it.getLong("quantity")?.toInt() }
//                    ?.sum() ?: 0
//                trySend(stock)
//            }
//        awaitClose { sub.remove() }

}