package com.example.inventarioapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventarioapp.model.Products
import com.example.inventarioapp.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductViewModel (
    private val repository: ProductRepository = ProductRepository()
): ViewModel(){
    private val _products = MutableStateFlow<List<Products>>(emptyList())
    val products: StateFlow<List<Products>> get() = _products

    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: MutableStateFlow<String?> get() = _uiMessage

    private val _selectedProduct = MutableStateFlow<Products?>(null)
    val selectedProduct: MutableStateFlow<Products?> get() = _selectedProduct

    init {
        viewModelScope.launch {
            repository.getProducts().collect { fetchedList ->
                _products.value = fetchedList
            }
        }
    }

    fun addProduct(product: Products){
        viewModelScope.launch {
            val result = repository.addProduct(product)

            result
                .onSuccess { _uiMessage.value = "SUCCEEDED_ADD_PRODUCT" }
                .onFailure { err -> _uiMessage.value = "ERROR_ADD_PRODUCT: ${err.message}" }
        }
    }

    fun loadProduct(id: String){
        viewModelScope.launch {
            _selectedProduct.value = repository.getProductById(id)
        }
    }

    fun updateProduct(product: Products){
        viewModelScope.launch {
            val result = repository.updateProduct(product)

            result
                .onSuccess { _uiMessage.value = "SUCCEEDED_UPDATE_PRODUCT" }
                .onFailure { err -> _uiMessage.value = "ERROR_UPDATE_PRODUCT: ${err.message}" }
        }
    }

    fun deleteProduct(productId: String){
        viewModelScope.launch {
            val result = repository.deleteProduct(productId)

            result
                .onSuccess { _uiMessage.value = "SUCCEEDED_DELETE_PRODUCT" }
                .onFailure { err -> _uiMessage.value = "ERROR_DELETE_PRODUCT: ${err.message}" }
        }
    }
}