package com.example.inventarioapp.screens.menu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.inventarioapp.constants.MenuOptions
import com.example.inventarioapp.ui.components.GenericListedOptions
import com.example.inventarioapp.viewmodel.SessionViewModel

@Composable
fun MoreScreen(
    darkThemeState: MutableState<Boolean>,
    onLogout: () -> Unit,
    sessionViewModel: SessionViewModel,
    navController: NavController
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("Ajustes y Más", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(16.dp))

        // 1. Sección de Tema (Aquí rescatas tu lógica anterior)
        Switch(
//            label = "Tema Oscuro",
//            icon = Icons.Default.DarkMode,
            checked = darkThemeState.value,
            onCheckedChange = { darkThemeState.value = it }
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // 2. Las opciones de navegación que definimos en MORE_OPTIONS
        GenericListedOptions(options = MenuOptions.MORE_OPTIONS, navController = navController)

        Spacer(modifier = Modifier.weight(1f))

        // 3. El botón de salida (Al final para que no se pulse por error)
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
        ) {
            Text("Cerrar Sesión", color = MaterialTheme.colorScheme.onErrorContainer)
        }
    }
}