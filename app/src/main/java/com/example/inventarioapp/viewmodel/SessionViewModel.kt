package com.example.inventarioapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.inventarioapp.model.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessionViewModel : ViewModel(){
    private val _session = MutableStateFlow<UserSession?>(null)
    val session: StateFlow<UserSession?> = _session.asStateFlow()

    fun loginAsAdmin() {
        _session.value = UserSession(
            userName = "Admin",
            role = "ADMIN"
        )
    }

    fun logout() {
        _session.value = null
    }
}