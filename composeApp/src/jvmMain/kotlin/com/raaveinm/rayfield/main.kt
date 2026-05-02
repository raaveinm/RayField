package com.raaveinm.rayfield

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.raaveinm.rayfield.ui.theme.RayFieldTheme

fun main() = application {
    val state = rememberWindowState(width = 1239.dp, height = 915.dp)

    Window(
        onCloseRequest = ::exitApplication,
        title = "RayField",
        state = state,
        undecorated = true,
        transparent = false
    ) {

//        val client = SshClientJvm()
        RayFieldTheme {
            Column {
                RayFieldTitleBar(
                    onClose = ::exitApplication,
                    onMinimize = { state.isMinimized = true }
                )
                App(Modifier.fillMaxSize())
            }
        }
    }
}