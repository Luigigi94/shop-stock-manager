package com.example.inventarioapp.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.inventarioapp.R
import com.example.inventarioapp.navigation.AppScreens
import com.example.inventarioapp.ui.LocalSessionViewModel
import com.example.inventarioapp.ui.components.CustomizedTopAppBar
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import com.example.inventarioapp.BuildConfig
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(darkThemeState: MutableState<Boolean>, navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = CredentialManager.create(context)

    val onGoogleSignIn = {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
            .setAutoSelectEnabled(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        scope.launch {
            try {
                val result = credentialManager.getCredential(context, request)
                val googleIdToken = GoogleAuthProvider.getCredential(
                    // Aquí extraemos el token del resultado
                    com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
                        .createFrom(result.credential.data).idToken,
                    null
                )

                Firebase.auth.signInWithCredential(googleIdToken).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        navController.navigate(AppScreens.MenuScreen.route) {
                            popUpTo(AppScreens.LoginScreen.route) { inclusive = true }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("Login", "Error: ${e.message}")
            }
        }
    }

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
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { onGoogleSignIn() },
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Text("Iniciar sesión con Google")
            }
        }
    }
}