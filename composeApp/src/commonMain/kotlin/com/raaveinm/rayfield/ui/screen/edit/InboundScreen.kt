package com.raaveinm.rayfield.ui.screen.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.unit.dp
import com.raaveinm.rayfield.data.ssh.ServerUnit
import com.raaveinm.rayfield.domain.helpers.LocalWindowSize
import com.raaveinm.rayfield.domain.helpers.WindowSize
import com.raaveinm.rayfield.ui.state.GlobalBlurHolder
import com.raaveinm.rayfield.ui.theme.LocalDimensions
import io.github.neilyich.glassmorphism.blurredBackground
import io.github.neilyich.glassmorphism.rememberBlurHolder

//
// Created by Kirill "Raaveinm" on 5/4/26.
//

@Composable
fun InboundScreen(serverUnit: ServerUnit? = null) {
    val dimen = LocalDimensions.current
    val windowSize = LocalWindowSize.current

    val globalBlurHolder = GlobalBlurHolder.current ?: rememberBlurHolder()
    val lazyState = rememberLazyListState()

    val onSurface = MaterialTheme.colorScheme.onSurface

    val padding = PaddingValues(
        vertical = dimen.sMediumMargin,
        horizontal = when(windowSize){
            WindowSize.EXPANDED -> dimen.sMediumMargin
            WindowSize.MEDIUM -> dimen.extraSmallMargin
            WindowSize.COMPACT -> dimen.smallSize
        }
    )

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
            contentPadding = padding,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

        }
    }
}
