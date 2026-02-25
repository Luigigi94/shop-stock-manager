package com.example.inventarioapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.inventarioapp.model.Reserves
import com.example.inventarioapp.repository.ReservesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ReserveViewModel(
    private val repository: ReservesRepository
): ViewModel() {
    private val _reserves = MutableStateFlow<List<Reserves>>(emptyList())
    val reserves: StateFlow<List<Reserves>> get() = _reserves

    private val _uiState = MutableStateFlow()
}