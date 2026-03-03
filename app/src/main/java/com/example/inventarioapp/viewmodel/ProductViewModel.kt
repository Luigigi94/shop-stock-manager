package com.example.inventarioapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventarioapp.constants.MovementType
import com.example.inventarioapp.model.InventoryMovements
import com.example.inventarioapp.model.Products
import com.example.inventarioapp.repository.InventoryRepository
import com.example.inventarioapp.repository.ProductRepository
import com.example.inventarioapp.state.ProductUiState
import com.example.inventarioapp.validators.ProductValidator
import com.example.inventarioapp.validators.model.ValidationResult
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class ProductViewModel(
    private val productRepository: ProductRepository = ProductRepository(),
    private val inventoryRepository: InventoryRepository = InventoryRepository()
) : ViewModel() {

    private val _products = MutableStateFlow<List<Products>>(emptyList())
    val products: StateFlow<List<Products>> get() = _products

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
                quantityProduct = value.toIntOrNull() ?: 0,
                quantityTouched = true
            )
        )
    }

    fun onQuantityBlur() {
        _uiState.value = validateForm(_uiState.value.copy(quantityTouched = true))
    }

    fun onDescriptionProduct(value: String) {
        _uiState.value = _uiState.value.copy(descriptionProduct = value)
    }

    fun onPriceProduct(value: String) {
        _uiState.value = validateForm(
            _uiState.value.copy(
                priceProduct = value.toDouble(),
                priceTouched = true
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

    fun startCreate() {
        _uiState.value = ProductUiState()
    }

    init {
        viewModelScope.launch {
            productRepository.getProducts().collect { fetchedList ->
                _products.value = fetchedList
            }
        }
    }

    fun quickAddProduct(products: Products){
        val quickProducts
    }

    fun addProduct() {
        Log.d("ProductVM_addProduct", "valida que entre al addProduct")
        val validateState = validateForm(
            _uiState.value.copy(
                nameTouched = true,
                quantityTouched = true,
                priceTouched = true,
                idCategoryTouched = true,
            )
        )

        Log.d("ProductVM_addProduct", "valida validateState: $validateState")
        _uiState.value = validateState

        if (!validateState.isValid) return
        val product = Products(
            idProduct = UUID.randomUUID().toString(),
            nameProduct = validateState.nameProduct,
            descriptionProduct = validateState.descriptionProduct,
            priceProduct = validateState.priceProduct,
            idCategory = validateState.idCategory?: "",
            createdAt = Timestamp.now(),
            stock = validateState.quantityProduct
        )
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, success = false) }
            productRepository.addProduct(product)
                .onSuccess {
                    _uiState.value = ProductUiState(
                        success = true,
                        nameTouched = false,
                        quantityTouched = false,
                        priceTouched = false,
                        idCategoryTouched = false
                    )
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = exception.message
                        )
                    }
                }
//            val quantity = _uiState.value.quantityProduct.toIntOrNull() ?: 0

            val movements = InventoryMovements(
                id = UUID.randomUUID().toString(),
                productId = product.idProduct,
                quantity = product.stock ,
                type = MovementType.PURCHASE,
                reason = "Inventario",
//                referenceId =
//                userId =
                createdAt = Timestamp.now()
            )

            inventoryRepository.saveInventoryMovements(listOf(movements))
        }
    }

    fun loadProduct(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val product = productRepository.getProductById(id)

            if (product != null) {
                _uiState.value = ProductUiState(
                    idProduct = product.idProduct,
                    nameProduct = product.nameProduct,
                    descriptionProduct = product.descriptionProduct,
                    priceProduct = product.priceProduct,
                    idCategory = product.idCategory,
                    isEdit = true,
                    isLoading = false,
                    quantityProduct = product.stock
                )
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Producto no encontrado"
                    )
                }
            }
            _selectedProduct.value = productRepository.getProductById(id)
        }
    }

    fun updateProduct() {
        val state = _uiState.value
        val product = Products(
            idProduct = state.idProduct,
            nameProduct = state.nameProduct,
            descriptionProduct = state.descriptionProduct,
            priceProduct = state.priceProduct,
            idCategory = state.idCategory?: "",
            updatedAt = Timestamp.now(),
            stock = state.quantityProduct
        )
        viewModelScope.launch {
            productRepository.updateProduct(product)
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
            productRepository.deleteProduct(productId)
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
        val priceResult = ProductValidator.price(state.priceProduct.toString())
        val idCategoryResult = ProductValidator.idCategory(state.idCategory)

        val isValid =
            nameResult is ValidationResult.Valid &&
                    quantityResult is ValidationResult.Valid &&
                    priceResult is ValidationResult.Valid &&
                    idCategoryResult is ValidationResult.Valid

        return state.copy(
            nameError =
                if (state.nameTouched)
                    (nameResult as? ValidationResult.Invalid)?.errorResId
                else
                    null,

            quantityError =
                if(state.quantityTouched)
                        (quantityResult as? ValidationResult.Invalid)?.errorResId
                else
                    null,


            priceError =
                if (state.priceTouched)
                    (priceResult as? ValidationResult.Invalid)?.errorResId
                else
                    null,

            idCategoryError =
                if (state.idCategoryTouched)
                    (idCategoryResult as? ValidationResult.Invalid)?.errorResId
                else
                    null,

            isValid = isValid

        )
    }
}