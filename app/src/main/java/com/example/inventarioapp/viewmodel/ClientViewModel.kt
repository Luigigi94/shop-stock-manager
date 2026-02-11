package com.example.inventarioapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventarioapp.model.Clients
import com.example.inventarioapp.repository.ClientRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ClientViewModel (
    private val repository: ClientRepository = ClientRepository()
): ViewModel(){

    private val _clients = MutableStateFlow<List<Clients>>(emptyList())
    val clients: StateFlow<List<Clients>> get() = _clients

    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> get() = _uiMessage

    private val _selectedClient = MutableStateFlow<Clients?>(null)
    val selectedClient: StateFlow<Clients?> get() = _selectedClient

    init {

        viewModelScope.launch {
            repository.getClients().collect { fetchedList ->
                _clients.value = fetchedList
            }
        }
    }

    fun addClient(client: Clients){
        viewModelScope.launch {
            val result = repository.addClient(client)

            result
                .onSuccess { _uiMessage.value = "SUCCEEDED_ADD_CLIENT" }
                .onFailure { e-> _uiMessage.value = "ERROR_ADD_CLIENT: ${e.message}" }
        }
    }

    fun loadClient(idClient: String){
        viewModelScope.launch {
            _selectedClient.value = repository.getClientById(idClient)
        }
    }

    fun updateClient(client: Clients) {
        viewModelScope.launch {
            val result = repository.updateClient(client)

            result
                .onSuccess { _uiMessage.value = "SUCCEEDED_UPDATE_CLIENT" }
                .onFailure { e -> _uiMessage.value = "ERROR_UPDATE_CLIENT: ${e.message}" }
        }
    }

    fun deleteClient(clientId: String) {
        viewModelScope.launch {
            val result = repository.deleteClient(clientId)

            result
                .onSuccess { _uiMessage.value = "SUCCEEDED_DELETE_CLIENT" }
                .onFailure { e -> _uiMessage.value = "ERROR_DELETE_CLIENT: ${e.message}" }
        }
    }
}