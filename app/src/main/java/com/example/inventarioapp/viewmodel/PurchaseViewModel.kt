package com.example.inventarioapp.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventarioapp.model.Clients
import com.example.inventarioapp.model.Products
import com.example.inventarioapp.model.Purchase
import com.example.inventarioapp.model.PurchaseItem
import com.example.inventarioapp.repository.PurchaseRepository
import com.example.inventarioapp.state.PurchaseItemUiState
import com.example.inventarioapp.validators.PurchaseItemValidator
import com.example.inventarioapp.validators.model.ValidationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PurchaseViewModel(
    private val repository: PurchaseRepository = PurchaseRepository()
) : ViewModel() {
    private val _cart = MutableStateFlow<List<PurchaseItem>>(emptyList())
    val cart: StateFlow<List<PurchaseItem>> = _cart

    private val _confirmedPurchases = MutableStateFlow<List<Purchase>>(emptyList())
    val confirmedPurchase: StateFlow<List<Purchase>> = _confirmedPurchases

    private val _lastPurchase = MutableStateFlow<Purchase?>(null)
    val lastPurchase: StateFlow<Purchase?> = _lastPurchase

    var uiMessage by mutableStateOf<String?>(null)
        private set

    var currentClient by mutableStateOf<Clients?>(null)

    private val _products = MutableStateFlow<List<Products>>(emptyList())
    private val _clients = MutableStateFlow<List<Clients>>(emptyList())


    val total: Double
        get() = _cart.value.sumOf { item ->
            val product = _products.value.firstOrNull { it.idProduct == item.idProduct }
            (product?.priceProduct ?: 0.0) * item.quantity
        }

    private val _uiState = MutableStateFlow(PurchaseItemUiState())
    val uiState: StateFlow<PurchaseItemUiState> = _uiState

    fun onIdProduct(value: String) {
        _uiState.value = validateForm(
            _uiState.value.copy(
                idProduct = value
            )
        )
    }

    fun onIdClient(value: String) {
        _uiState.value = validateForm(
            _uiState.value.copy(
                idClient = value
            )
        )
    }

    fun onQuantity(value: String) {
        _uiState.value = validateForm(
            _uiState.value.copy(
                quantity = value,
                quantityTouched = true
            )
        )
    }

    fun onQuantityBlur() {
        _uiState.value = validateForm(
            _uiState.value.copy(quantityTouched = true)
        )
    }

    init {
        viewModelScope.launch {
            repository.getActivePurchaseItem().collect { fetchedList ->
                _cart.value = fetchedList
            }
        }
    }

    fun addItem(/*item: PurchaseItem, client: Clients?*/) {
        val validateState = validateForm(
            _uiState.value.copy(
                idProductTouched = true,
                quantityTouched = true
            )
        )

        _uiState.value = validateState

        if (!validateState.isValid) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }

            try {
                val quantityInt = validateState.quantity.toInt()

                val newItem = PurchaseItem(
                    idProduct = validateState.idProduct,
                    quantity = quantityInt
                )

                val updatedItems = _cart.value + newItem

                val total = updatedItems.sumOf { item ->
                    val product = _products.value.firstOrNull {
                        it.idProduct == item.idProduct
                    }
                    (product?.priceProduct ?: 0.0) * item.quantity
                }


                val client = _clients.value.firstOrNull { it.idClient == uiState.value.idClient }

                val result = repository.addPurchasedItem(client, newItem, total)

                if (result.isSuccess) {
                    _uiState.value = PurchaseItemUiState(success = true)
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception){
                _uiState.update {
                    it.copy(
                        isLoading = false
                    )
                }
            }
            /*val result = repository.addPurchasedItem(client, item)

            if (result.isSuccess) {
                uiMessage = "PURCHASE_ITEM_ADDED_SUCCESSFULLY"
            } else {
                Log.e("_Log.ViewModel", "Error")
            }*/
        }
    }

    fun updateItem(item: PurchaseItem, newQuantity: Int) {
        viewModelScope.launch {
            repository.updatePurchaseItem(item.copy(quantity = newQuantity))
            uiMessage = "PURCHASE_ITEM_UPDATED_SUCCESSFULLY"
        }
    }

    fun deleteItem(item: PurchaseItem) {
        viewModelScope.launch {
            repository.deletePurchaseItem(item)
            uiMessage = "PURCHASE_ITEM_DELETED_SUCCESSFULLY"
        }
    }

    fun confirmPurchase(onSuccess: () -> Unit) {
        viewModelScope.launch {

            val purchase = repository.confirmPurchase()

            Log.d("PurchaseVM", "CONFIRMED PURCHASE -> $purchase")
            Log.d("PurchaseVM", "CLIENT -> ${purchase?.client}")

            if (purchase != null) {
                _lastPurchase.value = purchase
                _cart.value = emptyList()
                currentClient = null
                onSuccess()
            }
        }
    }

    fun clearMessage() {
        uiMessage = null
    }

    fun validateForm(state: PurchaseItemUiState): PurchaseItemUiState {
        val idProduct = PurchaseItemValidator.idProduct(state.idProduct)
        val quantity = PurchaseItemValidator.quantity(state.quantity)

        val isValid =
            idProduct is ValidationResult.Valid &&
                    quantity is ValidationResult.Valid

        return state.copy(
            idProductError =
                if (state.idProductTouched)
                    (idProduct as? ValidationResult.Invalid)?.message
                else
                    null,
            quantityError =
                if (state.quantityTouched)
                    (quantity as? ValidationResult.Invalid)?.message
                else
                    null,
            isValid = isValid
        )
    }

    /*fun fetchLastConfirmedPurchase() {
        viewModelScope.launch {
            repository.getLastConfirmedPurchase().collect { purchase ->
                _lastPurchase.value = purchase
            }
        }
    }*/
}