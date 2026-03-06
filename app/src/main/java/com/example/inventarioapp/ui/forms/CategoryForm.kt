package com.example.inventarioapp.ui.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.inventarioapp.R
import com.example.inventarioapp.state.CategoryUiState
import com.example.inventarioapp.ui.components.CustomizedOutlinedTextField

@Composable
fun CategoryForm(
    stateCategory: CategoryUiState,
    onNameCategory: (String) -> Unit,
    onNameBlur: () -> Unit,
    onDescriptionCategory: (String) -> Unit
){
    Column(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (stateCategory.isEdit) stringResource(R.string.generic_label_details) else stringResource(
                R.string.categories_label_new_category
            ),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )

        CustomizedOutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(R.string.categories_label_name_category)) },
            value = stateCategory.nameCategory,
            onValueChange = onNameCategory,
            error = stateCategory.nameError,
            onFocusLost = onNameBlur
        )

        CustomizedOutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(R.string.categories_label_description_category)) },
            value = stateCategory.descriptionCategory ?: "",
            onValueChange = onDescriptionCategory
        )
    }
}