package com.example.inventarioapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventarioapp.model.Clients
import com.example.inventarioapp.repository.ClientRepository
import com.example.inventarioapp.state.ClientUiState
import com.example.inventarioapp.validators.ClientValidator
import com.example.inventarioapp.validators.model.ValidationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.String

class ClientViewModel(
    private val repository: ClientRepository = ClientRepository()
) : ViewModel() {

    private val _clients = MutableStateFlow<List<Clients>>(emptyList())
    val clients: StateFlow<List<Clients>> get() = _clients

    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> get() = _uiMessage

    private val _selectedClient = MutableStateFlow<Clients?>(null)
    val selectedClient: StateFlow<Clients?> get() = _selectedClient

    private val _uiState = MutableStateFlow(ClientUiState())
    val uiState: StateFlow<ClientUiState> = _uiState

    /*
    * Setters para el support del state hoisting
    */

    fun onNameChange(value: String) {
        _uiState.value = validateForm(
            _uiState.value.copy(
                nameClient = value,
                nameTouched = true
            )
        )
    }

    fun onNameBlur(){
        _uiState.value = validateForm(
            _uiState.value.copy(nameTouched = true)
        )
    }

    fun onApePChange(value: String) {
        _uiState.value = validateForm(
            _uiState.value.copy(
                apePClient = value,
                apePTouched = true
            )
        )
    }

    fun onApePBlur() {
        _uiState.value = validateForm(
            _uiState.value.copy(apePTouched = true)
        )
    }

    fun onApeMChange(value: String) {
        _uiState.value.copy(apeMClient = value)
    }

    fun onTelephone(value: String) {
        _uiState.value = validateForm(
            _uiState.value.copy(
                telephone = value.filter { it.isDigit() },
                telephoneTouched = true
            )
        )
    }

    fun onTelephoneBlur() {
        _uiState.value = validateForm(
            _uiState.value.copy(telephoneTouched = true)
        )
    }

    fun startCreate() {
        _uiState.value = ClientUiState()
    }

    init {

        viewModelScope.launch {
            repository.getClients().collect { fetchedList ->
                _clients.value = fetchedList
            }
        }
    }

    fun addClient() {
        val validatedState = validateForm(
            _uiState.value.copy(
                nameTouched = true,
                apePTouched = true,
                telephoneTouched = true
            )
        )

        _uiState.value = validatedState

        if (!validatedState.isValid) return

        val newClient = Clients(
            idClient = UUID.randomUUID().toString(),
            nameClient = validatedState.nameClient,
            apePClient = validatedState.apePClient,
            apeMClient = validatedState.apeMClient,
            telephone = validatedState.telephone
        )


        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, success = false) }

            repository.addClient(newClient)
                .onSuccess {
                    _uiState.value = ClientUiState(success = true)
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

    fun loadClient(idClient: String) {
        viewModelScope.launch {
//            _selectedClient.value = repository.getClientById(idClient)

            _uiState.update { it.copy(isLoading = true) }

            val client = repository.getClientById(idClient)

            if (client != null) {
                _uiState.value = ClientUiState(
                    isLoading = false,
                    isEdit = true,
                    idClient = client.idClient,
                    nameClient = client.nameClient,
                    apePClient = client.apePClient,
                    apeMClient = client.apeMClient,
                    telephone = client.telephone,
                )
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Cliente no encontrado"
                    )
                }
            }
        }
    }

    fun updateClient() {
        val state = _uiState.value

        Log.w("ClientViewModel", "Valor de idClient: ${state.idClient}")

        val client = Clients(
            idClient = state.idClient ?: return,
            nameClient = state.nameClient,
            apePClient = state.apePClient,
            apeMClient = state.apeMClient,
            telephone = state.telephone
        )
        viewModelScope.launch {
            repository.updateClient(client)
                .onSuccess {
                    _uiState.value = ClientUiState(success = true)
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

    fun deleteClient() {
        val clientId = _uiState.value.idClient

        if (clientId.isBlank()) return
        viewModelScope.launch {
//            val result = repository.deleteClient(clientId)
//
//            result
//                .onSuccess { _uiMessage.value = "SUCCEEDED_DELETE_CLIENT" }
//                .onFailure { e -> _uiMessage.value = "ERROR_DELETE_CLIENT: ${e.message}" }

            _uiState.update { it.copy(isLoading = true, errorMessage = null, success = false) }

            val result = repository.deleteClient(clientId)

            result
                .onSuccess {
                    _uiState.value = ClientUiState(success = true)
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

    fun clearForm() {
        _uiState.value = ClientUiState()
    }

    private fun validateForm(state: ClientUiState): ClientUiState {

        val nameResult = ClientValidator.name(state.nameClient)
        val apePResult = ClientValidator.apeP(state.apePClient)
        val phoneResult = ClientValidator.telephone(state.telephone)

        val isValid =
            nameResult is ValidationResult.Valid &&
                    apePResult is ValidationResult.Valid &&
                    phoneResult is ValidationResult.Valid

        return state.copy(
            nameError =
                if (state.nameTouched)
                    (nameResult as? ValidationResult.Invalid)?.errorResId
                else
                    null,

            apePError =
                if (state.apePTouched)
                    (apePResult as? ValidationResult.Invalid)?.errorResId
                else
                    null,
            telephoneError =
                if (state.telephoneTouched)
                    (phoneResult as? ValidationResult.Invalid)?.errorResId
                else
                    null,
            isValid = isValid
        )
    }
}