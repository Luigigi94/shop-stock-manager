package com.example.inventarioapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventarioapp.model.Products
import com.example.inventarioapp.model.Supplier
import com.example.inventarioapp.model.SupplierPurchase
import com.example.inventarioapp.model.SupplierPurchaseItem
import com.example.inventarioapp.repository.CatalogsRepository
import com.example.inventarioapp.repository.SupplierPurchaseRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class SupplierPurchaseViewModel(
    private val repository: SupplierPurchaseRepository = SupplierPurchaseRepository(),
    private val catalogsRepository: CatalogsRepository = CatalogsRepository()
) : ViewModel() {

    /* ---------- state ---------- */

    val products = MutableStateFlow<List<Products>>(emptyList())

    private val _items = MutableStateFlow<List<SupplierPurchaseItem>>(emptyList())
    val items: StateFlow<List<SupplierPurchaseItem>> = _items

    private val _supplier = MutableStateFlow<Supplier?>(null)
    val supplier: StateFlow<Supplier?> = _supplier

    private val _total = MutableStateFlow(0.0)
    val total: StateFlow<Double> = _total

    /* ---------- init ---------- */

    fun loadCatalogs() = viewModelScope.launch {
        products.value = catalogsRepository.getProducts()
    }

    /* ---------- lógica items (igual que tu carrito) ---------- */

    fun addOrUpdateItem(product: Products, quantity: Int, cost: Double) {

        val existingIndex = _items.value.indexOfFirst { it.productId == product.idProduct }

        val updated = if (existingIndex != -1) {
            _items.value.mapIndexed { i, item ->
                if (i == existingIndex) {
                    val newQty = item.quantity + quantity
                    item.copy(
                        quantity = newQty,
                        cost = cost,
                        subtotal = newQty * cost
                    )
                } else item
            }
        } else {
            _items.value + SupplierPurchaseItem(
                productId = product.idProduct,
                productName = product.nameProduct,
                quantity = quantity,
                cost = cost,
                subtotal = cost * quantity
            )
        }

        _items.value = updated
        _total.value = updated.sumOf { it.subtotal }
    }

    fun updateItemQuantity(productId: String, quantity: Int) {
        val updated = _items.value.map {
            if (it.productId == productId)
                it.copy(quantity = quantity, subtotal = quantity * it.cost)
            else it
        }

        _items.value = updated
        _total.value = updated.sumOf { it.subtotal }
    }

    fun removeItem(productId: String) {
        val updated = _items.value.filterNot { it.productId == productId }

        _items.value = updated
        _total.value = updated.sumOf { it.subtotal }
    }

    /* ---------- proveedor ---------- */

    fun setSupplier(s: Supplier) {
        _supplier.value = s
    }

    /* ---------- confirmar compra proveedor ---------- */

    fun confirmPurchase(userId: String, onResult: (Boolean) -> Unit) {

        val supplier = _supplier.value ?: return
        val items = _items.value

        if (items.isEmpty()) return

        val purchaseId = UUID.randomUUID().toString()

        val purchase = SupplierPurchase(
            id = purchaseId,
            supplierId = supplier.idSupplier,
            supplierName = supplier.name,
            items = items,
            totalCost = _total.value,
            createdAt = Timestamp.now(),
            userId = userId
        )

        viewModelScope.launch {

            val result = repository.registerSupplierPurchase(purchase)

            if (result != null) {
                // limpiar estado si éxito
                _items.value = emptyList()
                _total.value = 0.0
                _supplier.value = null
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }
}