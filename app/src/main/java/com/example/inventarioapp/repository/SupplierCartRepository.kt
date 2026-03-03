package com.example.inventarioapp.repository

import com.example.inventarioapp.constants.FirestorePaths
import com.example.inventarioapp.model.SupplierPurchase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class SupplierCartRepository {

    private val db by lazy {
        FirebaseFirestore.getInstance()
    }

    private val supplierCart = db.collection(FirestorePaths.Collections.SUPPLIER_CART)

    suspend fun saveSupplierCart(supplierPurchase: SupplierPurchase) {
        val supplierId = supplierPurchase.supplierId ?: return
        val doc = supplierCart.document(supplierId)
        doc.set(supplierPurchase.copy(updatedAt = Timestamp.now()))
    }

    fun observeSupplierCart(userId: String): Flow<SupplierPurchase?> = callbackFlow {
        val sub = supplierCart.document(userId)
            .addSnapshotListener { snap, _ ->
                trySend(snap?.toObject(SupplierPurchase::class.java))
            }
        awaitClose { sub.remove() }
    }

    suspend fun clearSupplierCart(userId: String){
        supplierCart.document(userId).delete().await()
    }
}