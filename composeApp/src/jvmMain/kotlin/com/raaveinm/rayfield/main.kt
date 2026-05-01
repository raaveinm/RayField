package com.raaveinm.rayfield

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.raaveinm.rayfield.domain.SshClientJvm

fun main() = application {
    val state = rememberWindowState(width = 500.dp, height = 800.dp)

    Window(
        onCloseRequest = ::exitApplication,
        title = "RayField",
        state = state
    ) {

        val client = SshClientJvm()

        App(client)
    }
}