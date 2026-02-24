package com.example.inventarioapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventarioapp.constants.MovementType
import com.example.inventarioapp.model.InventoryCountItem
import com.example.inventarioapp.model.InventoryMovements
import com.example.inventarioapp.repository.CatalogsRepository
import com.example.inventarioapp.repository.InventoryRepository
import com.example.inventarioapp.repository.ProductRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class InventoryViewModel(
    private val inventoryRepository: InventoryRepository = InventoryRepository(),
    private val productRepository: ProductRepository = ProductRepository(),
    private val catalogsRepository: CatalogsRepository = CatalogsRepository()

): ViewModel() {

//    private val repoStock
    private val _items = MutableStateFlow<List<InventoryCountItem>>(emptyList())
    val items: StateFlow<List<InventoryCountItem>> = _items.asStateFlow()

    private var productsId: List<String> = emptyList()

    private var isLoaded = false

    init {
        loadItems()
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

    fun updateCount(productId: String, qty: Int) {
        Log.d("UpdateCount","Revisando UpdateCount $productId, $qty")
        _items.update { list ->
            list.map { item ->
                if (item.idProduct == productId) item.copy(countedQuantity = qty)
                else item
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
        inventoryRepository.applyMovements(movements, finalStocks)
    }
}