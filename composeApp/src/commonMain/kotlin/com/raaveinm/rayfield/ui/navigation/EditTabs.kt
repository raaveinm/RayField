@file:Suppress("JavaIoSerializableObjectMustHaveReadResolve")

package com.raaveinm.rayfield.ui.navigation

//
// Created by Kirill "Raaveinm" on 5/4/26.
//

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.raaveinm.rayfield.ui.screen.edit.InboundScreen
import com.raaveinm.rayfield.ui.screen.edit.OutboundScreen
import com.raaveinm.rayfield.ui.screen.edit.ProScreen
import com.raaveinm.rayfield.ui.screen.edit.SshScreen

//
// Created by Kirill "Raaveinm" on 5/4/26.
//

data class SshTab(
    val serverId: String? = null,
) : Tab {
    override val options: TabOptions
        @Composable get() = TabOptions(index = 0u, title = "SSH")
    @Composable
    override fun Content() {
        SshScreen(serverId)
    }
}

object InboundTab : Tab {
    override val options: TabOptions
        @Composable get() = TabOptions(index = 1u, title = "Inbound")
    @Composable
    override fun Content() {
        InboundScreen()
    }
}

object OutboundTab : Tab {
    override val options: TabOptions
        @Composable get() = TabOptions(index = 2u, title = "Outbound")

    @Composable
    override fun Content() {
        OutboundScreen()
    }
}

object ProTab : Tab {
    override val options: TabOptions
        @Composable get() = TabOptions(index = 3u, title = "Pro")
    @Composable
    override fun Content() {
        ProScreen()
    }
}