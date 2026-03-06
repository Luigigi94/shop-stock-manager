package com.example.inventarioapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.inventarioapp.R
import com.example.inventarioapp.navigation.AppScreens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizedTopAppBar(
    title: String,
    navController: NavController? = null,
    /*darkThemeState: MutableState<Boolean>? = null,*/
    showBack: Boolean = true,
    showThemeSwitch: Boolean = true
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.5.sp
            )
        },
        navigationIcon = {
            if (showBack && navController != null){
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier
                        .clickable {
//                            navController.popBackStack()
                            navController.navigate(route = AppScreens.MenuScreen.route)
                        }
                        .padding(horizontal = 12.dp)
                )
            }
        },
        /*actions = {
            if (showThemeSwitch && darkThemeState != null) {
                Switch(
                    checked = darkThemeState.value,
                    onCheckedChange = { darkThemeState.value = it }
                )
            }
        }*/
    )
}