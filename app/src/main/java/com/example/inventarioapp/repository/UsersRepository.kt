package com.example.inventarioapp.repository

import com.example.inventarioapp.constants.FirestorePaths
import com.example.inventarioapp.model.UserSession
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class UsersRepository {

    private val db by lazy {
        FirebaseFirestore.getInstance()
    }

    private val userRef = db.collection(FirestorePaths.Collections.USERS)

    suspend fun saveUser(uid: String, user: HashMap<String, Any?>) {
        try {
            userRef.document(uid)
                .set(user, SetOptions.merge())
                .await()
        } catch (e: Exception){
            throw e
        }
    }

    suspend fun fetchUserRole(userId: String): UserSession?{
        return userRef.document(userId)
            .get()
            .await()
            .toObject(UserSession::class.java)

    }
}