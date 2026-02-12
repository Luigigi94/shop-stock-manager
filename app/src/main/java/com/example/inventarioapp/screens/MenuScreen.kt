package com.example.inventarioapp.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.inventarioapp.constants.MenuOptions
import com.example.inventarioapp.ui.components.CustomizedOutlinedCard
import com.example.inventarioapp.ui.components.CustomizedTopAppBar

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
        ContentCards(navController)
    }
}

@Composable
fun ListedOptions(navController: NavController){
    val listedMenuOptions = MenuOptions.options
    LazyColumn() {
        items(listedMenuOptions) { option ->
            CustomizedOutlinedCard(onClick = { navController.navigate(option.route) }) {
                Row() {
                    if (option.icon != null) {
                        Icon(
                            imageVector = option.icon,
                            contentDescription = option.contentDescription,
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "No Icon Available"
                        )
                    }
                    Text(
                        text = stringResource( option.label)
                    )
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(darkThemeState: MutableState<Boolean>, navController: NavController){
    Scaffold(topBar = {
        CustomizedTopAppBar(
            title = "MenuScreen",
            darkThemeState = darkThemeState,
            showThemeSwitch = true
        )
    }) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            MenuBodyContent(navController)
            ListedOptions(navController)
        }
    }
}