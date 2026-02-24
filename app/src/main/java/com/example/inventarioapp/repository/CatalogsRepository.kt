package com.example.inventarioapp.repository

import com.example.inventarioapp.model.Clients
import com.example.inventarioapp.model.Products
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CatalogsRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val products = db.collection("Products")
    private val clients = db.collection("Clients")


    /* ---------- Catálogos ---------- */
    suspend fun getProducts(): List<Products> =
        products.get().await().toObjects(Products::class.java)

    suspend fun getClients(): List<Clients> =
        clients.get().await().toObjects(Clients::class.java)
}