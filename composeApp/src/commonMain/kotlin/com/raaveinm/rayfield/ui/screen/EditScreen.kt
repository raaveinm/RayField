package com.raaveinm.rayfield.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.raaveinm.rayfield.ui.theme.LocalDimensions

//
// Created by Kirill "Raaveinm" on 5/3/26.
//

class EditScreen (
    val modifier: Modifier = Modifier,
    val serverLink: String? = null
) : Screen {
    @Composable
    override fun Content() {
        val state = rememberLazyGridState()
        val navigator = LocalNavigator.currentOrThrow
        val dimen = LocalDimensions.current
        val padding = PaddingValues(
            top = dimen.sMediumMargin,
            bottom = dimen.mediumMargin,
            start = dimen.extraSmallMargin,
            end = dimen.extraSmallMargin
        )

        if (serverLink == null) {
            Column(modifier = modifier.fillMaxSize()) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 360.dp),
                    state = state,
                    contentPadding = padding,
                    horizontalArrangement = Arrangement.spacedBy(dimen.mediumPadding),
                    verticalArrangement = Arrangement.spacedBy(dimen.mediumPadding),
                    modifier = Modifier.fillMaxSize()
                ) {
//                    items() { server ->
//
//                    }
                }
            }
        }
    }
}
