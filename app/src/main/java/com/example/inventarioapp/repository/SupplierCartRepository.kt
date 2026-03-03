package com.example.inventarioapp.repository

import android.util.Log
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

    suspend fun saveSupplierCart(supplierPurchase: SupplierPurchase, userId: String) {
        val doc = supplierCart.document(userId)
        doc.set(supplierPurchase.copy(updatedAt = Timestamp.now()))
    }

    fun observeSupplierCart(userId: String): Flow<SupplierPurchase?> = callbackFlow {
        Log.d("SupplierCartRepo","userId: $userId")
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