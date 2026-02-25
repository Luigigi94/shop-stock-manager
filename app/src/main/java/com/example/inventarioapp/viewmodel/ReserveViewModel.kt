package com.example.inventarioapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.inventarioapp.model.Reserves
import com.example.inventarioapp.repository.ReservesRepository
import com.example.inventarioapp.state.ReserveUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ReserveViewModel(
    private val repository: ReservesRepository
): ViewModel() {
    private val _reserves = MutableStateFlow<List<Reserves>>(emptyList())
    val reserves: StateFlow<List<Reserves>> get() = _reserves

    private val _uiState = MutableStateFlow(ReserveUiState())
    val uiState: StateFlow<ReserveUiState?> get() = _uiState

    /*
    * Setters para el support del state hoisting
    */

    private fun validateForm(state: ReserveUiState): ReserveUiState {
        val idClientResult =
        val idProductResult =
        val endReserveResult =
        val amountResult =
    }
}