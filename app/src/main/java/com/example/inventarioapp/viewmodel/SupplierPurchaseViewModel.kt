package com.example.inventarioapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventarioapp.constants.MovementType
import com.example.inventarioapp.model.Cart
import com.example.inventarioapp.model.InventoryMovements
import com.example.inventarioapp.model.Products
import com.example.inventarioapp.model.Supplier
import com.example.inventarioapp.model.SupplierPurchase
import com.example.inventarioapp.model.SupplierPurchaseItem
import com.example.inventarioapp.repository.CatalogsRepository
import com.example.inventarioapp.repository.SupplierCartRepository
import com.example.inventarioapp.repository.SupplierPurchaseRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.collections.emptyList

class SupplierPurchaseViewModel(
    private val repository: SupplierPurchaseRepository = SupplierPurchaseRepository(),
    private val catalogsRepository: CatalogsRepository = CatalogsRepository(),
    private val supplierCartRepository: SupplierCartRepository = SupplierCartRepository()
) : ViewModel() {

    /* ---------- state ---------- */

    val products = MutableStateFlow<List<Products>>(emptyList())

    private val _currentPurchase = MutableStateFlow<SupplierPurchase?>(null)
    val currentPurchase: StateFlow<SupplierPurchase?> = _currentPurchase

    private val _selectedSupplier = MutableStateFlow<Supplier?>(null)
    val selectedSupplier: StateFlow<Supplier?> = _selectedSupplier

    private val _total = MutableStateFlow(0.0)
    val total: StateFlow<Double> = _total

    private var started = false

    /* ---------- init ---------- */
    fun start(userId: String){
        if (started) return
        started = true

        observeSupplierCart(userId)
    }

    fun loadCatalogs() = viewModelScope.launch {
        products.value = catalogsRepository.getProducts()
    }

    fun observeSupplierCart(userId: String){
        viewModelScope.launch {
            supplierCartRepository.observeSupplierCart(userId).collect { remoteCart ->
                _currentPurchase.value = remoteCart ?: SupplierPurchase(id = userId, userId = userId)
            }
        }
    }

    /* ---------- lógica items (igual que tu carrito) ---------- */

    fun addOrUpdateItem(product: Products, quantity: Int, cost: Double) {

        val current = _currentPurchase.value ?: return

        val existingItem = current.items.indexOfFirst { it.productId == product.idProduct }
        val currentQty = if (existingItem != -1) current.items[existingItem].quantity else 0
        val newTotalQty = currentQty - quantity

        val updatedItems = if (existingItem != -1) {
            current.items.mapIndexed { i, item ->
                if (i == existingItem){
                    val newQty = item.quantity + quantity
                    item.copy(
                        quantity = newQty,
                        subtotal = newQty * item.cost
                    )
                } else item
            }
        } else {
            current.items + SupplierPurchaseItem(
                id = UUID.randomUUID().toString(),
                productId = product.idProduct,
                productName = product.nameProduct,
                cost = cost,
                quantity = quantity,
                subtotal = cost * quantity
            )
        }

        val updateSupplierCart = current.copy(
            items = updatedItems,
            totalCost = updatedItems.sumOf { it.subtotal },
            updatedAt = Timestamp.now()
        )

        _currentPurchase.value = updateSupplierCart

        viewModelScope.launch { supplierCartRepository.saveSupplierCart(updateSupplierCart) }

        /*val existingIndex = _currentPurchase.value.indexOfFirst { it.productId == product.idProduct }

        val updated = if (existingIndex != -1) {
            _currentPurchase.value.mapIndexed { i, item ->
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
            _currentPurchase.value + SupplierPurchaseItem(
                productId = product.idProduct,
                productName = product.nameProduct,
                quantity = quantity,
                cost = cost,
                subtotal = cost * quantity
            )
        }

        _currentPurchase.value = updated
        _total.value = updated.sumOf { it.subtotal }*/
    }

    fun updateItemQuantity(productId: String, quantity: Int) {
        val current = _currentPurchase.value ?: return

        val updateItems = current.items.map { item ->
            if (item.id == productId){
                item.copy(
                    quantity = quantity,
                    subtotal = quantity * item.cost
                )
            } else
                item
        }

        saveUpdatedSupplierCart(updateItems)
        /*val updated = _currentPurchase.value.map {
            if (it.productId == productId)
                it.copy(quantity = quantity, subtotal = quantity * it.cost)
            else it
        }

        _currentPurchase.value = updated
        _total.value = updated.sumOf { it.subtotal }*/
    }

    fun saveUpdatedSupplierCart(items: List<SupplierPurchaseItem>){
        val current = _currentPurchase.value ?: return

        val updateCart = current.copy(
            items = items,
            totalCost = items.sumOf { it.subtotal },
            updatedAt = Timestamp.now()
        )

        _currentPurchase.value = updateCart

        viewModelScope.launch {
            supplierCartRepository.saveSupplierCart(updateCart)
        }
    }

    fun removeItem(productId: String) {
        val current = _currentPurchase.value ?: return
        val updatedItems = current.items.filterNot { it.productId == productId }
        val updatedSupplierCart = current.copy(
            items = updatedItems,
            totalCost = updatedItems.sumOf { it.cost * it.quantity },
            updatedAt = Timestamp.now()
        )
        _currentPurchase.value = updatedSupplierCart
//        _total.value = updatedSupplierCart.sumOf { it.subtotal }

        viewModelScope.launch {
            supplierCartRepository.saveSupplierCart(updatedSupplierCart)
        }
    }

    /* ---------- proveedor ---------- */

    fun setSupplier(s: Supplier) {
        _selectedSupplier.value = s
    }

    private fun buildMovements (
        supplierPurchase: SupplierPurchase,
        purchaseId: String
    ): List<InventoryMovements> {
        return supplierPurchase.items.map {item ->
            InventoryMovements(
                id = UUID.randomUUID().toString(),
                productId = item.productId,
                quantity = item.quantity,
                type = MovementType.SALE,
                reason = "Venta",
                referenceId = purchaseId,
                userId = supplierPurchase.userId,
                createdAt = Timestamp.now()
            )
        }
    }

    /* ---------- confirmar compra proveedor ---------- */

    suspend fun confirmPurchase(): String? {

        val current = _currentPurchase.value ?: return null
        val purchaseId = UUID.randomUUID().toString()
        var transaction: String? = null

        val purchase = SupplierPurchase(
            id = purchaseId,
            supplierId = current.supplierId,
            items = current.items,
            totalCost = current.totalCost,
            createdAt = Timestamp.now(),
            userId = current.userId
        )

        val resultId = repository.registerSupplierPurchase(purchase)

        return if (resultId != null) {
            supplierCartRepository.clearSupplierCart(current.userId)
            _currentPurchase.value = null
            _selectedSupplier.value = null
            _total.value = 0.0

            resultId
        } else {
            null
        }
    }
}