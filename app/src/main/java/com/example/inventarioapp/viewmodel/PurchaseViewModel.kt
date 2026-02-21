package com.example.inventarioapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventarioapp.constants.MovementType
import com.example.inventarioapp.model.Cart
import com.example.inventarioapp.model.Clients
import com.example.inventarioapp.model.InventoryMovements
import com.example.inventarioapp.model.Products
import com.example.inventarioapp.model.Purchase
import com.example.inventarioapp.model.PurchaseItem
import com.example.inventarioapp.repository.PurchaseRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class PurchaseViewModel(
    private val repository: PurchaseRepository = PurchaseRepository()
) : ViewModel() {
    private val _cart = MutableStateFlow<Cart?>(null)
    val cart: StateFlow<Cart?> = _cart

    private val _purchasesByUser = MutableStateFlow<List<Purchase>>(emptyList())
    val purchasesByUser: StateFlow<List<Purchase>> = _purchasesByUser
    private val _purchase = MutableStateFlow<Purchase?>(null)
    val purchase: StateFlow<Purchase?> = _purchase

    val products = MutableStateFlow<List<Products>>(emptyList())
    val clients = MutableStateFlow<List<Clients>>(emptyList())

    private var started = false

    /* ---------- init ---------- */

    fun start(userId: String) {
        if (started) return
        started = true

        observeCart(userId)
        observePurchasesByUser(userId)
    }

    fun loadCatalogs() = viewModelScope.launch {
        products.value = repository.getProducts()
        clients.value = repository.getClients()
    }

    fun observeCart(userId: String) {
        viewModelScope.launch {
            repository.observeCart(userId).collect { remoteCart ->
                _cart.value = remoteCart ?: Cart(id = userId, userId = userId)
            }
        }
    }

    fun observePurchasesByUser(userId: String){
        viewModelScope.launch {
            repository.getPurchasesByUser(userId).collect { fetchedList ->
                _purchasesByUser.value = fetchedList
            }
        }
    }

    fun observePurchase(purchaseId: String){
        viewModelScope.launch {
            repository.observePurchase(purchaseId).collect {
                _purchase.value = it
            }
        }
    }

    /* ---------- lógica ---------- */

    fun addOrUpdateItem(
        product: Products,
        quantity: Int
    ) {
        Log.d("PurchaseViewModel", "Revisando que no vengan obj null\nproducto: $product\nCantidad: $quantity")
        val current = _cart.value ?: return

        val existingItem = current.items.indexOfFirst { it.productId == product.idProduct }
        val updatedItems = if (existingItem != -1) {
            current.items.mapIndexed { i, item ->
                if (i == existingItem) {
                    val newQuantity = item.quantity + quantity
                    item.copy(
                        quantity = newQuantity,
                        subtotal = newQuantity * item.price
                    )
                } else item
            }
        } else {
            current.items + PurchaseItem(
                id = UUID.randomUUID().toString(),
                productId = product.idProduct,
                productName = product.nameProduct,
                price = product.priceProduct,
                quantity = quantity,
                subtotal = product.priceProduct * quantity
            )
        }

        val updatedCart = current.copy(items = updatedItems, total = updatedItems.sumOf { it.subtotal }, updatedAt = System.currentTimeMillis())
        _cart.value = updatedCart

        Log.d("PurchaseViewModel", "antes de llamar al repository.saveCart")
        viewModelScope.launch { repository.saveCart(updatedCart) }
    }

    fun updateItemQuantity(itemId: String, quantity: Int){
        val current = _cart.value ?: return

        val updatedItems = current.items.map { item ->
            if (item.id == itemId){
                item.copy(
                    quantity = quantity,
                    subtotal = quantity * item.price
                )
            } else {
                item
            }
        }

        saveUpdatedCart(updatedItems)
    }

    private fun saveUpdatedCart(items: List<PurchaseItem>){
        val current = _cart.value ?: return

        val updatedCart = current.copy(
            items = items,
            total = items.sumOf { it.subtotal },
            updatedAt = System.currentTimeMillis()
        )

        _cart.value = updatedCart

        viewModelScope.launch {
            repository.saveCart(updatedCart)
        }
    }

    fun removeItem(itemId: String, userId: String?) {
        val current = _cart.value ?: return
        val updatedItems = current.items.filterNot { it.id == itemId }
        val updatedCart = current.copy(items = updatedItems, total = updatedItems.sumOf { it.price*it.quantity }, updatedAt = System.currentTimeMillis())
        _cart.value = updatedCart

        viewModelScope.launch { repository.saveCart(updatedCart) }
    }

    fun confirmCart (): String?{
        val current = _cart.value?: return null

        val purchaseId = UUID.randomUUID().toString()
        val purchase = Purchase(
            id = purchaseId,
            clientId = current.clientId,
            clientName = "${current.clientName}" ?: "Anonimo",
            items = current.items,
            total = current.total,
            createdAt = System.currentTimeMillis(),
            userId = current.userId?: "Admin"
        )

        viewModelScope.launch {
            repository.savePurchase(purchase)

            val movements = current.items.map {item ->
                InventoryMovements(
                    id = UUID.randomUUID().toString(),
                    productId = item.productId,
                    quantity = item.quantity,
                    type = MovementType.SALE,
                    reason = "Venta",
                    referenceId = purchaseId,
                    userId = purchase.userId,
                    createdAt = Timestamp.now()
                )
            }

            repository.saveInventoryMovements(movements)
            repository.updateStock(movements)
            repository.clearCart(purchase.userId)
            _cart.value = null
        }

        return purchaseId
    }

    fun setClient(client: Clients?){
        val current = _cart.value ?: return

        val updated = current.copy(
            clientId = client?.idClient,
            clientName = "${client?.nameClient} ${client?.apePClient} ${client?.apeMClient ?: ""}"
        )

        _cart.value = updated

        viewModelScope.launch {
            repository.saveCart(updated)
        }
    }
}