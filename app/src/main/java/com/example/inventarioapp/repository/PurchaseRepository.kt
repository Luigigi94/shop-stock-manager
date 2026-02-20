package com.example.inventarioapp.repository

import android.util.Log
import com.example.inventarioapp.constants.FirestorePaths
import com.example.inventarioapp.model.Cart
import com.example.inventarioapp.model.Clients
import com.example.inventarioapp.model.Products
import com.example.inventarioapp.model.Purchase
import com.example.inventarioapp.model.PurchaseItem
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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

    suspend fun clearCart(userId: String) {
        carts.document(userId).delete().await()
    }
}