@file:Suppress("JavaIoSerializableObjectMustHaveReadResolve")

package com.raaveinm.rayfield.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.staticCompositionLocalOf
import cafe.adriel.voyager.core.stack.StackEvent
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.transitions.SlideTransition
import com.raaveinm.rayfield.ui.animations.AnimatedNavTransition
import com.raaveinm.rayfield.ui.animations.AnimatedTabTransition
import com.raaveinm.rayfield.ui.animations.StatefulSlideTransition
import com.raaveinm.rayfield.ui.screen.AddServerScreen
import com.raaveinm.rayfield.ui.screen.EditScreen
import com.raaveinm.rayfield.ui.screen.MainScreen
import com.raaveinm.rayfield.ui.screen.RawSshScreen
import com.raaveinm.rayfield.ui.screen.SettingsScreen

//
// Created by Kirill "Raaveinm" on 5/4/26.
//

val LocalBackNavigator = staticCompositionLocalOf<MutableState<Navigator?>> {
    error("No LocalBackNavigator provided")
}

val Tab.tabIndex: Int
    get() = when (this) {
        is HomeTab -> 0
        is EditTab -> 1
        is SettingsTab -> 2
        is RawSshTab -> 3
        is AddServerTab -> 4
        else -> 0
    }

object HomeTab : Tab {
    override val options: TabOptions
        @Composable get() = TabOptions(index = 0u, title = "Home")
    @Composable
    override fun Content() {
        Navigator(MainScreen()) { navigator ->
            AnimatedNavTransition(navigator)
        }
    }
}

data class EditTab(val serverId: String? = null) : Tab {

    override val key: String = "EditTab_${serverId ?: "global_edit"}"

    override val options: TabOptions
        @Composable get() = TabOptions(index = 1u, title = "Edit")

    @Composable
    override fun Content() {
        Navigator(EditScreen(serverId = serverId)) { navigator ->
            val backNavState = LocalBackNavigator.current
            StatefulSlideTransition(navigator = navigator, backNavState = backNavState)
        }
    }
}

object SettingsTab : Tab {
    override val options: TabOptions
        @Composable get() = TabOptions(index = 2u, title = "Settings")

    @Composable
    override fun Content() {
        Navigator(SettingsScreen()) { navigator ->
            val backNavState = LocalBackNavigator.current
            StatefulSlideTransition(navigator = navigator, backNavState = backNavState)
        }
    }
}

object RawSshTab : Tab {
    override val options: TabOptions
        @Composable get() = TabOptions(index = 3u, title = "RawSshS")

    @Composable
    override fun Content() {
        Navigator(RawSshScreen()) { navigator ->
            val backNavState = LocalBackNavigator.current
            StatefulSlideTransition(navigator = navigator, backNavState = backNavState)
        }
    }
}

object AddServerTab : Tab {
    override val options: TabOptions
        @Composable get() = TabOptions(index = 4u, title = "Add Server")

    @Composable
    override fun Content() {
        Navigator(AddServerScreen()) { navigator ->
            val backNavState = LocalBackNavigator.current
            StatefulSlideTransition(navigator = navigator, backNavState = backNavState)
        }
    }
}
