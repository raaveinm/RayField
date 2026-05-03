package com.raaveinm.rayfield.domain.helpers

import androidx.compose.runtime.staticCompositionLocalOf

enum class WindowSize {
    COMPACT,
    MEDIUM,
    EXPANDED
}

val LocalWindowSize = staticCompositionLocalOf { WindowSize.COMPACT }