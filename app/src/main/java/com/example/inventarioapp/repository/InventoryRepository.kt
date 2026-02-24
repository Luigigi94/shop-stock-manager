package com.example.inventarioapp.repository

import android.util.Log
import com.example.inventarioapp.constants.FirestorePaths
import com.example.inventarioapp.constants.MovementType
import com.example.inventarioapp.model.InventoryMovements
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class InventoryRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
){
    fun observeStock(productId: String): Flow<Int> =
        /*val sub =*/ db.collection("InventoryMovements")
        .whereEqualTo("productId", productId)
        .snapshots()
        .map { snapshots ->
            snapshots.documents.sumOf {doc ->
                val qty = doc.getLong("quantity")?.toInt() ?: 0
                val type = doc.getString("type")

                when (type){
                    MovementType.PURCHASE.toString() -> qty
                    MovementType.SALE.toString() -> -qty
                    else -> 0
                }
            }
        }

    /*fun observeStockBulk(productIds: List<String>): Flow<Map<String, Int>> {

        if (productIds.isEmpty()) return flowOf(emptyMap())

        return db.collection("InventoryMovements")
            .whereIn("productId", productIds)
            .snapshots()
            .map { snapshots ->

                snapshots.documents
                    .groupBy { it.getString("productId")!! }
                    .mapValues { (_, docs) ->

                        docs.sumOf { doc ->
                            val qty = doc.getLong("quantity")?.toInt() ?: 0
                            val type = doc.getString("type")

                            when (type) {
                                MovementType.PURCHASE.name -> qty
                                MovementType.SALE.name -> -qty
                                MovementType.INVENTORY.name ->
                                    if (qty >= 0) qty else -qty
                                else -> 0
                            }
                        }
                    }
            }
    }*/

    suspend fun saveInventoryMovements(movements: List<InventoryMovements>){
        val batch = db.batch()
        movements.forEach { movement ->
            val doc = db.collection("InventoryMovements").document(movement.id)

            batch.set(doc, movement)
        }
        batch.commit().await()
    }

    suspend fun applyMovements(movements: List<InventoryMovements>) {

        db.runTransaction { transaction ->

            movements.forEach { movement ->

                // guardar movimiento
                val movementRef = db.collection("InventoryMovements")
                    .document(movement.id)

                transaction.set(movementRef, movement)

                // actualizar stock del producto
                val productRef = db.collection(FirestorePaths.Collections.PRODUCTS)
                    .document(movement.productId)

                val snapshot = transaction.get(productRef)
                val currentStock = snapshot.getLong("stock") ?: 0L

                // en inventario físico el nuevo stock es ABSOLUTO
                val newStock = movement.quantity.toLong()

                transaction.update(productRef, "stock", newStock)
            }
        }.await()
    }
}