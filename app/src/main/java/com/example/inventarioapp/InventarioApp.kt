package com.example.inventarioapp

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp

class InventarioApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.d("App","Firebase init Called")
        // 🔥 Inicializa Firebase ANTES de cualquier ViewModel/Repository
        FirebaseApp.initializeApp(this)
    }
}