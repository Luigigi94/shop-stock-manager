package com.example.inventarioapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventarioapp.constants.MovementType
import com.example.inventarioapp.model.InventoryCountItem
import com.example.inventarioapp.model.InventoryHeader
import com.example.inventarioapp.model.InventoryMovements
import com.example.inventarioapp.model.Products
import com.example.inventarioapp.repository.InventoryRepository
import com.example.inventarioapp.repository.ProductRepository
import com.example.inventarioapp.state.InventoryUiState
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
    private val _itemsListed = MutableStateFlow<List<InventoryCountItem>>(emptyList())
    val itemsListed: StateFlow<List<InventoryCountItem>> = _itemsListed.asStateFlow()

    private val _listedInventories = MutableStateFlow<List<InventoryHeader>>(emptyList())
    val listedInventories: StateFlow<List<InventoryHeader>> get() = _listedInventories

    private val _uiState = MutableStateFlow(InventoryUiState())
    val uiState: StateFlow<InventoryUiState> get() = _uiState

    private var currentInventoryId: String? = null


    init {
//        loadItems()
        viewModelScope.launch {
            inventoryRepository.getListedInventories().collect { fetchedList ->
                _listedInventories.value = fetchedList
            }
        }
    }

    fun initViewModel(inventoryId: String){
        if (currentInventoryId == inventoryId) return
        currentInventoryId = inventoryId
        loadInventory(inventoryId)
    }

    fun loadInventoryItem(inventoryId: String, idProduct: String){
        viewModelScope.launch {
            val draftItems = inventoryRepository.getInventoryDraft(inventoryId) ?: return@launch
            val item = draftItems.find { it.idProduct == idProduct } ?: return@launch

            _uiState.value = InventoryUiState(
                idProduct = item.idProduct,
                productName = item.productName,
                systemQuantity = item.systemQuantity,
                countedQuantity = item.countedQuantity.toString(),
                isEdit = true
            )
        }
    }
    fun loadInventory(inventoryId: String) = viewModelScope.launch {
        val draft = inventoryRepository.getInventoryDraft(inventoryId)

        if (!draft.isNullOrEmpty()){
            Log.d("InventoryVM", "Cargando borrador encontrado para $inventoryId")
            _itemsListed.value = draft
        } /*else {
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
        }*/
    }

    fun addProductToInventory(product: Products){
        val currentProducts = _itemsListed.value
        val existingItem = currentProducts.find { it.idProduct == product.idProduct }

        if (existingItem != null){
            val newItem = InventoryCountItem(
                idProduct = product.idProduct,
                productName = product.nameProduct,
                systemQuantity = product.stock,
                countedQuantity = product.stock,
            )
            _itemsListed.value = currentProducts + newItem

            saveCurrentDraft()
        }
    }

    fun saveCurrentDraft(){
        val inventoryId = currentInventoryId ?: return
        viewModelScope.launch {
            inventoryRepository.saveInventoryDraft(inventoryId, _itemsListed.value)
        }
    }

    fun updateCount(productId: String, qty: Int) {
        _itemsListed.update { list ->
            list.map { if (it.idProduct == productId) it.copy(countedQuantity = qty) else it }
        }

        saveCurrentDraft()
    }


    fun confirmInventory(userId: String = "Admin") = viewModelScope.launch {
        val inventoryId = currentInventoryId ?: return@launch
        Log.d("confirmInventory", "Revisando que llegue usuario: $userId")
        val refId = "inventory_${userId}_${System.currentTimeMillis()}"
        val diffItems = _itemsListed.value.filter { it.difference != 0 }
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