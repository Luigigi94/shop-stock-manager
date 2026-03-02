package com.example.inventarioapp.repository

import com.example.inventarioapp.constants.FirestorePaths
import com.example.inventarioapp.model.Supplier
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class SupplierRepository {
    private val db by lazy {
        FirebaseFirestore.getInstance()
    }
    
    private val supplierCollection = db.collection(FirestorePaths.Collections.SUPPLIER)

    fun getSuppliers(): Flow<List<Supplier>> {
        return callbackFlow {
            val listener = supplierCollection.addSnapshotListener { snapshots, exception ->
                if (exception != null || snapshots == null) return@addSnapshotListener
                val list = snapshots.documents.mapNotNull { documentSnapshot ->
                    documentSnapshot.toObject(Supplier::class.java)
                }

                trySend(list)
            }
            awaitClose { listener.remove() }
        }
    }

    suspend fun addSupplier(supplier: Supplier): Result<Unit>{
        return try {
            supplierCollection
                .document(supplier.idSupplier)
                .set(supplier)
                .await()

            Result.success(Unit)
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun getSupplierById(supplierId: String): Supplier?{
        return supplierCollection
            .document(supplierId)
            .get()
            .await()
            .toObject(Supplier::class.java)
    }

    suspend fun updateSupplier(supplier: Supplier): Result<Unit>{
        return try {
            supplierCollection
                .document(supplier.idSupplier)
                .set(supplier)
                .await()

            Result.success(Unit)
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun deleteSupplier(supplierId: String): Result<Unit>{
        return try {
            supplierCollection
                .document(supplierId)
                .delete()
                .await()

            Result.success(Unit)
        }catch (e: Exception){
            Result.failure(e)
        }
    }
}