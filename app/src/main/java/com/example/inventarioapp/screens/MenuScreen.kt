package com.example.inventarioapp.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.inventarioapp.constants.MenuOptions
import com.example.inventarioapp.model.OptionsMenu

@Composable
fun ContentCards(navController: NavController){
    val texto: String = "Hola"
    val numeral: Int = 1
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                CustomizedElevatedCard(texto, numeral)
                CustomizedElevatedCard(texto, (numeral + 1))
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                CustomizedElevatedCard(texto, (numeral + 2))
                CustomizedElevatedCard(texto, (numeral + 3))
            }
        }
    }
}

@Composable
fun CustomizedElevatedCard(texto: String, numeral: Int){
    ElevatedCard(
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = texto)
        Text(text = numeral.toString())
    }
}
@Composable
fun MenuBodyContent(navController: NavController, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
//        Text(text = "Inventario")
//        Button(
//            onClick = {/*TODO: Create button to add an Item*/}
//        ) {
//            Text(text = "Agregar Objeto")
//        }
        ContentCards(navController)
    }
}

@Composable
fun ListedOptions(navController: NavController){
    val listedMenuOptions = MenuOptions.options

    CustomizedOutlinedCard(listedMenuOptions) { optionsMenu ->
        navController.navigate(optionsMenu.route)
    }
}

@Composable
fun CustomizedOutlinedCard(optionsMenu: List<OptionsMenu>, modifier: Modifier = Modifier, onClick: (OptionsMenu) -> Unit){
    LazyColumn() {
        items(optionsMenu) { option ->
            OutlinedCard(
                modifier = modifier.fillMaxWidth(),
                shape = CardDefaults.outlinedShape,
                colors = CardDefaults.outlinedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.elevatedCardElevation(),
                border = CardDefaults.outlinedCardBorder(),
                onClick = { onClick(option) }
            ) {
                Row() {
                    if (option.icon != null) {
                        Icon(
                            imageVector = option.icon,
                            contentDescription = "Icon Add Product",
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "No Icon Available"
                        )
                    }
                    Text(
                        text = option.label
                    )
                }

            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(navController: NavController){
    Scaffold(topBar = {
        TopAppBar(
            title = { Text(text = "Menu") }
        )
    }) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            MenuBodyContent(navController)
            ListedOptions(navController)
        }
    }
}