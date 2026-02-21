package com.example.inventarioapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventarioapp.repository.PurchaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StockViewModel (
    private val repository: PurchaseRepository = PurchaseRepository()
): ViewModel() {
    val stock = MutableStateFlow<Map<String, Int>>(emptyMap())

    fun observeStockForProducts(productIds: List<String>){
        productIds.forEach { id ->
            viewModelScope.launch {
                repository.observeStock(id).collect { quantity ->
                    stock.update { current ->
                        current.toMutableMap().apply { put(id, quantity) }
                    }
                }
            }
        }
    }
}