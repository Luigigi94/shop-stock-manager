package com.example.inventarioapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventarioapp.constants.MovementType
import com.example.inventarioapp.model.Clients
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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class ReserveViewModel(
    private val repository: ReservesRepository = ReservesRepository(),
    private val catalogsRepository: CatalogsRepository = CatalogsRepository(),
    private val inventoryRepository: InventoryRepository = InventoryRepository()
) : ViewModel() {
    private val _products = MutableStateFlow<List<Products>>(emptyList())
    val products: StateFlow<List<Products>> = _products.asStateFlow()

    private val _clients = MutableStateFlow<List<Clients>>(emptyList())
    val clients: StateFlow<List<Clients>> = _clients.asStateFlow()


    private val _reserves = MutableStateFlow<List<Reserves>>(emptyList())
    val reserves: StateFlow<List<Reserves>> get() = _reserves

    private val _movements = MutableStateFlow<List<InventoryMovements>>(emptyList())
    val movements: StateFlow<List<InventoryMovements>> get() = _movements


    private val _selectedReserve = MutableStateFlow<Reserves?>(null)
    val selectedReserve: StateFlow<Reserves?> get() = _selectedReserve


    private val _uiState = MutableStateFlow(ReserveUiState())
    val uiState: StateFlow<ReserveUiState> get() = _uiState
    val totalPayments: StateFlow<Double> = combine(_movements, _reserves){ movement, reserve ->
        val currentReserve = reserve.find { it.idReserves == _uiState.value.idReserve }
        val priceAtReserve = currentReserve?.priceAtReserve ?: 0.0
        val sumAmounts = movement.sumOf { it.amount ?: 0.0 }

        priceAtReserve - sumAmounts
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    /*
    * Setters para el support del state hoisting
    */

    private inline fun updateUi(block: ReserveUiState.() -> ReserveUiState) {
        _uiState.value = validateForm(_uiState.value.block())
    }

    fun onIdClient(value: String) =
        updateUi { copy(idClient = value, idClientTouched = true) }

    fun onIdProduct(value: String) =
    updateUi { copy(idProduct = value, idProductTouched = true) }

    fun onEndReserve(value: Date) =
        updateUi { copy(endReserve = value, endReserveTouched = true) }

    fun onAmount(value: String) =
        updateUi { copy(amount = value.toDoubleOrNull() ?: 0.0, amountTouched = true) }

    fun onAmountBlur() {
        _uiState.value = validateForm(
            _uiState.value.copy(
                amountTouched = true
            )
        )
    }

    fun onQtyReserve(value: String) =
        updateUi { copy(qtyReserve = value.toIntOrNull() ?: 0, qtyReserveTouched = true) }

    fun onQtyReserveBlur() {
        _uiState.value = validateForm(
            _uiState.value.copy(qtyReserveTouched = true)
        )
    }

    fun startCreate() {
        _uiState.value = ReserveUiState()
    }

    init {
        viewModelScope.launch {
            loadCatalogs()

            repository.getReserves().collect { fetchedList ->
                _reserves.value = fetchedList
            }
        }
    }

    private suspend fun loadCatalogs() {
        try {
            _products.value = catalogsRepository.getProducts()
            _clients.value = catalogsRepository.getClients()
        } catch (_: Exception) {}
    }

    /*
    * Helper para crear los movements
    * */

    private fun buildMovement(
        productId: String,
        qty: Int,
        amount: Double,
        referenceId: String
    ) = InventoryMovements(
        id = UUID.randomUUID().toString(),
        productId = productId,
        quantity = qty,
        amount = amount,
        type = MovementType.RESERVE,
        reason = "Apartado",
        referenceId = referenceId,
        createdAt = Timestamp.now()
    )

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

        val productReserved =
            _products.value.find { it.idProduct == validateState.idProduct }
                ?: return

        val reserve = Reserves(
            idReserves = UUID.randomUUID().toString(),
            idClient = validateState.idClient,
            idProduct = validateState.idProduct,
            reservedAt = Timestamp.now(),
            endReserve = validateState.endReserve,
            priceAtReserve = productReserved.priceProduct,
            amount = validateState.amount,
            qtyReserve = validateState.qtyReserve,
            originalQty = validateState.qtyReserve,
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
            val movements = buildMovement(productId = productReserved.idProduct, qty = reserve.qtyReserve, amount = reserve.amount, referenceId = reserve.idReserves,)
            val diff = productReserved.stock - reserve.qtyReserve

            inventoryRepository.saveInventoryMovements(listOf(movements))
            repository.applyReserveMovements(reserve, movements, diff)

        }
    }

    fun updateReserve(){
        val state = _uiState.value
        val actualData = _selectedReserve.value
        val actualAmount = actualData?.amount ?: 0.0
        val originalQty = actualData?.originalQty ?: 0
        val totalDebt = (originalQty.toDouble() * state.priceAtReserve.toDouble())
        val currentPaid = state.amount + actualAmount
        val reserveFinalized =  totalDebt == currentPaid
        val newDiff = state.qtyReserve - originalQty



        val reserve = Reserves(
            idReserves = state.idReserve ?: return,
            idClient = state.idClient,
            idProduct = state.idProduct,
            reservedAt = state.reservedAt,
            endReserve = state.endReserve,
            priceAtReserve = state.priceAtReserve.toDoubleOrNull() ?: 0.0,
            qtyReserve = state.qtyReserve,
            amount = state.amount,
            isFinalized = reserveFinalized
        )

        val movements = buildMovement(productId = state.idProduct, qty = reserve.qtyReserve, amount = reserve.amount,referenceId = reserve.idReserves,)

        viewModelScope.launch {
            repository.updateReserve(reserve)
                .onSuccess {
                    _uiState.value = ReserveUiState(success = true)
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message
                        )
                    }
                }
            inventoryRepository.saveInventoryMovements(listOf(movements))
            repository.applyReserveMovements(reserve, movements, newDiff)
        }
    }

    fun deleteReserve(){
        val reserveId = _uiState.value.idReserve

        if (reserveId.isNullOrBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, success = false) }

            val result = repository.deleteReserve(reserveId)

            result
                .onSuccess { _uiState.value = ReserveUiState(success = true) }
                .onFailure { e->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message
                        )
                    }
                }
        }
    }

    private fun validateForm(state: ReserveUiState): ReserveUiState {
        val productFind = products.value.find { state.idProduct == it.idProduct }

        val productPrice = productFind?.priceProduct ?: "0.0"
        val idClientResult = ReserveValidator.idClient(state.idClient)
        val idProductResult = ReserveValidator.idProduct(state.idProduct)
        val endReserveResult = ReserveValidator.endReserve(state.endReserve)
        val amountResult = ReserveValidator.amount(state.amount.toString(), productPrice.toString())
        val qtyReserveResult = ReserveValidator.qty(state.qtyReserve.toString())

        val isValid =
            idClientResult is ValidationResult.Valid &&
                    idProductResult is ValidationResult.Valid &&
                    endReserveResult is ValidationResult.Valid &&
                    amountResult is ValidationResult.Valid &&
                    qtyReserveResult is ValidationResult.Valid

        Log.d("ReserveVM ValidateForm", "Revisando validaciones: idClientResult: $idClientResult\n idProductResult: $idProductResult\n endReserveResult: $endReserveResult\n amountResult: $amountResult\n qtyReserveResult: $qtyReserveResult")

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
                    amountResult as? ValidationResult.Invalid
                else
                    null,

            qtyReserveError =
                if (state.qtyReserveTouched)
                    (qtyReserveResult as? ValidationResult.Invalid)?.errorResId
                else
                    null,

            isValid = isValid

        )
    }

    fun loadReserve(idReserve: String){
        viewModelScope.launch {

            val reserve = repository.getReserveById(idReserve) ?: return@launch

            _selectedReserve.value = reserve

            _uiState.value = ReserveUiState(
                idReserve = reserve.idReserves,
                idClient = reserve.idClient,
                idProduct = reserve.idProduct,
                reservedAt = reserve.reservedAt,
                endReserve = reserve.endReserve,
                qtyReserve = reserve.qtyReserve,
                amount = reserve.amount,
                isEdit = true
            )
        }
    }

    fun clearForm(){
        _uiState.value = ReserveUiState()
    }

    fun currentDate(): String {
        val firebaseTimestamp = Timestamp.now()
        val firebaseDate = firebaseTimestamp.toDate()

        val formatPattern = "dd/MM/yyyy HH:mm:ss"
        val dateFormat = SimpleDateFormat(formatPattern, Locale.getDefault())

// Format the Date object into a string
        val dateString: String = dateFormat.format(firebaseDate)

        return dateString
    }

    fun getClientName(idClient: String): String{
        val client = clients.value.find { it.idClient == idClient }

        return if (client != null){
            "${client.nameClient} ${client.apePClient} ${client.apeMClient}"
        } else {
            "Desconocido"
        }
    }

    fun getProductName(idProduct: String): String{
        val product = products.value.find { it.idProduct == idProduct }

        return product?.nameProduct ?: "Sin producto"
    }

    fun loadHistory(referenceId: String){
        viewModelScope.launch {
            val list = inventoryRepository.getMovementsByReference(referenceId)
            _movements.value = list
        }
    }

    fun getQtyHistory(): List<InventoryMovements>{
        return _movements.value
            .filter { it.type == MovementType.RESERVE }
    }
}