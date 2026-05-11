package com.raaveinm.rayfield.ui.screen.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.koinScreenModel
import com.raaveinm.rayfield.ui.adapters.AdaptivePadding
import com.raaveinm.rayfield.ui.state.GlobalBlurHolder
import com.raaveinm.rayfield.ui.state.configuration.EditScreenModel
import com.raaveinm.rayfield.ui.theme.LocalDimensions
import io.github.neilyich.glassmorphism.blurredBackground
import io.github.neilyich.glassmorphism.rememberBlurHolder
import org.koin.core.parameter.parametersOf

//
// Created by Kirill "Raaveinm" on 5/4/26.
//

@Composable
fun ProScreen() {
    val dimen = LocalDimensions.current
    val globalBlurHolder = GlobalBlurHolder.current ?: rememberBlurHolder()
    val lazyState = rememberLazyListState()

    val onSurface = MaterialTheme.colorScheme.onSurface

    Box(
        Modifier
            .fillMaxSize()
            .blurredBackground(
                blurHolder = globalBlurHolder,
                blurRadius = 96.dp,
                tileMode = TileMode.Clamp
            ),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = lazyState,
            contentPadding = AdaptivePadding.adaptiveAll,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

        }
    }
}