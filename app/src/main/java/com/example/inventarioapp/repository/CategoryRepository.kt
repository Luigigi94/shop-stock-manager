package com.example.inventarioapp.repository

import android.util.Log
import com.example.inventarioapp.constants.FirestorePaths
import com.example.inventarioapp.model.Categories
import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.tasks.await

/**
 * Repository de Categories
 *
 * Encapsula todas las operaciones con firebase
 * La UI nnunca debe tocar firebase directamente
 */
class CategoryRepository {

    private val db by lazy {
        FirebaseFirestore.getInstance()
    }
    
    private val categoryRepo = db.collection(FirestorePaths.Collections.CATEGORIES)
    /**
    * Devuelve un Flow con la lista de categorias
    * Esto permite que la UI sea reactiva
    */
    fun getCategories(): Flow<List<Categories>> {
        return callbackFlow {
            val listener = categoryRepo.addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                val list = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Categories::class.java)
                }

                trySend(list)
            }
            awaitClose { listener.remove() }
        }
    }

    /**
     * Agrega una categoria nueva a firebase
     * @param category: objeto Categories a agregar
     * */
    suspend fun addCategory(category: Categories): Result<Unit>{
        return try {
            categoryRepo
                .document(category.idCategory)
                .set(category)
                .await()

            Result.success(Unit)
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun getCategoryById(id: String): Categories? {
        return categoryRepo
            .document(id)
            .get()
            .await()
            .toObject(Categories::class.java)
    }

    /**
     * Actualiza una categoria en firebase
     * */
    suspend fun updateCategory(category: Categories): Result<Unit>{
        return try {
            categoryRepo
                .document(category.idCategory)
                .set(category)
                .await()

            Result.success(Unit)
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    /**
     * Elimina una categoria por su id en firebase*/
    suspend fun deleteCategory(category: String): Result<Unit>{
        return try {
            categoryRepo
                .document(category)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception){
            Result.failure(e)
        }
    }
    
    suspend fun getGenericCategory(): List<Categories>{
        return categoryRepo
            .whereEqualTo("nameCategory","Cat_General")
            .get()
            .await()
            .toObjects(Categories::class.java)
    }
}