package com.example.inventarioapp.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.inventarioapp.R
import com.example.inventarioapp.model.Categories
import com.example.inventarioapp.ui.components.CustomizedButton
import com.example.inventarioapp.ui.components.CustomizedOutlinedCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryScreen(navController: NavController){
    var nameInput by remember { mutableStateOf("") }
    var descriptionInput by remember { mutableStateOf("") }
    var listCategories by remember { mutableStateOf(emptyList<Categories>()) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.menu_add_category)) }
            )
        }
    ) { innerPading ->
        Column(modifier = Modifier.padding(innerPading)) {
            Text(text = stringResource(R.string.title_category))
            AddCategory(texto = stringResource(R.string.label_category_name), valueInput = nameInput, onValueChange = { nameInput = it })
            AddCategory(texto = stringResource(R.string.label_category_description), valueInput = descriptionInput, onValueChange = { descriptionInput = it })
            CustomizedButton(onClick = {
                if (nameInput.isNotBlank()){
                    val newCategory = Categories(
                        nameCategory = nameInput,
                        descriptionCategory = descriptionInput,
                        idCategory = null
                    )

                    listCategories = listCategories + newCategory

                    nameInput = ""
                    descriptionInput = ""
                }
            }) {
                Text(text = stringResource(R.string.button_add_category))
            }
            ListedCategories(listCategories)

        }
    }
}

@Composable
fun AddCategory(valueInput: String, texto: String, onValueChange: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        CustomizedOutlinedTextField(valueInput, texto, onValueChange = onValueChange)
    }
}

@Composable
fun CustomizedOutlinedTextField(valueInput: String, texto: String, modifier: Modifier = Modifier, onValueChange: (String) -> Unit){
    OutlinedTextField(
        value = valueInput,
        modifier = modifier,
        label = { Text(texto) },
        onValueChange = onValueChange
    )
}

@Composable
fun ListedCategories(listCategories: List<Categories>) {
    LazyColumn() {
        items(listCategories) { category ->
            CustomizedOutlinedCard(onClick = {/*TODO: New Screen to Edit Category*/}) {
                Row(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(text = category.nameCategory)
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editing Category"
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun previewAll(/*navController: NavController*/){
    var valueInput by remember { mutableStateOf("") }
    var listCategories by remember { mutableStateOf(emptyList<Categories>()) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.menu_add_category)) }
            )
        }
    ) { innerPading ->
        Column(modifier = Modifier.padding(innerPading)) {
            Text(text = stringResource(R.string.title_category))
            AddCategory(texto = stringResource(R.string.label_category_name), valueInput = valueInput, onValueChange = { valueInput = it })
            AddCategory(texto = stringResource(R.string.label_category_description), valueInput = valueInput, onValueChange = { valueInput = it })
            ListedCategories(listCategories)
            CustomizedButton(onClick = {}) {
                Text(text = stringResource(R.string.button_add_category))
            }
        }
    }
}