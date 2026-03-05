package com.example.inventarioapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventarioapp.constants.MovementType
import com.example.inventarioapp.model.InventoryCountItem
import com.example.inventarioapp.model.InventoryDetail
import com.example.inventarioapp.model.InventoryHeader
import com.example.inventarioapp.model.InventoryMovements
import com.example.inventarioapp.model.InventoryResult
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

    private val _confirmationResult = MutableStateFlow<InventoryResult>(InventoryResult.Idle)
    val confirmationResult: StateFlow<InventoryResult> = _confirmationResult.asStateFlow()

    private val _activeDraftId = MutableStateFlow<String?>(null)
    val activeDraftId = _activeDraftId.asStateFlow()

    private var currentInventoryId: String? = null




    init {
//        loadItems()
        fetchDraftId()
        viewModelScope.launch {
            inventoryRepository.getListedInventories().collect { fetchedList ->
                _listedInventories.value = fetchedList
            }
        }
    }

    fun initViewModel(inventoryId: String){
        Log.d("INV -> InventoryVM initiViewModel","Revisando el valor de inventoryId $inventoryId y de currentInventory: $currentInventoryId")
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

    fun onIdProduct(value: String){
        _uiState.update { it.copy(idProduct = value) }
    }
    fun onQtyCounted(value: String){
        _uiState.update { it.copy(countedQuantity = value) }
    }
    fun loadInventory(inventoryId: String) {
        Log.d("INV -> InventoryVM loadInventory","valor de inventoryId: $inventoryId")
        currentInventoryId = inventoryId // ¡ESTA LÍNEA ES VITAL!
        viewModelScope.launch {
            val draft = inventoryRepository.getInventoryDraft(inventoryId)
            Log.d("INV -> InventoryVM loadInventory","valor de draft: $draft")
            if (!draft.isNullOrEmpty()) {
                _itemsListed.update {  draft }
            }
        }
    }

    fun addProductToInventory(productId: String) {
        viewModelScope.launch {
            // 1. Buscamos si ya existe en el conteo actual
            val currentItems = _itemsListed.value
            val alreadyInList = currentItems.any { it.idProduct == productId }

            if (alreadyInList) {
                // Si ya está, quizás prefieras mandarlo a updateCount en lugar de duplicar
                return@launch
            }

            // 2. Si no está, buscamos sus datos base (nombre, stock actual) en el repo de productos
            val baseProduct = productRepository.getProductById(productId) // Necesitas este método en tu repo

            baseProduct?.let { prod ->
                val newItem = InventoryCountItem(
                    idProduct = prod.idProduct,
                    productName = prod.nameProduct,
                    systemQuantity = prod.stock,
                    countedQuantity = _uiState.value.countedQuantity.toIntOrNull() ?: 0,
                    updatedAt = Timestamp.now()
                )

                // 3. Actualizamos la lista y el State de la UI
                _itemsListed.value = currentItems + newItem

                // 4. Limpiamos el formulario para el siguiente producto
                _uiState.update { InventoryUiState() }

                saveCurrentDraft()
            }
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


    fun confirmInventory(inventoryId: String, userId: String) = viewModelScope.launch {
        Log.d("confirmInventory", "Revisando que llegue usuario: $inventoryId")
        val diffItems = _itemsListed.value.filter { it.difference != 0 }
        Log.d("confirmInventory", "Revisando que existe diffItems: $diffItems")

        val items = _itemsListed.value

        if (diffItems.isEmpty()) return@launch
        if (items.isEmpty()) {
            _confirmationResult.value = InventoryResult.Error("No hay items para registrar")
            return@launch
        }

        val details = items.map { item ->
            InventoryDetail(
                id = "${inventoryId}_${item.idProduct}",
                referenceId = inventoryId,
                productId = item.idProduct,
                productName = item.productName,
                systemQuantity = item.systemQuantity,
                countedQuantity = item.countedQuantity,
                difference = item.difference,
                timestamp = Timestamp.now()
            )
        }

        val header = InventoryHeader(
            idHeaderInventory = inventoryId,
            userId = userId,
            userName = userId,
            createdAt = items.minByOrNull { it.updatedAt?.seconds ?: 0 }?.updatedAt ?: Timestamp.now(),
            finishedAt = Timestamp.now(),
            totalItemsCounted = items.size,
            totalDiscrepancies = items.count { it.difference != 0 }
        )

        val movements = diffItems.map { item ->
            InventoryMovements(
                id = UUID.randomUUID().toString(),
                productId = item.idProduct,
                quantity = item.difference,
                type = MovementType.INVENTORY,
                reason = "Conteo Físico",
                referenceId = inventoryId,
                userId = userId,
                createdAt = Timestamp.now()
            )
        }

        try {
            val finalStocks = diffItems.associate { it.idProduct to it.countedQuantity }
            inventoryRepository.applyInventoryMovements(movements, finalStocks)
            inventoryRepository.saveFinalInventoryRecord(header, details)
            inventoryRepository.deleteDraft(inventoryId)

            _confirmationResult.value = InventoryResult.Success
        } catch (e: Exception) {
            Log.e("ConfirmInventory","Error: $e")
            _confirmationResult.value = InventoryResult.Error(e.localizedMessage ?: "Error desconocido")
        }
    }

    fun fetchDraftId() {
        viewModelScope.launch {
            val id = inventoryRepository.getDraftActive()
            Log.d("INV -> InventoryVM FetchDraftId", "ID de Firebase: $id")
            _activeDraftId.value = id
        }
    }

    fun clearActiveDraftId(){
        _activeDraftId.value = null
    }
    fun resetConfirmationResult() {
        _confirmationResult.value = InventoryResult.Idle
    }
}