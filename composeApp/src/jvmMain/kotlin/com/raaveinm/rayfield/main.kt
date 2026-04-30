package com.raaveinm.rayfield

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.raaveinm.rayfield.domain.SshClientJvm

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "RayField",
    ) {

        val client = SshClientJvm()

        App(client)
    }
}