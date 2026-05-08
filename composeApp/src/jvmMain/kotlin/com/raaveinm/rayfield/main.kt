package com.raaveinm.rayfield

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.raaveinm.rayfield.di.initKoin
import com.raaveinm.rayfield.domain.helpers.PlatformIdentity
import com.raaveinm.rayfield.ui.RayFieldTitleBar
import com.raaveinm.rayfield.ui.decoration.Circles
import com.raaveinm.rayfield.ui.decoration.circlesJvm
import com.raaveinm.rayfield.ui.state.GlobalBlurHolder
import com.raaveinm.rayfield.ui.theme.RayFieldTheme
import io.github.neilyich.glassmorphism.blurredContent
import io.github.neilyich.glassmorphism.rememberBlurHolder
import java.awt.Dimension

fun main() {
    initKoin()
    application {
        val state = rememberWindowState(width = 1239.dp, height = 915.dp)

        val platform = PlatformIdentity().currentPlatform

        Window(
            onCloseRequest = ::exitApplication,
            title = "RayField",
            state = state,
            undecorated = true,
            transparent = platform != PlatformIdentity.Platforms.Linux,
        ) {
            window.minimumSize = Dimension(400, 600)
            window.maximumSize = Dimension(3840, 2160)

            val blurHolder = rememberBlurHolder()

            RayFieldTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(
                            shape = if (platform != PlatformIdentity.Platforms.Linux) RoundedCornerShape(12.dp)
                            else RoundedCornerShape(0.dp)
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    RayFieldTitleBar(
                        onClose = ::exitApplication,
                        onMaximize = {
                            state.placement =
                                if (state.placement == WindowPlacement.Maximized) WindowPlacement.Floating
                                else WindowPlacement.Maximized
                        },
                        onMinimize = { state.isMinimized = true },
                        isMaximized = state.placement == WindowPlacement.Maximized,
                        platform = platform
                    )

                    CompositionLocalProvider(GlobalBlurHolder provides blurHolder) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .blurredContent(blurHolder)
                            ) { Circles(circlesJvm()) }
                            App(Modifier.fillMaxSize())
                        }
                    }
                }
            }
        }
    }
}
