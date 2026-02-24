package com.example.inventarioapp.ui.forms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.inventarioapp.R
import com.example.inventarioapp.model.Categories
import com.example.inventarioapp.state.ProductUiState
import com.example.inventarioapp.ui.components.CustomizedButton
import com.example.inventarioapp.ui.components.CustomizedExposedDropdownMenu
import com.example.inventarioapp.ui.components.CustomizedOutlinedTextField


@Composable
fun ProductForm(
    state: ProductUiState,
//    currentStock: Int,
    categories: List<Categories>,
    selectedCategory: Categories?,
    onNameChange: (String) -> Unit,
    onNameBlur: () -> Unit,
    onDescriptionChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onPriceBlur: () -> Unit,
    onQuantityChange: (String) -> Unit,
    onQuantityBlur: () -> Unit,
    onCategorySelected: (String) -> Unit,
    onAddCategoryClick: () -> Unit
) {

    Column {

        CustomizedOutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.nameProduct,
            onValueChange = onNameChange,
            label = { Text(stringResource(R.string.label_name_product)) },
            error = state.nameError,
            onFocusLost = onNameBlur
        )

        Spacer(Modifier.height(10.dp))

        CustomizedOutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.descriptionProduct,
            onValueChange = onDescriptionChange,
            label = { Text(stringResource(R.string.label_description_product)) })


        Spacer(Modifier.height(10.dp))

        Row {


            CustomizedOutlinedTextField(
                modifier = Modifier.weight(1f),
                value = state.priceProduct.toString(),
                onValueChange = onPriceChange,
                label = { Text(stringResource(R.string.label_price_product)) },
                error = state.priceError,
                onFocusLost = onPriceBlur
            )


            if (state.isEdit) {
                CustomizedOutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = state.quantityProduct.toString(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.label_quantity_product)) })
            } else {
                CustomizedOutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = state.quantityProduct.toString(),
                    onValueChange = onQuantityChange,
                    label = { Text(stringResource(R.string.label_quantity_product)) },
                    error = state.quantityError,
                    onFocusLost = onQuantityBlur
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        Row {

            CustomizedExposedDropdownMenu(
                items = categories,
                selectedItem = selectedCategory,
                label = stringResource(R.string.on_action_category),
                itemLabel = { it.nameCategory },
                onItemSelected = { onCategorySelected(it.idCategory) },
                modifier = Modifier.weight(1f),
                isError = state.idCategoryError != null,
                supportingText = state.idCategoryError
            )

            CustomizedButton(
                onClick = onAddCategoryClick, modifier = Modifier.weight(0.3f)
            ) {
                Icon(Icons.Filled.Folder, null)
            }
        }

    }
}