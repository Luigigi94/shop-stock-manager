package com.example.inventarioapp.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.inventarioapp.R
import com.example.inventarioapp.navigation.AppScreens
import com.example.inventarioapp.ui.LocalSessionViewModel
import com.example.inventarioapp.ui.components.CustomizedTopAppBar
import com.example.inventarioapp.viewmodel.SessionViewModel

@Composable
fun LoginBodyContent(navController: NavController, modifier: Modifier = Modifier, sessionViewModel: SessionViewModel){
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Inventario")
        Button(
            onClick = {
                sessionViewModel.loginAsAdmin()
                navController.navigate(route = AppScreens.MenuScreen.route)
            }
        ) {
            Text("Bienvenido")
        }
    }
}
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(darkThemeState: MutableState<Boolean>, navController: NavController) {
    val sessionViewModel = LocalSessionViewModel.current
    Scaffold(
        topBar = {
            CustomizedTopAppBar(
                title = stringResource(R.string.topbar_login),
                navController = navController,
                darkThemeState = darkThemeState,
                showBack = false,
                showThemeSwitch = true
            )
        }
    ) { innerPadding ->
        LoginBodyContent(navController, Modifier.padding(innerPadding), sessionViewModel)
    }
}