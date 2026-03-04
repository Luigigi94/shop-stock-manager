package com.example.inventarioapp.model

import com.google.firebase.Timestamp
import kotlin.uuid.Uuid

class UserSession (
    val uuid: String =  "",
    val userName: String? = "",
    val role: String = "",
    val email: String?,
    val photoUrl: String?,
    val lastLogin: Timestamp? = null
)