package com.example.inventarioapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventarioapp.repository.InventoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class StockViewModel (
    private val inventoryRepository: InventoryRepository = InventoryRepository()
): ViewModel() {
    val stock = MutableStateFlow<Map<String, Int>>(emptyMap())
   /* fun observeStockForProducts(productIds: List<String>) {

        viewModelScope.launch {
            inventoryRepository.observeStockBulk(productIds)
                .collect { stock.value = it }
        }
    }*/
}