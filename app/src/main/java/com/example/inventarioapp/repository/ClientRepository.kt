package com.example.inventarioapp.repository

import com.example.inventarioapp.constants.FirestorePaths
import com.example.inventarioapp.model.Clients
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ClientRepository {

    private val db by lazy {
        FirebaseFirestore.getInstance()
    }
    
    private val clientCollection = db.collection(FirestorePaths.Collections.CLIENTS)

    fun getClients(): Flow<List<Clients>> {
        return callbackFlow {
            val listener = clientCollection.addSnapshotListener { snapshots, exception ->
                if (exception != null || snapshots == null) return@addSnapshotListener
                val list = snapshots.documents.mapNotNull { documentSnapshot ->
                    documentSnapshot.toObject(Clients::class.java)
                }

                trySend(list)
            }
            awaitClose { listener.remove() }
        }
    }

    suspend fun addClient(client: Clients): Result<Unit>{
        return try {
            clientCollection
                .document(client.idClient)
                .set(client)
                .await()

            Result.success(Unit)
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun getClientById(idClient: String): Clients? {
        return clientCollection
            .document(idClient)
            .get()
            .await()
            .toObject(Clients::class.java)
    }

    suspend fun updateClient(client: Clients): Result<Unit>{
        return try {
            clientCollection
                .document(client.idClient)
                .set(client)
                .await()
            Result.success(Unit)
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun deleteClient(idClient: String): Result<Unit>{
        return try {
            clientCollection
                .document(idClient)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception){
            Result.failure(e)
        }
    }
}