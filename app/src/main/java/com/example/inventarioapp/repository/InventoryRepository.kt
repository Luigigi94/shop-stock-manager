package com.example.inventarioapp.repository

import android.util.Log
import com.example.inventarioapp.constants.FirestorePaths
import com.example.inventarioapp.model.InventoryCountItem
import com.example.inventarioapp.model.InventoryDetail
import com.example.inventarioapp.model.InventoryDraft
import com.example.inventarioapp.model.InventoryHeader
import com.example.inventarioapp.model.InventoryMovements
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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

    suspend fun saveInventoryDraft(inventoryId: String, items: List<InventoryCountItem>) {
        dbInventoryDraft
            .document(inventoryId)
            .set(mapOf("items" to items))
            .await()
    }

    suspend fun deleteDraft(userId: String) {
        dbInventoryDraft.document(userId).delete().await()
    }

    suspend fun getInventoryDraft(inventoryId: String): List<InventoryCountItem>? {
        return dbInventoryDraft
            .document(inventoryId)
            .get()
            .await()
            .toObject(InventoryDraft::class.java)?.items
    }

    suspend fun getDraftActive(): String? {
        return try {
            val snapshot = dbInventoryDraft
                .get()
                .await()

            // Si no está vacío, devolvemos el ID del primero (y único)
            if (!snapshot.isEmpty) {
                snapshot.documents[0].id
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
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

    suspend fun saveFinalInventoryRecord(
        header: InventoryHeader,
        details: List<InventoryDetail>
    ) {
        val batch = db.batch()

        val headerRef = dbInventoryList.document(header.idHeaderInventory)
        batch.set(headerRef, header)

        details.forEach { detail ->
            val detailRef = db.collection(FirestorePaths.Collections.INVENTORY_DETAILS).document(detail.id)
            batch.set(detailRef, detail)
        }

        batch.commit().await()
    }
}