package com.example.inventarioapp.repository

import android.util.Log
import com.example.inventarioapp.constants.FirestorePaths
import com.example.inventarioapp.model.Clients
import com.example.inventarioapp.model.Purchase
import com.example.inventarioapp.model.PurchaseItem
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class PurchaseRepository {
    private val db by lazy {
        FirebaseFirestore.getInstance()
    }
    private val purchaseCollection = db.collection(FirestorePaths.Collections.PURCHASES)
    private val currentPurchaseDoc = purchaseCollection.document(FirestorePaths.Documents.CURRENT_PURCHASE)

    fun getActivePurchaseItem(): Flow<List<PurchaseItem>> = callbackFlow {
        val listener = currentPurchaseDoc.addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null) return@addSnapshotListener
            val purchase = snapshot.toObject(Purchase::class.java)
            trySend(purchase?.items ?: emptyList())
        }
        awaitClose { listener.remove() }
    }


    suspend fun addPurchasedItem(
        client: Clients?,
        item: PurchaseItem,
        total: Double
    ): Result<Unit>{
        return try {
            db.runTransaction { transaction ->
                val snapshot = transaction.get(currentPurchaseDoc)

                if (snapshot.exists()){
                    val currentPurchase = snapshot.toObject(Purchase::class.java)
                    val items = currentPurchase?.items?.toMutableList() ?: mutableListOf()

                    items.add(item)
                    transaction.update(currentPurchaseDoc, "items", items)
                    transaction.update(currentPurchaseDoc, "total", total)
                    transaction.update(currentPurchaseDoc, "client", client ?: currentPurchase?.client)
                } else {
                    val newPurchase = Purchase(
                        client = client,
                        items = listOf(item),
                        total = total,
                        confirmed = false,
                        purchaseTimeStamp = Timestamp.now()
                    )
                    transaction.set(currentPurchaseDoc, newPurchase)
                }
            }.await()
            Result.success(Unit)
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    fun updatePurchaseItem(updateItem: PurchaseItem) {
//        val currentPurchaseDoc = db.collection(FirestorePaths.Collections.PURCHASES).document(FirestorePaths.Documents.CURRENT_PURCHASE)
        db.runTransaction { transaction ->
            val snapshot = transaction.get(currentPurchaseDoc)
            val purchase = snapshot.toObject(Purchase::class.java) ?: return@runTransaction
//            val updatedItems = purchase.items.map { if (it.product.idProduct == updateItem.product.idProduct) updateItem else it }

            val updateItems = purchase.items.map {
                if (it.idProduct == updateItem.idProduct) updateItem else it
            }

            transaction.update(currentPurchaseDoc, "items", updateItems)

//            transaction.update(currentPurchaseDoc, "items", updatedItems)
//            transaction.update(currentPurchaseDoc, "total", updatedItems.sumOf { it.subtotal })
        }
    }

    fun deletePurchaseItem(item: PurchaseItem) {
//        val currentPurchaseDoc = db.collection(FirestorePaths.Collections.PURCHASES).document(FirestorePaths.Documents.CURRENT_PURCHASE)
        db.runTransaction { transaction ->
            val snapshot = transaction.get(currentPurchaseDoc)
            val purchase = snapshot.toObject(Purchase::class.java) ?: return@runTransaction
//            val updatedItems = purchase.items.filter { it.product.idProduct != updateItem.product.idProduct }
//            val newTotal = if (updatedItems.isEmpty()) 0.0 else updatedItems.sumOf { it.subtotal }
//
//            transaction.update(currentPurchaseDoc, "items", updatedItems)
//            transaction.update(currentPurchaseDoc, "total", newTotal)

            val updatedItems = purchase.items.filter {
                it.idProduct != item.idProduct
            }

            transaction.update(currentPurchaseDoc, "items", updatedItems)
        }
    }

    /*fun confirmPurchase(){
        val currentPurchaseDoc = purchaseCollection.document(FirestorePaths.Documents.CURRENT_PURCHASE)

        currentPurchaseDoc.get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.exists()) {
                    Log.w("PurchaseRepo", "No hay compra activa")
                    return@addOnSuccessListener
                }

                val newPurchaseDoc = purchaseCollection.document()

                val data = snapshot.data ?: emptyMap<String, Any>()

                val confirmedData = data.toMutableMap().apply {
                    put("idPurchase", newPurchaseDoc.id)
                    put("purchaseTimeStamp", Timestamp.now())
                    put("confirmed", true)
                }

                newPurchaseDoc.set(confirmedData)
                    .addOnSuccessListener { Log.d("PurchaseRepo", "Compra Confirmada") }
                    .addOnFailureListener { e -> Log.e("PurchaseRepo", "Error en Compra: ", e) }

                currentPurchaseDoc.delete()
                    .addOnSuccessListener { Log.d("PurchaseRepo", "Compra Activa Eliminada") }
                    .addOnFailureListener { e -> Log.e("PurchaseRepo", "Error al eliminar la compra activa: ", e) }
            }
            .addOnFailureListener { e -> Log.e("PurchaseRepo", "Error en Compra: ", e) }
    }*/

    suspend fun confirmPurchase(): Purchase? {
        val snapshot = currentPurchaseDoc.get().await()

        if (!snapshot.exists()) return null

        val newPurchaseDoc = purchaseCollection.document()

        val purchase = snapshot.toObject(Purchase::class.java) ?: return null

        Log.d("PurchaseRepo", "ANTES CONFIRMAR -> $purchase")
        Log.d("PurchaseRepo", "CLIENT -> ${purchase.client}")

        val confirmedPurchase = purchase.copy(
            idPurchase = newPurchaseDoc.id,
            confirmed = true,
            purchaseTimeStamp = Timestamp.now()
        )

        newPurchaseDoc.set(confirmedPurchase).await()
        currentPurchaseDoc.delete().await()

        return confirmedPurchase
    }

    fun getLastConfirmedPurchase(): Flow<Purchase?> = callbackFlow {
        val query = purchaseCollection
            .whereEqualTo("confirmed", true)
            .orderBy("purchaseTimeStamp", Query.Direction.DESCENDING)
            .limit(1)

        val listener = query.addSnapshotListener { snapshot, exception ->
            if (exception != null || snapshot == null) {
                trySend(null)
                return@addSnapshotListener
            }

            val lastPurchase = snapshot.documents.firstOrNull()?.toObject(Purchase::class.java)
            trySend(lastPurchase)
        }
        awaitClose { listener.remove() }
    }


}