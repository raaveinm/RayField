package com.raaveinm.rayfield.domain.helpers

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun calculateWindowSize(): WindowSize {
    var windowSize = WindowSize.COMPACT
    BoxWithConstraints {
        windowSize = when {
            maxWidth < 600.dp -> WindowSize.COMPACT
            maxWidth < 840.dp -> WindowSize.MEDIUM
            else -> WindowSize.EXPANDED
        }
    }
    return windowSize
}