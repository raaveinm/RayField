package com.raaveinm.rayfield.ui.screen.desktop

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.core.screen.Screen

data class MainScreen(val padding: Modifier) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
    }
}