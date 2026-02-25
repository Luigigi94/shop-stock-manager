package com.example.inventarioapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventarioapp.model.Categories
import com.example.inventarioapp.repository.CategoryRepository
import com.example.inventarioapp.state.CategoryUiState
import com.example.inventarioapp.validators.CategoryValidator
import com.example.inventarioapp.validators.model.ValidationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * ViewModel de Categories
 *
 * Mantiene el estado reactivo para Compose
 * Consume datos desde CcategoryRepository*/

class CategoryViewModel(
    private val repository: CategoryRepository = CategoryRepository()
) : ViewModel() {
    //    Lista de categorias expuestas a la UI
    private val _categories = MutableStateFlow<List<Categories>>(emptyList())
    val categories: StateFlow<List<Categories>> get() = _categories

    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> get() = _uiMessage

    private val _selectedCategory = MutableStateFlow<Categories?>(null)
    val selectedCategory: StateFlow<Categories?> get() = _selectedCategory

    private val _uiState = MutableStateFlow(CategoryUiState())
    val uiState: StateFlow<CategoryUiState> = _uiState

    /*
    * Setters para el support del state hoisting
    */

    fun onNameCategory(value: String) {
        _uiState.value = validateForm(
            uiState.value.copy(
                nameCategory = value,
                nameTouched = true
            )
        )
    }

    fun onNameBlur() {
        _uiState.value = validateForm(
            _uiState.value.copy(nameTouched = true)
        )
    }

    fun onDescriptionCategory(value: String) {
        _uiState.value = _uiState.value.copy(descriptionCategory = value)
    }

    fun startCreate() {
        _uiState.value = CategoryUiState()
    }

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
    fun addCategory() {
        val validatedState = validateForm(
            _uiState.value.copy(
                nameTouched = true
            )
        )

        _uiState.value = validatedState

        if (!validatedState.isValid) return

        val category = Categories(
            idCategory = UUID.randomUUID().toString(),
            nameCategory = validatedState.nameCategory,
            descriptionCategory = validatedState.descriptionCategory
        )
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, success = false) }
            repository.addCategory(category)
                .onSuccess {
                    _uiState.value = CategoryUiState(success = true)
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                        )
                    }
                }
        }
    }

    /**
     * Obtiene los valores de categoria por Id
     * @param id: UUID del documento
     * */
    fun loadCategory(id: String) {
        viewModelScope.launch {
//            _selectedCategory.value = repository.getCategoryById(id)
            _uiState.update { it.copy(isLoading = true) }

            val category = repository.getCategoryById(id)

            if (category != null) {
                _uiState.value = CategoryUiState(
                    idCategory = category.idCategory,
                    nameCategory = category.nameCategory,
                    descriptionCategory = category.descriptionCategory,
                    isEdit = true,
                    isLoading = false
                )
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                    )
                }
            }
        }
    }

    /**
     * Actualiza una categoría existente.
     */
    fun updateCategory() {
        val state = _uiState.value

        val category = Categories(
            idCategory = state.idCategory,
            nameCategory = state.nameCategory,
            descriptionCategory = state.descriptionCategory
        )
        viewModelScope.launch {
            repository.updateCategory(category)
                .onSuccess {
                    _uiState.value = CategoryUiState(success = true)
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                        )
                    }
                }
        }
    }

    /**
     * Elimina una categoría.
     */
    fun deleteCategory() {

        val categoryId = _uiState.value.idCategory

        if (categoryId.isBlank()) return

        viewModelScope.launch {
            repository.deleteCategory(categoryId)
                .onSuccess { _uiState.value = CategoryUiState(success = true) }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                        )
                    }
                }
        }
    }

    fun clearForm() {
        _uiState.value = CategoryUiState()
    }

    private fun validateForm(state: CategoryUiState): CategoryUiState {
        val nameResult = CategoryValidator.name(state.nameCategory)

        val isValid =
            nameResult is ValidationResult.Valid

        return state.copy(
            nameError =
                if (state.nameTouched)
                    (nameResult as? ValidationResult.Invalid)?.errorResId
                else
                    null,

            isValid = isValid
        )
    }
}