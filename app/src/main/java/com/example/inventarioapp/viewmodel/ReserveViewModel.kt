package com.example.inventarioapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventarioapp.constants.MovementType
import com.example.inventarioapp.model.InventoryMovements
import com.example.inventarioapp.model.Products
import com.example.inventarioapp.model.Reserves
import com.example.inventarioapp.repository.CatalogsRepository
import com.example.inventarioapp.repository.InventoryRepository
import com.example.inventarioapp.repository.ReservesRepository
import com.example.inventarioapp.state.ReserveUiState
import com.example.inventarioapp.validators.ReserveValidator
import com.example.inventarioapp.validators.model.ValidationResult
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class ReserveViewModel(
    private val repository: ReservesRepository = ReservesRepository(),
    private val catalogsRepository: CatalogsRepository = CatalogsRepository(),
    private val inventoryRepository: InventoryRepository = InventoryRepository()
) : ViewModel() {
    private val _reserves = MutableStateFlow<List<Reserves>>(emptyList())
    val reserves: StateFlow<List<Reserves>> get() = _reserves

    val products = MutableStateFlow<List<Products>>(emptyList())

    private val _selectedReserve =  MutableStateFlow<Reserves?>(null)

    val selectedReserve: MutableStateFlow<Reserves?> get() = _selectedReserve


    private val _uiState = MutableStateFlow(ReserveUiState())
    val uiState: StateFlow<ReserveUiState> get() = _uiState

    /*
    * Setters para el support del state hoisting
    */

    fun onIdClient(value: String) {
        _uiState.value = validateForm(
            _uiState.value.copy(
                idClient = value,
                idClientTouched = true
            )
        )
    }

    fun onIdClientBlur() {
        _uiState.value = validateForm(
            _uiState.value.copy(
                idClientTouched = true
            )
        )
    }

    fun onIdProduct(value: String) {
        _uiState.value = validateForm(
            _uiState.value.copy(
                idProduct = value,
                idProductTouched = true
            )
        )
    }

    fun onIdProductBlur() {
        _uiState.value = validateForm(
            _uiState.value.copy(
                idProductTouched = true
            )
        )
    }

    fun onEndReserve(value: Date) {
        _uiState.value = validateForm(
            _uiState.value.copy(
                endReserve = value,
                endReserveTouched = true
            )
        )
    }

    fun onEndReserveBlur() {
        _uiState.value = validateForm(
            _uiState.value.copy(
                endReserveTouched = true
            )
        )
    }

    fun onAmount(value: String) {
        _uiState.value = validateForm(
            _uiState.value.copy(
                amount = value.toDoubleOrNull() ?: 0.0,
                amountTouched = true
            )
        )
    }

    fun onAmountBlur() {
        _uiState.value = validateForm(
            _uiState.value.copy(
                amountTouched = true
            )
        )
    }

    fun startCreate() {
        _uiState.value = ReserveUiState()
    }

    init {
        viewModelScope.launch {
            repository.getReserves().collect { fetchedList ->
                _reserves.value = fetchedList
            }
        }
    }

    fun loadProductsCatalog() {
        viewModelScope.launch {
            try {
                val result = catalogsRepository.getProducts()
                products.value = result
            } catch (e: Exception) {

            }
        }
    }

    fun addReserve() {
        val validateState = validateForm(
            _uiState.value.copy(
                idClientTouched = true,
                idProductTouched = true,
                endReserveTouched = true,
                amountTouched = true,
                qtyReserveTouched = true,
            )
        )

        _uiState.value = validateState

        if (!validateState.isValid) return
        loadProductsCatalog()
        val productReserved = products.value.find {
            it.idProduct == validateState.idProduct
        }
        if (productReserved == null) return

        val reserve = Reserves(
            idReserves = UUID.randomUUID().toString(),
            idClient = validateState.idClient,
            idProduct = validateState.idProduct,
            reservedAt = Timestamp.now(),
            endReserve = validateState.endReserve,
            priceAtReserve = productReserved.priceProduct,
            amount = validateState.amount,
            qtyReserve = validateState.qtyReserve,
            isFinalized = false,
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, success = false) }
            repository.addReserve(reserve)
                .onSuccess {
                    _uiState.value = ReserveUiState(
                        success = true,
                        idClientTouched = false,
                        idProductTouched = false,
                        endReserveTouched = false,
                        amountTouched = false,
                        qtyReserveTouched = false,
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

            val movements = InventoryMovements(
                id = UUID.randomUUID().toString(),
                productId = productReserved.idProduct,
                quantity = reserve.qtyReserve,
                type = MovementType.RESERVE,
                reason = "Apartado",
                createdAt = Timestamp.now()
            )

            inventoryRepository.saveInventoryMovements(listOf(movements))
        }
    }

    private fun validateForm(state: ReserveUiState): ReserveUiState {
        val idClientResult = ReserveValidator.idClient(state.idClient)
        val idProductResult = ReserveValidator.idProduct(state.idProduct)
        val endReserveResult = ReserveValidator.endReserve(state.endReserve)
        val amountResult = ReserveValidator.amount(state.amount.toString(), "10000.00")
        val qtyReserveResult = ReserveValidator.qty(state.qtyReserve.toString())

        val isValid =
            idClientResult is ValidationResult.Valid &&
                    idProductResult is ValidationResult.Valid &&
                    endReserveResult is ValidationResult.Valid &&
                    amountResult is ValidationResult.Valid &&
                    qtyReserveResult is ValidationResult.Valid

        return state.copy(
            idClientError =
                if (state.idClientTouched)
                    (idClientResult as? ValidationResult.Invalid)?.errorResId
                else
                    null,

            idProductError =
                if (state.idProductTouched)
                    (idProductResult as? ValidationResult.Invalid)?.errorResId
                else
                    null,

            endReserveError =
                if (state.endReserveTouched)
                    (endReserveResult as? ValidationResult.Invalid)?.errorResId
                else
                    null,

            amountError =
                if (state.amountTouched)
                    (amountResult as? ValidationResult.Invalid)?.errorResId
                else
                    null,

            qtyReserveError =
                if (state.qtyReserveTouched)
                    (amountResult as? ValidationResult.Invalid)?.errorResId
                else
                    null,

            isValid = isValid

        )
    }

    fun loadReserve(idReserve: String){
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val reserve = repository.getReserveById(idReserve)

            if (reserve != null) {
                val reserveState = _uiState.value
                _uiState.value = ReserveUiState(
                    idReserve = reserveState.idReserve,
                    idClient = reserveState.idClient,
                    idProduct = reserveState.idProduct,
                    reservedAt = reserveState.reservedAt,
                    endReserve = reserveState.endReserve,
                    qtyReserve = reserveState.qtyReserve,
                    amount = reserveState.amount,
                    isEdit = true,
                    isLoading = false
                )
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "No se encontró apartado"
                    )
                }
            }
            _selectedReserve.value = repository.getReserveById(idReserve)
        }
    }

    fun clearForm(){
        _uiState.value = ReserveUiState()
    }
}