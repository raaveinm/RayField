package com.raaveinm.rayfield.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.raaveinm.rayfield.ui.animations.AnimatedTabTransition
import com.raaveinm.rayfield.ui.fragments.DisplayGrid
//import com.raaveinm.rayfield.ui.navigation.SshTab
import com.raaveinm.rayfield.ui.theme.LocalDimensions
import io.github.neilyich.glassmorphism.blurredContent
import io.github.neilyich.glassmorphism.rememberBlurHolder

//
// Created by Kirill "Raaveinm" on 5/3/26.
//

data class EditScreen (
    val serverId: String? = null
) : Screen {

    override val key: ScreenKey = "EditScreen:${serverId ?: "root"}"

    @Preview
    @Composable
    override fun Content() {
        val localBlurHolder = rememberBlurHolder()
        val navigator = LocalNavigator.currentOrThrow
        val dimen = LocalDimensions.current

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (serverId == null) {
                DisplayGrid(navigator)
                return@Box
            }

            Text(text = "Edit Screen: $serverId", color = Color.Cyan)
//            TabNavigator(SshTab) { navigator ->
//                Box(
//                    modifier = Modifier.blurredContent(localBlurHolder)
//                ){ AnimatedTabTransition(navigator) }
//            }
        }
    }
}
