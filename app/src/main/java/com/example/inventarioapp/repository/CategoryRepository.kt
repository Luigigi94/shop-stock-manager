package com.example.inventarioapp.repository

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
    /**
    * Devuelve un Flow con la lista de categorias
    * Esto permite que la UI sea reactiva
    */
    fun getCategories(): Flow<List<Categories>> {
        return callbackFlow {
            val listener = db.collection("Categories").addSnapshotListener { snapshot, error ->
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
            db.collection("Categories")
                .document(category.idCategory)
                .set(category)
                .await()

            Result.success(Unit)
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun getCategoryById(id: String): Categories? {
        return db.collection("Categories")
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
            db.collection("Categories")
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
    suspend fun deleteCategory(category: String){
//        TODO: Implementar eliminación en firebase
    }
}