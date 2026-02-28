package com.example.inventarioapp.ui.forms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.inventarioapp.R
import com.example.inventarioapp.model.InventoryCountItem
import com.example.inventarioapp.ui.components.CustomizedButton
import com.example.inventarioapp.ui.components.CustomizedOutlinedTextField

@Composable
fun InventoryForm(
    item: InventoryCountItem,
    onQtyChange: (String) -> Unit,
    onBack: () -> Unit
){
    Column {
        Text("Producto: ${item.productName}", style = MaterialTheme.typography.titleLarge)
        Text("Stock en sistema: ${item.systemQuantity}", style = MaterialTheme.typography.bodyMedium)

        CustomizedOutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = item.productName,
            label = { Text(stringResource(R.string.inventory_label_product_name)) },
            readOnly = true
        )
        CustomizedOutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = item.countedQuantity.toString(),
            onValueChange = onQtyChange,
            label = { Text(stringResource(R.string.inventory_label_product_quantity)) }
        )
        CustomizedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Regresar a la lista")
        }
    }
}