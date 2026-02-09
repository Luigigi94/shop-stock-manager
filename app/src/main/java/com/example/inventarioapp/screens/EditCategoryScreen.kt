package com.example.inventarioapp.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.inventarioapp.R
import com.example.inventarioapp.model.Categories
import com.example.inventarioapp.ui.components.CustomizedButton
import com.example.inventarioapp.viewmodel.CategoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCategoryScreen(navController: NavController, categoryId: String?, viewModel: CategoryViewModel = viewModel()){
    var nameCategory by remember { mutableStateOf("") }
    var descriptionCategory by remember { mutableStateOf("") }

    val category = viewModel.selectedCategory.collectAsState()

    LaunchedEffect(categoryId) {
        viewModel.loadCategory(categoryId.toString())
    }

    LaunchedEffect(category.value){
        val current = category.value ?: return@LaunchedEffect

        nameCategory = current.nameCategory
        descriptionCategory = current.descriptionCategory
    }

    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.menu_edit_category)) }
            )
        }
    ) { innerPading ->
        Column(modifier = Modifier.padding(innerPading)) {
            Text(text = stringResource(R.string.title_category))
            AddCategory(texto = stringResource(R.string.label_category_name), valueInput = nameCategory, onValueChange = { nameCategory = it })
            AddCategory(texto = stringResource(R.string.label_category_description), valueInput = descriptionCategory, onValueChange = { descriptionCategory = it })
            Spacer(Modifier.size(10.dp))
            Column {
                Row {
                    CustomizedButton(
                        modifier = Modifier.weight(2f),
                        onClick = {
                            navController.popBackStack()
                        }) {
                        Row {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = stringResource(R.string.button_cancel),

                                )
                            Text(text = stringResource(R.string.button_cancel))
                        }
                    }
                    CustomizedButton(
                        modifier = Modifier.weight(2f),
                        onClick = {
                        /*TODO: add functionality*/
                    }) {
                        Text(text = stringResource(R.string.button_delete))
                    }
                }
                CustomizedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if (nameCategory.isNotBlank()){
                            val updateCategory = Categories(
                                nameCategory = nameCategory,
                                descriptionCategory = descriptionCategory,
                                idCategory = categoryId.toString()
                            )

                            viewModel.updateCategory(updateCategory)
                            navController.popBackStack()
                        }
                    }) {
                    Text(text = stringResource(R.string.button_edit_category))
                }
            }
            /*text?.let{
                Text(it)
            }*/
        }
    }
}