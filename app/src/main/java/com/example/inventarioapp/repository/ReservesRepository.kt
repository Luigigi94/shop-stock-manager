package com.example.inventarioapp.repository

import androidx.compose.animation.core.snap
import com.example.inventarioapp.constants.FirestorePaths
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
}