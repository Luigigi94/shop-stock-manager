package com.example.inventarioapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventarioapp.constants.MovementType
import com.example.inventarioapp.model.InventoryCountItem
import com.example.inventarioapp.model.InventoryMovements
import com.example.inventarioapp.repository.CatalogsRepository
import com.example.inventarioapp.repository.InventoryRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class InventoryViewModel(
    private val inventoryRepository: InventoryRepository = InventoryRepository(),
    private val catalogsRepository: CatalogsRepository = CatalogsRepository()

): ViewModel() {

//    private val repoStock
    private val _items = MutableStateFlow<List<InventoryCountItem>>(emptyList())
    val items: MutableStateFlow<List<InventoryCountItem>> = _items

    private var productsId: List<String> = emptyList()

    /*fun loadInventory() = viewModelScope.launch {
        val products = catalogsRepository.getProducts()
        productsId = products.map { it.idProduct }


        inventoryRepository.observeStockBulk(productsId)
            .collect { stockMap ->
                _items.value = products.map { products ->
                    val systemQty = stockMap[products.idProduct] ?: 0

                    InventoryCountItem(
                        idProduct = products.idProduct,
                        productName = products.nameProduct,
                        systemQuantity = systemQty,
                        countedQuantity = systemQty
                    )
                }
            }
    }*/

    fun updateCount(productId: String, qty: Int) {
        _items.update { list ->
            list.map {
                if (it.idProduct == productId)
                    it.copy(countedQuantity = qty)
                else it
            }
        }
    }

    fun confirmInventory(userId: String = "Admin") = viewModelScope.launch {
        val refId = "inventory_${userId}_${System.currentTimeMillis()}"

        val movements = _items.value
            .filter { it.difference != 0 }
            .map { item ->
                InventoryMovements(
                    id = UUID.randomUUID().toString(),
                    productId = item.idProduct,
                    quantity = item.countedQuantity,
                    type = MovementType.INVENTORY,
                    reason = "Conteo Físico",
                    referenceId = refId,
                    userId = userId,
                    createdAt = Timestamp.now()
                )
            }
        if (movements.isEmpty()) return@launch
        inventoryRepository.applyMovements(movements)
    }
}