package com.example.inventarioapp.repository

import com.example.inventarioapp.model.PurchaseItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class PurchaseRepository {
    private val db by lazy {
        FirebaseFirestore.getInstance()
    }

    fun getPurchaseList(): Flow<List<PurchaseItem>>{
        return callbackFlow {
            var listener = db.collection("PurchaseList").addSnapshotListener { snapshots, exception ->
                if (exception != null || snapshots == null) return@addSnapshotListener

                val list = snapshots.documents.mapNotNull { documentSnapshot ->
                    documentSnapshot.toObject(PurchaseItem::class.java)
                }

                trySend(list)
            }
            awaitClose { listener.remove() }
        }
    }

//    suspend fun addPurchase(purchaseItem: PurchaseItem): Result<Unit>{
//        return try {
//            db.collection("PurchaseList")
//                .document(purchaseItem.)
//        }catch (e: Exception){
//            Result.failure(e)
//        }
//    }
}