package com.example.inventarioapp.repository

import android.util.Log
import com.example.inventarioapp.constants.FirestorePaths
import com.example.inventarioapp.model.InventoryMovements
import com.example.inventarioapp.model.Reserves
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ReservesRepository {
    private val db by lazy {
        FirebaseFirestore.getInstance()
    }

    fun getReserves(): Flow<List<Reserves>> {
        return callbackFlow {
            val listener = db.collection(FirestorePaths.Collections.RESERVES)
                .addSnapshotListener { snap, exception ->
                    if (exception != null || snap == null) return@addSnapshotListener
                    val list = snap.documents.mapNotNull { documentSnapshot ->
                        documentSnapshot.toObject(Reserves::class.java)
                    }
                    trySend(list)
                }
            awaitClose { listener.remove() }
        }
    }

    suspend fun addReserve(reserves: Reserves): Result<Unit>{
        return try {
            db.collection(FirestorePaths.Collections.RESERVES)
                .document(reserves.idReserves)
                .set(reserves)
                .await()

            Result.success(Unit)
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun getReserveById(reserveId: String): Reserves?{
        return db.collection(FirestorePaths.Collections.RESERVES)
            .document(reserveId)
            .get()
            .await()
            .toObject(Reserves::class.java)
    }

    suspend fun updateReserve(reserves: Reserves): Result<Unit>{
        return try {
            db.collection(FirestorePaths.Collections.RESERVES)
                .document(reserves.idReserves)
                .set(reserves)
                .await()
            Result.success(Unit)
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun deleteReserve(reserveId: String): Result<Unit>{
        return try {
            db.collection(FirestorePaths.Collections.RESERVES)
                .document(reserveId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun applyReserveMovements(
        reserve: Reserves,
        movements: InventoryMovements,
        newStock: Int
    ){
        Log.d("Firestore", "Comienza applyReserveMovements reserve: $reserve\nmovements: $movements\nnewStock: $newStock")
        try {
            db.runTransaction { transaction ->
                val reserveRef = db.collection(FirestorePaths.Collections.RESERVES).document(reserve.idReserves)
                val movementRef = db.collection(FirestorePaths.Collections.INVENTORYMOVEMENTS).document(movements.id)
                val productRef = db.collection(FirestorePaths.Collections.PRODUCTS).document(reserve.idProduct)

                transaction.set(reserveRef, reserve)

                transaction.set(movementRef, movements)

                transaction.update(productRef, "stock", newStock.toLong())
            }.await()
            Log.d("Firestore", "Transacción completada con éxito")
        } catch (e: Exception) {
            Log.e("Firestore", "Error en la transacción: ${e.message}", e)
        }
    }


}