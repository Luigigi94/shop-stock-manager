package com.example.inventarioapp.screens

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.inventarioapp.R
import com.example.inventarioapp.model.Categories
import com.example.inventarioapp.navigation.AppScreens
import com.example.inventarioapp.ui.components.CustomizedButton
import com.example.inventarioapp.ui.components.CustomizedOutlinedCard
import com.example.inventarioapp.ui.components.CustomizedOutlinedTextField
import java.util.UUID

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.runtime.collectAsState
import com.example.inventarioapp.viewmodel.CategoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryScreen(navController: NavController, viewModel: CategoryViewModel = viewModel()){
    var nameInput by remember { mutableStateOf("") }
    var descriptionInput by remember { mutableStateOf("") }

//    Lista reactiva desde viewModel (FireBase)
    val listCategories by viewModel.categories.collectAsState()
//    Mensajes para toast
    val message by viewModel.uiMessage.collectAsState()
//    Toast pendiente de hacer generico
    LaunchedEffect(message) {
        message?.let {
            val text = when {
                it == "SUCCEDED_ADD_CATEGORY" ->
                    navController.context.getString(R.string.result_success_added_category)
                it.startsWith("ERROR_ADD_CATEGORY") ->
                    navController.context.getString(R.string.result_failure_added_category)
                else -> it
            }
            Toast.makeText(navController.context, text, Toast.LENGTH_SHORT).show()
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.menu_add_category)) },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.menu_button_back),
                        modifier = Modifier
                            .clickable{ navController.popBackStack() }
                            .padding(horizontal = 12.dp)
                    )
                }
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
                        idCategory = UUID.randomUUID().toString()
                    )
                    viewModel.addCategory(newCategory)

                    nameInput = ""
                    descriptionInput = ""
                }
            }) {
                Text(text = stringResource(R.string.button_add_category))
            }
            ListedCategories(listCategories, navController)

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
        CustomizedOutlinedTextField(valueInput, label = { Text(texto) }, onValueChange = onValueChange)
    }
}

@Composable
fun ListedCategories(listCategories: List<Categories>, navController: NavController) {
    LazyColumn {
        items(listCategories) { category ->
            CustomizedOutlinedCard(onClick = { navController.navigate(route = AppScreens.EditCategoryScreen.route+"/"+category.idCategory)}) {
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