package com.example.inventarioapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventarioapp.constants.MovementType
import com.example.inventarioapp.model.InventoryCountItem
import com.example.inventarioapp.model.InventoryMovements
import com.example.inventarioapp.repository.InventoryRepository
import com.example.inventarioapp.repository.ProductRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class InventoryViewModel(
    private val inventoryRepository: InventoryRepository = InventoryRepository(),
    private val productRepository: ProductRepository = ProductRepository()

): ViewModel() {

//    private val repoStock
    private val _items = MutableStateFlow<List<InventoryCountItem>>(emptyList())
    val items: StateFlow<List<InventoryCountItem>> = _items.asStateFlow()

    private var currentUserId: String? = null
    private var isInitialized = false

    private var isLoaded = false

    init {
        loadItems()
    }

    fun initViewModel(userId: String){
        if (isInitialized) return
        currentUserId = userId
        loadInventory(userId)
        isInitialized = true
    }


    fun loadItems() = viewModelScope.launch {
        if (isLoaded) return@launch

        val products = productRepository.getInventoryProducts()

        _items.value = products.map { prod ->
            InventoryCountItem(
                idProduct =  prod.idProduct,
                productName = prod.nameProduct,
                systemQuantity = prod.stock,
                countedQuantity = prod.stock,
            )
        }
        isLoaded = true
    }

    fun loadInventory(userId: String) = viewModelScope.launch {
        val draft = inventoryRepository.getInventoryDraft(userId)

        if (!draft.isNullOrEmpty()){
            Log.d("InventoryVM", "Cargando borrador encontrado para $userId")
            _items.value = draft
        } else {
            Log.d("InventoryVM", "No hay borrador, cargando productos base")
            val products = productRepository.getInventoryProducts()
            _items.value = products.map { prod ->
                InventoryCountItem(
                    idProduct = prod.idProduct,
                    productName = prod.nameProduct,
                    systemQuantity = prod.stock,
                    countedQuantity = prod.stock
                )
            }
        }
    }

    fun updateCount(productId: String, qty: Int) {
        _items.update { list ->
            list.map { if (it.idProduct == productId) it.copy(countedQuantity = qty) else it }
        }

        currentUserId?.let { userId ->
            viewModelScope.launch {
                inventoryRepository.saveInventoryDraft(userId, _items.value)
            }
        }
    }


    fun confirmInventory(userId: String = "Admin") = viewModelScope.launch {
        Log.d("confirmInventory", "Revisando que llegue usuario: $userId")
        val refId = "inventory_${userId}_${System.currentTimeMillis()}"
        val diffItems = _items.value.filter { it.difference != 0 }
        Log.d("confirmInventory", "Revisando que existe diffItems: $diffItems")

        if (diffItems.isEmpty()) return@launch

        val movements = diffItems.map { item ->
            InventoryMovements(
                id = UUID.randomUUID().toString(),
                productId = item.idProduct,
                quantity = item.difference,
                type = MovementType.INVENTORY,
                reason = "Conteo Físico",
                referenceId = refId,
                userId = userId,
                createdAt = Timestamp.now()
            )
        }

        val finalStocks = diffItems.associate { it.idProduct to it.countedQuantity }
        inventoryRepository.applyInventoryMovements(movements, finalStocks)
        inventoryRepository.deleteDraft(userId)
    }
}