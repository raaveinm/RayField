package com.raaveinm.rayfield

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.raaveinm.rayfield.ui.screen.mobile.MainScreenMobile

@Composable
fun App(modifier: Modifier = Modifier) {
    Scaffold(
        topBar = {},
        modifier = modifier.background(MaterialTheme.colorScheme.background)
    ) { paddingValues ->
        Navigator(
            screen = MainScreenMobile(Modifier
                .padding(paddingValues))
        ) { navigator ->
            SlideTransition(navigator)
        }
    }
}