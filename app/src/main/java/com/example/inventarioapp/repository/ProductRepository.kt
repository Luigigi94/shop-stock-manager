package com.example.inventarioapp.repository

import com.example.inventarioapp.model.Products
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
            val listener = db.collection("Products").addSnapshotListener{ snapshot, error ->
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
            db.collection("Products")
                .document(product.idProduct)
                .set(product)
                .await()

            Result.success(Unit)
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun getProductById(id: String): Products? {
        return db.collection("Products")
            .document(id)
            .get()
            .await()
            .toObject(Products::class.java)
    }

    suspend fun updateProduct(product: Products): Result<Unit>{
        return try {
            db.collection("Products")
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
            db.collection("Products")
                .document(product)
                .delete()
                .await()

            Result.success(Unit)
        } catch (e: Exception){
            Result.failure(e)
        }
    }
}