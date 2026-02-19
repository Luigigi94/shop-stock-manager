package com.example.inventarioapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventarioapp.model.Products
import com.example.inventarioapp.repository.ProductRepository
import com.example.inventarioapp.state.ProductUiState
import com.example.inventarioapp.validators.ClientValidator
import com.example.inventarioapp.validators.ProductValidator
import com.example.inventarioapp.validators.model.ValidationResult
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class ProductViewModel(
    private val repository: ProductRepository = ProductRepository()
) : ViewModel() {
    private val _products = MutableStateFlow<List<Products>>(emptyList())
    val products: StateFlow<List<Products>> get() = _products

//    private val _uiMessage = MutableStateFlow<String?>(null)
//    val uiMessage: MutableStateFlow<String?> get() = _uiMessage

    private val _selectedProduct = MutableStateFlow<Products?>(null)
    val selectedProduct: MutableStateFlow<Products?> get() = _selectedProduct

    private val _uiState = MutableStateFlow(ProductUiState())

    val uiState: MutableStateFlow<ProductUiState> = _uiState

    /*
    * Setters para el support del state hoisting
    */

    fun onNameProduct(value: String) {
        _uiState.value = validateForm(
            _uiState.value.copy(
                nameProduct = value,
                nameTouched = true
            )
        )
    }

    fun onNameBlur() {
        _uiState.value = validateForm(
            _uiState.value.copy(nameTouched = true)
        )
    }

    fun onQuantityProduct(value: String) {
        _uiState.value = validateForm(
            _uiState.value.copy(
                quantityProduct = value.toInt(),
                quantityTouched = true
            )
        )
    }

    fun onQuantityBlur() {
        _uiState.value = validateForm(
            _uiState.value.copy(quantityTouched = true)
        )
    }

    fun onDescriptionProduct(value: String) {
        _uiState.value = _uiState.value.copy(descriptionProduct = value)
    }

    fun onPriceProduct(value: String) {
        _uiState.value = validateForm(
            _uiState.value.copy(
                priceProduct = value.toDouble(),
                quantityTouched = true
            )
        )
    }

    fun onPriceBlur() {
        _uiState.value = validateForm(
            _uiState.value.copy(priceTouched = true)
        )
    }

    fun onIdCategory(value: String) {
        _uiState.value = validateForm(
            _uiState.value.copy(
                idCategory = value,
                idCategoryTouched = true
            )
        )
    }

    fun onIdCategoryBlur() {
        _uiState.value = validateForm(
            _uiState.value.copy(idCategoryTouched = true)
        )
    }

    fun startCreate() {
        _uiState.value = ProductUiState()
    }

    init {
        viewModelScope.launch {
            repository.getProducts().collect { fetchedList ->
                _products.value = fetchedList
            }
        }
    }

    fun addProduct() {
        val state = _uiState.value

        if (state.nameProduct.isBlank()) return

        val product = Products(
            idProduct = UUID.randomUUID().toString(),
            nameProduct = state.nameProduct,
            quantityProduct = state.quantityProduct,
            descriptionProduct = state.descriptionProduct,
            priceProduct = state.priceProduct,
            idCategory = state.idCategory,
            createdAt = Timestamp.now(),
        )
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, success = false) }
            repository.addProduct(product)
                .onSuccess {
                    _uiState.value = ProductUiState(success = true)
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = exception.message
                        )
                    }
                }
        }
    }

    fun loadProduct(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val product = repository.getProductById(id)

            if (product != null) {
                _uiState.value = ProductUiState(
                    idProduct = product.idProduct,
                    nameProduct = product.nameProduct,
                    quantityProduct = product.quantityProduct,
                    descriptionProduct = product.descriptionProduct,
                    priceProduct = product.priceProduct,
                    idCategory = product.idCategory,
                    isEdit = true,
                    isLoading = false
                )
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Producto no encontrado"
                    )
                }
            }
            _selectedProduct.value = repository.getProductById(id)
        }
    }

    fun updateProduct() {
        val state = _uiState.value
        val product = Products(
            idProduct = state.idProduct,
            nameProduct = state.nameProduct,
            quantityProduct = state.quantityProduct,
            descriptionProduct = state.descriptionProduct,
            priceProduct = state.priceProduct,
            idCategory = state.idCategory,
            updatedAt = Timestamp.now(),
        )
        viewModelScope.launch {
            repository.updateProduct(product)
                .onSuccess { _uiState.update { it.copy(success = true) } }
                .onFailure { err ->
                    uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = err.message
                        )
                    }
                }
        }
    }

    fun deleteProduct() {
        val productId = _uiState.value.idProduct

        if (productId.isBlank()) return
        viewModelScope.launch {
            repository.deleteProduct(productId)
                .onSuccess { _uiState.value = ProductUiState(success = true) }
                .onFailure { err ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = err.message
                        )
                    }
                }
        }
    }

    fun clearForm() {
        _uiState.value = ProductUiState()
    }

    private fun validateForm(state: ProductUiState): ProductUiState {
        val nameResult = ProductValidator.name(state.nameProduct)
        val quantityResult = ProductValidator.quantity(state.quantityProduct.toString())
        val priceResult = ProductValidator.quantity(state.priceProduct.toString())
        val idCategoryResult = ProductValidator.quantity(state.idCategory)

        val isValid =
            nameResult is ValidationResult.Valid &&
                    quantityResult is ValidationResult.Valid &&
                    priceResult is ValidationResult.Valid &&
                    idCategoryResult is ValidationResult.Valid

        return state.copy(
            nameError =
                if (state.nameTouched)
                    (nameResult as? ValidationResult.Invalid)?.message
                else
                    null,

            quantityError =
                if (state.quantityTouched)
                    (quantityResult as? ValidationResult.Invalid)?.message
                else
                    null,

            priceError =
                if (state.priceTouched)
                    (priceResult as? ValidationResult.Invalid)?.message
                else
                    null,

            idCategoryError =
                if (state.idCategoryTouched)
                    (idCategoryResult as? ValidationResult.Invalid)?.message
                else
                    null,

            isValid = isValid

        )
    }
}