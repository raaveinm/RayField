package com.raaveinm.rayfield.ui.screen.mobile

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

data class MainScreenMobile(val padding: Modifier) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

    }
}