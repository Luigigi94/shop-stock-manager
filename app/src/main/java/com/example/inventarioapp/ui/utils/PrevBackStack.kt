package com.example.inventarioapp.ui.utils

import androidx.navigation.NavController

fun PrevBackStack(navController: NavController){
    navController.previousBackStackEntry
        ?.savedStateHandle
        ?.set("openCreateForm", true)

    navController.popBackStack()
}