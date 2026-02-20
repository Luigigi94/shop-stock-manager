package com.example.inventarioapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventarioapp.model.Cart
import com.example.inventarioapp.model.Clients
import com.example.inventarioapp.model.Products
import com.example.inventarioapp.model.Purchase
import com.example.inventarioapp.model.PurchaseItem
import com.example.inventarioapp.repository.PurchaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class PurchaseViewModel(
    private val repository: PurchaseRepository = PurchaseRepository()
) : ViewModel() {

    private val _cart = MutableStateFlow<Cart?>(null)
    val cart: StateFlow<Cart?> = _cart
    /*
    private val _purchase = MutableStateFlow<Purchase?>(null)
    val purchase: StateFlow<Purchase?> = _purchase
    */
    val products = MutableStateFlow<List<Products>>(emptyList())
    val clients = MutableStateFlow<List<Clients>>(emptyList())

    /* ---------- init ---------- */

    init {
        val userId = "Admin"
        observeCart(userId)
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

    /* ---------- lógica ---------- */

    fun addOrUpdateItem(
        product: Products,
        quantity: Int
    ) {
        Log.d("PurchaseViewModel", "Revisando que no vengan obj null\nproducto: $product\nCantidad: $quantity")
//        val current = _cart.value ?: Cart(id = userId?: "Admin", userId = userId?: "Admin")
        val current = _cart.value ?: return

//        val existingItem = current.items.find { it.productId == product.idProduct }
        val existingItem = current.items.indexOfFirst { it.productId == product.idProduct }
        val updatedItems = if (existingItem != -1) {
            // sumar cantidad al existente
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
            // agregar nuevo producto
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

    fun removeItem(itemId: String, userId: String?) {
        val current = _cart.value ?: return
        val updatedItems = current.items.filterNot { it.id == itemId }
        val updatedCart = current.copy(items = updatedItems, total = updatedItems.sumOf { it.price*it.quantity }, updatedAt = System.currentTimeMillis())
        _cart.value = updatedCart

        viewModelScope.launch { repository.saveCart(updatedCart) }
    }

    fun confirmCart (client: Clients?, userId: String?){
        val current = _cart.value?: return

        val purchase = Purchase(
            id = UUID.randomUUID().toString(),
            clientId = client?.idClient,
            clientName = "${client?.nameClient} ${client?.apePClient} ${client?.apeMClient} " ?: "Anonimo",
            items = current.items,
            total = current.total,
            createdAt = System.currentTimeMillis(),
            userId = userId?: "Admin"
        )

        viewModelScope.launch {
            repository.savePurchase(purchase)
            repository.clearCart(purchase.userId)
            _cart.value = null
        }
    }

    /*fun savePurchase(client: Clients?, userId: String?) = viewModelScope.launch {
        val current = _cart.value ?: return@launch

        val purchase = current.copy(
            clientId = client?.idClient,
            clientName = client?.nameClient ?: "Anónimo",
            userId = userId ?: "Admin"
        )

        repository.savePurchase(purchase)
    }

    fun newPurchase() {
        _cart.value = Purchase(id = UUID.randomUUID().toString())
    }

    fun addItemAndSave(product: Products, quantity: Int, client: Clients?, userId: String?) {
        Log.d("PurchaseViewModel","revisanding valores de todos\n $product \n$quantity \n $client")
        if (_purchase.value == null) newPurchase()
        addOrUpdateItem(product, quantity)
        savePurchase(client, userId?: "Admin")
    }*/
}