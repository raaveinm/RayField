package com.raaveinm.rayfield.ui.screen.edit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.unit.dp
import com.raaveinm.rayfield.ui.state.GlobalBlurHolder
import io.github.neilyich.glassmorphism.blurredBackground
import io.github.neilyich.glassmorphism.rememberBlurHolder

//
// Created by Kirill "Raaveinm" on 5/4/26.
//

@Composable
fun ProScreen() {
    val globalBlurHolder = GlobalBlurHolder.current ?: rememberBlurHolder()
    Box(Modifier
        .fillMaxSize()
        .blurredBackground(
            blurHolder = globalBlurHolder,
            blurRadius = 48.dp,
            tileMode = TileMode.Mirror
        ),
        contentAlignment = Alignment.Center
    ){
        Text(text = "Pro Screen", color = Color.Cyan)
    }
}