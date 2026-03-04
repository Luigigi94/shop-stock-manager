package com.example.inventarioapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventarioapp.model.UserSession
import com.example.inventarioapp.repository.UsersRepository
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SessionViewModel(
    private val usersRepository: UsersRepository = UsersRepository()
) : ViewModel(){
    private val _session = MutableStateFlow<UserSession?>(null)
    val session: StateFlow<UserSession?> = _session.asStateFlow()

    init {
        checkActiveSession()
    }

    private fun checkActiveSession() {
        val currentUser = Firebase.auth.currentUser
        if (currentUser != null) {
            _session.value = UserSession(
                userName = currentUser.displayName,
                email = currentUser.email,
                photoUrl = currentUser.photoUrl?.toString()
            )
        }
    }

    fun updateSession(user: FirebaseUser?){
        _session.value = user?.let {
            UserSession(
                userName = it.displayName,
                email = it.displayName,
                photoUrl = it.photoUrl?.toString()
            )
        }
    }

    fun saveUserToFirebase(user: FirebaseUser){
        val sessionUser = _session.value
        val userData = hashMapOf<String, Any?>(
            "uuid" to user.uid,
            "userName" to user.displayName,
            "email" to user.email,
            "photoUrl" to user.photoUrl.toString(),
            "lastLogin" to Timestamp.now()
        )

        viewModelScope.launch {
            try {
                usersRepository.saveUser(user.uid, userData)
            }catch (e: Exception) {
                Log.e("VM", "Error al guardar usuario: ${e.message}")
            }
        }
    }

    fun logout() {
        Firebase.auth.signOut()
        _session.value = null
    }
}