package com.example.inventarioapp.repository

import com.example.inventarioapp.constants.FirestorePaths
import com.example.inventarioapp.model.InventoryCountItem
import com.example.inventarioapp.model.InventoryDraft
import com.example.inventarioapp.model.InventoryHeader
import com.example.inventarioapp.model.InventoryMovements
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class InventoryRepository {

    private val db by lazy {
        FirebaseFirestore.getInstance()
    }
    
    private val dbInventoryDraft = db.collection(FirestorePaths.Collections.INVENTORY_DRAFT)
    private val dbInventoryList = db.collection(FirestorePaths.Collections.INVENTORY_LIST)

    suspend fun saveInventoryMovements(movements: List<InventoryMovements>) {
        val batch = db.batch()
        movements.forEach { movement ->
            val doc = db.collection("InventoryMovements").document(movement.id)

            batch.set(doc, movement)
        }
        batch.commit().await()
    }

    suspend fun applyInventoryMovements(
        movements: List<InventoryMovements>,
        finalStocks: Map<String, Int>
    ) {
        db.runTransaction { transaction ->
            movements.forEach { movement ->
                val movementRef =
                    db.collection(FirestorePaths.Collections.INVENTORY_MOVEMENTS).document(movement.id)
                transaction.set(movementRef, movement)

                val productRef = db.collection(FirestorePaths.Collections.PRODUCTS)
                    .document(movement.productId)

                val newStock = finalStocks[movement.productId] ?: 0
                transaction.update(productRef, "stock", newStock.toLong())
            }
        }.await()
    }

    suspend fun saveInventoryDraft(userId: String, items: List<InventoryCountItem>) {
        dbInventoryDraft
            .document(userId)
            .set(mapOf("items" to items))
            .await()
    }

    suspend fun deleteDraft(userId: String) {
        dbInventoryDraft.document(userId).delete().await()
    }

    suspend fun getInventoryDraft(userId: String): List<InventoryCountItem>? {
        return dbInventoryDraft
            .document(userId)
            .get()
            .await()
            .toObject(InventoryDraft::class.java)?.items
    }
    
    suspend fun getListedInventories(): Flow<List<InventoryHeader>> {
        return callbackFlow {
            val listener = dbInventoryList.addSnapshotListener { snapshots, exception ->
                if (exception != null || snapshots == null) return@addSnapshotListener
                val list = snapshots.documents.mapNotNull { documentSnapshot ->
                    documentSnapshot.toObject(InventoryHeader::class.java)
                }
                trySend(list)
            }
            awaitClose { listener.remove() }
        }
    }

    suspend fun getMovementsByReference(idReference: String): List<InventoryMovements> {
        return try {
            db.collection(FirestorePaths.Collections.INVENTORY_MOVEMENTS)
                .whereEqualTo("referenceId", idReference)
//                .orderBy("createdAt", Query.Direction.ASCENDING)
                .get()
                .await()
                .toObjects(InventoryMovements::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}