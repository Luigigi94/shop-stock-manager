package com.example.inventarioapp.repository

import android.util.Log
import com.example.inventarioapp.model.Cart
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class CartRepository (
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val carts = db.collection("Carts")

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