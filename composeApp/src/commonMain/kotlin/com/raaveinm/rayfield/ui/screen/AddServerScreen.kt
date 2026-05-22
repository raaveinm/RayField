package com.raaveinm.rayfield.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.raaveinm.rayfield.ui.screen.edit.SshScreen
import com.raaveinm.rayfield.ui.state.configuration.EditScreenModel

//
// Created by Kirill "Raaveinm" on 5/4/26.
//

class AddServerScreen : Screen {
    @Composable
    override fun Content() {
        val editScreenModel = koinScreenModel<EditScreenModel>()
        CompositionLocalProvider(LocalSharedEditModel provides editScreenModel) {
            SshScreen()
        }
    }
}
