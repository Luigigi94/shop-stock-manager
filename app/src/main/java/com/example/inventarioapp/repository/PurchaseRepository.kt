package com.example.inventarioapp.repository

import android.util.Log
import androidx.compose.animation.core.snap
import com.example.inventarioapp.constants.FirestorePaths
import com.example.inventarioapp.constants.MovementType
import com.example.inventarioapp.model.Cart
import com.example.inventarioapp.model.Clients
import com.example.inventarioapp.model.InventoryMovements
import com.example.inventarioapp.model.Products
import com.example.inventarioapp.model.Purchase
import com.example.inventarioapp.model.PurchaseItem
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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
    private val products = db.collection("Products")
    private val clients = db.collection("Clients")
    private val carts = db.collection("Carts")


    /* ---------- Catálogos ---------- */

    suspend fun getProducts(): List<Products> =
        products.get().await().toObjects(Products::class.java)

    suspend fun getClients(): List<Clients> =
        clients.get().await().toObjects(Clients::class.java)

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

    suspend fun saveCart(cart: Cart) {
        Log.d("PurchaseRepository","Revisando que cart no venga null $cart")
        val doc = carts.document(cart.userId) // usamos userId como ID del carrito
        doc.set(cart.copy(updatedAt = System.currentTimeMillis())).await()
    }

    fun observeCart(userId: String): Flow<Cart?> = callbackFlow {
        val sub = carts.document(userId)
            .addSnapshotListener { snap, _ ->
                trySend(snap?.toObject(Cart::class.java))
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

    suspend fun clearCart(userId: String) {
        carts.document(userId).delete().await()
    }

    suspend fun saveInventoryMovements(movements: List<InventoryMovements>){
        val batch = db.batch()
        movements.forEach { movement ->
            val doc = db.collection("InventoryMovements").document(movement.id)

            batch.set(doc, movement)
        }
        batch.commit().await()
    }

    fun observeStock(productId: String): Flow<Int> =
        /*val sub =*/ db.collection("InventoryMovements")
            .whereEqualTo("productId", productId)
            .snapshots()
            .map { snapshots ->
                snapshots.documents.sumOf {doc ->
                    val qty = doc.getLong("quantity")?.toInt() ?: 0
                    val type = doc.getString("type")

                    when (type){
                        MovementType.PURCHASE.toString() -> qty
                        MovementType.SALE.toString() -> -qty
                        else -> 0
                    }
                }
            }

    fun observeStockBulk(productIds: List<String>): Flow<Map<String, Int>> {

        if (productIds.isEmpty()) return flowOf(emptyMap())

        return db.collection("InventoryMovements")
            .whereIn("productId", productIds)
            .snapshots()
            .map { snapshots ->

                snapshots.documents
                    .groupBy { it.getString("productId")!! }
                    .mapValues { (_, docs) ->

                        docs.sumOf { doc ->
                            val qty = doc.getLong("quantity")?.toInt() ?: 0
                            val type = doc.getString("type")

                            when (type) {
                                MovementType.PURCHASE.name -> qty
                                MovementType.SALE.name -> -qty
                                else -> 0
                            }
                        }
                    }
            }
    }
//            .addSnapshotListener { snap, _ ->
//                val stock = snap?.documents
//                    ?.mapNotNull { it.getLong("quantity")?.toInt() }
//                    ?.sum() ?: 0
//                trySend(stock)
//            }
//        awaitClose { sub.remove() }


    suspend fun updateStock(movements: List<InventoryMovements>) {
        movements.forEach { movement ->
            val stockRef = db.collection("InventoryMovements").document(movement.productId)
            db.runTransaction { transaction ->
                val snapshot = transaction.get(stockRef)
                val currentStock = snapshot.getLong("quantity") ?: 0L
                Log.d("updateStock", "revisanding movimiento ${movement.type}")
                val newStock = when (movement.type){
                    MovementType.PURCHASE -> {
                        currentStock + movement.quantity
                    }
                    MovementType.SALE -> {
                        currentStock - movement.quantity
                    }
                    else -> {}
                }
                transaction.update(stockRef, "quantity", newStock)
            }
        }
    }

}