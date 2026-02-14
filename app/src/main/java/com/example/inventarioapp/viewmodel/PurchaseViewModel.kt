package com.example.inventarioapp.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventarioapp.model.Clients
import com.example.inventarioapp.model.Purchase
import com.example.inventarioapp.model.PurchaseItem
import com.example.inventarioapp.repository.PurchaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PurchaseViewModel(
    private val repository: PurchaseRepository = PurchaseRepository()
): ViewModel() {
    private val _cart = MutableStateFlow<List<PurchaseItem>>(emptyList())
    val cart: StateFlow<List<PurchaseItem>> = _cart

    private val _confirmedPurchases = MutableStateFlow<List<Purchase>>(emptyList())
    val confirmedPurchase: StateFlow<List<Purchase>> = _confirmedPurchases

    private val _lastPurchase = MutableStateFlow<Purchase?>(null)
    val lastPurchase: StateFlow<Purchase?> = _lastPurchase

    var uiMessage by mutableStateOf<String?>(null)
        private set

    var currentClient by mutableStateOf<Clients?>(null)

    val total: Double
        get() = _cart.value.sumOf { it.subtotal }

    init {
        viewModelScope.launch {
            repository.getActivePurchaseItem().collect { fetchedList ->
                _cart.value = fetchedList
            }
        }
    }

    fun addItem(item: PurchaseItem, client: Clients?){
        viewModelScope.launch {
            repository.addPurchasedItem(client, item)
            uiMessage = "PURCHASE_ITEM_ADDED_SUCCESSFULLY"
        }
    }

    fun updateItem(item: PurchaseItem, newQuantity: Int){
        viewModelScope.launch {
            repository.updatePurchaseItem(item.copy(quantity = newQuantity))
            uiMessage = "PURCHASE_ITEM_UPDATED_SUCCESSFULLY"
        }
    }

    fun deleteItem(item: PurchaseItem){
        viewModelScope.launch {
            repository.deletePurchaseItem(item)
            uiMessage = "PURCHASE_ITEM_DELETED_SUCCESSFULLY"
        }
    }

    fun confirmPurchase(){
        viewModelScope.launch {
            repository.confirmPurchase()
            _cart.value = emptyList()
            currentClient = null
            uiMessage = "PURCHASE_CONFIRMED_SUCCESSFULLY"
        }
    }

    fun clearMessage(){
        uiMessage = null
    }

    fun fetchLastConfirmedPurchase() {
        viewModelScope.launch {
            repository.getLastConfirmedPurchase().collect { purchase ->
                _lastPurchase.value = purchase
            }
        }
    }
}