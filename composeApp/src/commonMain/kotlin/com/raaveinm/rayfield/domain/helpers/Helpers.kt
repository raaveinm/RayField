package com.raaveinm.rayfield.domain.helpers

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp

@Composable
fun calculateWindowSize(): WindowSize {
    val density = LocalDensity.current
    val windowInfo = LocalWindowInfo.current
    val widthInDp = with(density) { windowInfo.containerSize.width.toDp() }

    return when {
        widthInDp < 760.dp -> WindowSize.COMPACT
        widthInDp < 1024.dp -> WindowSize.MEDIUM
        else -> WindowSize.EXPANDED
    }
}
