package com.example.inventarioapp.repository

import android.util.Log
import com.example.inventarioapp.constants.FirestorePaths
import com.example.inventarioapp.model.Products
import com.example.inventarioapp.model.PurchaseItem
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ProductRepository {
    private val db by lazy {
        FirebaseFirestore.getInstance()
    }

    fun getProducts(): Flow<List<Products>>{
        return callbackFlow {
            val listener = db.collection(FirestorePaths.Collections.PRODUCTS).addSnapshotListener{ snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                val list = snapshot.documents.mapNotNull{ doc ->
                    doc.toObject(Products::class.java)
                }

                trySend(list)
            }
            awaitClose { listener.remove() }
        }
    }

    suspend fun addProduct(product: Products): Result<Unit>{
        return try {
            db.collection(FirestorePaths.Collections.PRODUCTS)
                .document(product.idProduct)
                .set(product)
                .await()

            Result.success(Unit)
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun getProductById(id: String): Products? {
        return db.collection(FirestorePaths.Collections.PRODUCTS)
            .document(id)
            .get()
            .await()
            .toObject(Products::class.java)
    }
    suspend fun getInventoryProducts(): List<Products> {
        return try {
            db.collection(FirestorePaths.Collections.PRODUCTS)
                .get()
                .await()
                .toObjects(Products::class.java)
        } catch (e: Exception){
            Log.e("Repository getInventoryProducts", "Error: $e")
            emptyList()
        }
    }

    suspend fun updateProduct(product: Products): Result<Unit>{
        return try {
            db.collection(FirestorePaths.Collections.PRODUCTS)
                .document(product.idProduct)
                .set(product)
                .await()

            Result.success(Unit)
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun deleteProduct(product: String): Result<Unit>{
        return try {
            db.collection(FirestorePaths.Collections.PRODUCTS)
                .document(product)
                .delete()
                .await()

            Result.success(Unit)
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun applySaleStockUpdates(item: List<PurchaseItem>) {
        val batch = db.batch()

        item.forEach { item ->
            val ref = db.collection("Products").document(item.productId)

            batch.update(ref, "stock", FieldValue.increment(-item.quantity.toLong()))
        }
        batch.commit().await()
    }
}