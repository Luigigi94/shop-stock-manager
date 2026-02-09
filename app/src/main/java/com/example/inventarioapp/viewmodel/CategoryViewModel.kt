package com.example.inventarioapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventarioapp.model.Categories
import com.example.inventarioapp.repository.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel de Categories
 *
 * Mantiene el estado reactivo para Compose
 * Consume datos desde CcategoryRepository*/

class CategoryViewModel (
    private val repository: CategoryRepository = CategoryRepository()
): ViewModel(){
//    Lista de categorias expuestas a la UI
    private val _categories = MutableStateFlow<List<Categories>>(emptyList())
    val categories: StateFlow<List<Categories>> get() = _categories

    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> get() = _uiMessage

    init {
//        Inicialización: suscribirse a los datos de Firebase
        viewModelScope.launch {
            repository.getCategories().collect { fetchedList ->
                _categories.value = fetchedList
            }
        }
    }

    /**
     * Agrega una nueva categoria
     * La UI llama a esta función desde el botón
     * */
    fun addCategory(category: Categories){
        viewModelScope.launch {
            val result = repository.addCategory(category)

            result
                .onSuccess { _uiMessage.value = "SUCCEDED_ADD_CATEGORY" }
                .onFailure { e-> _uiMessage.value = "ERROR_ADD_CATEGORY: ${e.message}" }
        }
    }

    /**
     * Actualiza una categoría existente.
     */
    fun updateCategory(category: Categories) {
        viewModelScope.launch {
            repository.updateCategory(category)
        }
    }

    /**
     * Elimina una categoría.
     */
    fun deleteCategory(categoryId: String) {
        viewModelScope.launch {
            repository.deleteCategory(categoryId)
        }
    }
}