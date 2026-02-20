package com.example.inventarioapp.ui

import androidx.compose.runtime.staticCompositionLocalOf
import com.example.inventarioapp.viewmodel.SessionViewModel

val LocalSessionViewModel =
    staticCompositionLocalOf<SessionViewModel> {
        error("SessionViewModel not provided")
    }