package com.example.inventarioapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.inventarioapp.model.UserSession
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessionViewModel : ViewModel(){
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

    fun logout() {
        Firebase.auth.signOut()

        _session.value = null
    }
}