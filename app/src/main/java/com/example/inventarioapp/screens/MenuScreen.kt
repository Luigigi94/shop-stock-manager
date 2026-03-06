package com.example.inventarioapp.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.inventarioapp.R
import com.example.inventarioapp.constants.MenuOptions
import com.example.inventarioapp.navigation.AppScreens
import com.example.inventarioapp.navigation.BottomNavDestinations
import com.example.inventarioapp.screens.menu.MoreScreen
import com.example.inventarioapp.ui.LocalSessionViewModel
import com.example.inventarioapp.ui.components.CustomizedTopAppBar
import com.example.inventarioapp.ui.components.GenericListedOptions

@Composable
fun CustomizedElevatedCard(title: String, value: String, icon: ImageVector, iconColor: Color) {
    ElevatedCard(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),

        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(28.dp)
            )

            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
/*
@Composable
fun ListedOptions(navController: NavController) {
    val options = MenuOptions.options

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = stringResource(R.string.menu_label_quick_actions),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        options.forEach { option ->
            ElevatedCard(
                onClick = { navController.navigate(route = option.route) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                MaterialTheme.colorScheme.primaryContainer,
                                RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = option.icon ?: Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = stringResource(option.label),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
*/


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(darkThemeState: MutableState<Boolean>, navController: NavController) {
    val sessionViewModel = LocalSessionViewModel.current
    val session by sessionViewModel.session.collectAsState()
    var selectedTab by remember { mutableStateOf(BottomNavDestinations.POS.route) }
    Scaffold(
        topBar = {
            CustomizedTopAppBar(
                title = stringResource(R.string.topbar_dashboard),
                /* darkThemeState = darkThemeState, */
                showThemeSwitch = true
            )
        },
        bottomBar = {
            NavigationBar {
                val items = listOf(
                    BottomNavDestinations.POS,
                    BottomNavDestinations.Catalog,
                    BottomNavDestinations.Stock,
                    BottomNavDestinations.More
                )
                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = null) },
                        label = { Text(item.label) },
                        selected = selectedTab == item.route,
                        onClick = { selectedTab = item.route }
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.welcome_user),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "${session?.userName ?: "Usuario"} 👋",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            item {

            }

            item {
                when (selectedTab) {
                    BottomNavDestinations.POS.route -> {
                        Column {
                            // Tus tarjetas de resumen
//                            DashboardCards()
                            // Tus opciones de venta (Refactorizadas)

                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)) {
                                Box(modifier = Modifier.weight(1f)) {
                                    CustomizedElevatedCard(
                                        stringResource(R.string.menu_label_sales_today),
                                        "$1,250",
                                        Icons.Default.PointOfSale,
                                        Color(0xFF4CAF50)
                                    )
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    CustomizedElevatedCard(
                                        stringResource(R.string.menu_label_reserves),
                                        "12",
                                        Icons.Default.Inventory,
                                        Color(0xFF2196F3)
                                    )
                                }
                            }

                            GenericListedOptions(navController, MenuOptions.POS_OPTIONS, "Acciones de Venta")
                        }
                    }
                    BottomNavDestinations.Catalog.route -> {
                        GenericListedOptions(navController, MenuOptions.CATALOG_GROUP, "Administración")
                    }
                    BottomNavDestinations.Stock.route -> {
                        GenericListedOptions(navController, MenuOptions.STOCK_GROUP, "Inventarios")
                    }
                    BottomNavDestinations.More.route -> {
                        MoreScreen(darkThemeState, onLogout = {
                            sessionViewModel.logout()
                            navController.navigate(AppScreens.LoginScreen.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        },sessionViewModel, navController)
                    }
                }
            }

            /*item {
                ListedOptions(navController)
            }
            item {
                Button(
                    onClick = {
                        sessionViewModel.logout()
                        // Al limpiar la sesión, debemos mandar al usuario al Login
                        navController.navigate(AppScreens.LoginScreen.route) {
                            // Limpiamos el historial para que no pueda regresar al menú con el botón atrás
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Text("Cerrar Sesión")
                }
            }*/
        }
    }
}