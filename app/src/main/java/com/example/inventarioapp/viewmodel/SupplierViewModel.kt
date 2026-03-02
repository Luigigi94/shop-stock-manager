package com.example.inventarioapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventarioapp.model.Supplier
import com.example.inventarioapp.repository.SupplierRepository
import com.example.inventarioapp.state.SupplierUiState
import com.example.inventarioapp.validators.SupplierValidator
import com.example.inventarioapp.validators.model.ValidationResult
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class SupplierViewModel (
    private val repository: SupplierRepository = SupplierRepository()
): ViewModel(){

    private val _suppliers = MutableStateFlow<List<Supplier>>(emptyList())
    val suppliers: StateFlow<List<Supplier>> = _suppliers

    private val _uiState = MutableStateFlow(SupplierUiState())
    val uiState: StateFlow<SupplierUiState> = _uiState

    private inline fun updateUI(block: SupplierUiState.() -> SupplierUiState){
        _uiState.value = validateForm(_uiState.value.block())
    }

    /**
     * Setters para el support del state hoisting
     * */

    fun onNameSupplier(value: String) =
        updateUI { copy(name = value, nameTouched = true) }

    fun onTelephoneSupplier(value: String) =
        updateUI { copy(phone = value, phoneTouched = true) }

    fun onIdentifierAccountSupplier(value: String) =
        updateUI { copy(identifierAccount = value, identifierAccountTouched = true) }

    fun onIdBankSupplier(value: String) =
        updateUI { copy(idBank = value, idBankTouched = true) }

    fun onNameBlur(){
        _uiState.value = validateForm(
            _uiState.value.copy(
                nameTouched = true
            )
        )
    }

    fun onPhoneBlur(){
        _uiState.value = validateForm(
            _uiState.value.copy(
                phoneTouched = true
            )
        )
    }

    fun onIdentifierAccountBlur(){
        _uiState.value = validateForm(
            _uiState.value.copy(
                identifierAccountTouched = true
            )
        )
    }
    fun onBankBlur(){
        _uiState.value = validateForm(
            _uiState.value.copy(
                idBankTouched = true
            )
        )
    }

    init {
        viewModelScope.launch {
            repository.getSuppliers().collect { fetchedList ->
                _suppliers.value = fetchedList
            }
        }
    }

    fun addSupplier() {
        val validatedState = validateForm(
            _uiState.value.copy(
                nameTouched = true,
                phoneTouched = true,
                identifierAccountTouched = true
            )
        )

        _uiState.value = validatedState

        if (!validatedState.isValid) return

        val newSupplier = Supplier(
            idSupplier = UUID.randomUUID().toString(),
            name = validatedState.name,
            phone = validatedState.phone,
            identifierAccount = validatedState.identifierAccount,
            idBank = validatedState.idBank,
            createdAt = Timestamp.now()
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, success = false) }

            repository.addSupplier(newSupplier)
                .onSuccess {
                    _uiState.value = SupplierUiState(
                            success = true,
                            isLoading = false
                        )
                    }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message
                        )
                    }
                }
        }
    }

    fun loadSupplier(idSupplier: String){
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val supplier = repository.getSupplierById(idSupplier)

            if (supplier != null){
                _uiState.value = SupplierUiState(
                    isLoading = false,
                    isEdit = true,
                    success = false,
                    idSupplier = supplier.idSupplier,
                    name = supplier.name,
                    phone = supplier.phone,
                    identifierAccount = supplier.identifierAccount,
                    idBank = supplier.idBank,
                )
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Proveedor no encontrado"
                    )
                }
            }
        }
    }

    fun updateSupplier(){
        val state = _uiState.value
        val supplier = Supplier(
            idSupplier = state.idSupplier,
            name = state.name,
            phone = state.phone,
            identifierAccount = state.identifierAccount,
            idBank = state.idBank
        )
        viewModelScope.launch {
            repository.updateSupplier(supplier)
                .onSuccess { _uiState.update {  it.copy(success = true, isLoading = false) } }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message
                        )
                    }
                }
        }
    }

    fun deleteSupplier(){
        val supplierId = _uiState.value.idSupplier

        if (supplierId.isBlank()) return
        viewModelScope.launch {

            _uiState.update { it.copy(isLoading = true, errorMessage = null, success = false) }

            val result = repository.deleteSupplier(supplierId)

            result
                .onSuccess {
                    _uiState.value = SupplierUiState(success = true)
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message
                        )
                    }
                }
        }
    }

    fun startCreate(){
        _uiState.value = SupplierUiState()
    }

    private fun validateForm(state: SupplierUiState): SupplierUiState{
        val nameResult = SupplierValidator.name(state.name)
        val telephoneResult = SupplierValidator.telephone(state.phone)
        val identifierAccountResult = SupplierValidator.identifierAccount(state.identifierAccount)
        val idBankResult = SupplierValidator.idBank(state.idBank)

        val isValid =
            nameResult is ValidationResult.Valid &&
                    telephoneResult is ValidationResult.Valid &&
                    identifierAccountResult is ValidationResult.Valid &&
                    idBankResult is ValidationResult.Valid

        return state.copy(
            nameError =
                if (state.nameTouched)
                    (nameResult as? ValidationResult.Invalid)?.errorResId
            else
            null,
            phoneError =
                if (state.phoneTouched)
                    (telephoneResult as? ValidationResult.Invalid)?.errorResId
            else
            null,
            identifierAccountError =
                if (state.identifierAccountTouched)
                    (identifierAccountResult as? ValidationResult.Invalid)?.errorResId
            else
            null,
            idBankError =
                if (state.idBankTouched)
                    (idBankResult as? ValidationResult.Invalid)?.errorResId
            else
            null,
            isValid = isValid
        )
    }
}