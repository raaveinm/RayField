@file:Suppress("JavaIoSerializableObjectMustHaveReadResolve")

package com.raaveinm.rayfield.ui.navigation

//
// Created by Kirill "Raaveinm" on 5/4/26.
//

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.raaveinm.rayfield.data.ssh.ServerUnit
import com.raaveinm.rayfield.ui.animations.StatefulSlideTransition
import com.raaveinm.rayfield.ui.screen.edit.SshScreen

//
// Created by Kirill "Raaveinm" on 5/4/26.
//
//
//val Tab.tabIndex: Int {
//    return when (this) {
//        is SshTab -> 0
//        is InboundTab -> 1
//        is StreamTab -> 2
//        is OutboundTab -> 3
//        is ProTab -> 4
//        else -> 0
//    }
//}

object SshTab : Tab {
    override val options: TabOptions
        @Composable get() = TabOptions(index = 0u, title = "SSH")
    @Composable
    override fun Content() {
        val backNavigator = LocalBackNavigator.current
        Navigator(SshScreen(serverUnit = null)) { navigator ->
            StatefulSlideTransition(navigator = navigator, backNavState = backNavigator)
        }
    }
}

object InboundTab : Tab {
    override val options: TabOptions
        @Composable get() = TabOptions(index = 1u, title = "Inbound")
    @Composable
    override fun Content() {

    }
}
