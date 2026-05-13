package com.raaveinm.rayfield.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.raaveinm.rayfield.ui.adapters.AdaptivePadding.adaptiveAll
import com.raaveinm.rayfield.ui.animations.AnimatedTabTransition
import com.raaveinm.rayfield.ui.fragments.ConnectionInfoCard
import com.raaveinm.rayfield.ui.fragments.DisplayGrid
import com.raaveinm.rayfield.ui.navigation.InboundTab
import com.raaveinm.rayfield.ui.navigation.OutboundTab
import com.raaveinm.rayfield.ui.navigation.ProTab
import com.raaveinm.rayfield.ui.navigation.SshTab
import com.raaveinm.rayfield.ui.navigation.StreamTab
import com.raaveinm.rayfield.ui.state.GlobalBlurHolder
import com.raaveinm.rayfield.ui.state.MainScreenModel
import com.raaveinm.rayfield.ui.theme.LocalDimensions
import io.github.neilyich.glassmorphism.blurredBackground
import io.github.neilyich.glassmorphism.blurredContent
import io.github.neilyich.glassmorphism.rememberBlurHolder

//
// Created by Kirill "Raaveinm" on 5/3/26.
//

data class EditScreen (
    val configId: String? = null,
    val serverId: String? = null
) : Screen {

    override val key: ScreenKey = "EditScreen:${configId ?: "none"}:${serverId ?: "none"}"

    @Preview
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<MainScreenModel>()
        val serverList by screenModel.serverUnits.collectAsState()

        val globalBlurHolder = GlobalBlurHolder.current ?: rememberBlurHolder()
        val localBlurHolder = rememberBlurHolder()
        val navigator = LocalNavigator.currentOrThrow

        val dimen = LocalDimensions.current

        val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.96f)
        val onInvertSurface = MaterialTheme.colorScheme.surface.copy(alpha = .84f)
        val primary = MaterialTheme.colorScheme.primary
        val onSurface = MaterialTheme.colorScheme.onSurface
        val primaryContainer = MaterialTheme.colorScheme.primaryContainer

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (serverId == null && configId == null) {
                var showPreciser by remember { mutableStateOf(false) }
                var selectedServerId by remember { mutableStateOf<String?>(null) }
                val connectionList by remember(selectedServerId) {
                    screenModel.getServerStatesForServer(serverId = selectedServerId ?: "")
                }.collectAsState(emptyList())

                DisplayGrid(
                    modifier = Modifier.blurredContent(localBlurHolder),
                    serverList = serverList,
                    onClick = {
                        selectedServerId = it.serverId
                        if (connectionList.isEmpty()){
                            navigator.push(EditScreen(serverId = selectedServerId))
                        }
                        showPreciser = !showPreciser
                })
                if (showPreciser) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .blurredBackground(
                                blurHolder = localBlurHolder,
                                blurRadius = 8.dp,
                                tileMode = TileMode.Decal)
                            .background(surfaceVariant.copy(alpha = .5f))
                            .clickable{ showPreciser = false }
                            .padding(adaptiveAll),
                        contentAlignment = Alignment.Center
                    ) {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 360.dp),
                            state = rememberLazyGridState(),
                            horizontalArrangement = Arrangement.spacedBy(dimen.mediumPadding),
                            verticalArrangement = Arrangement.spacedBy(dimen.mediumPadding)
                        ) {
                            items(connectionList) { serverState ->
                                ConnectionInfoCard(
                                    serverState = serverState,
                                    modifier = Modifier.fillMaxWidth(),
                                    followingIcon = {
                                        IconButton(onClick = {
                                            navigator.push(
                                                EditScreen(
                                                    serverId = serverState.serverId,
                                                    configId = serverState.configId
                                                )
                                            )
                                        }) {
                                            Icon(
                                                imageVector = Icons.Outlined.Edit,
                                                contentDescription = "edit_server_button"
                                            )
                                        }
                                    }
                                )
                            }
                            item {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(MaterialTheme.shapes.medium)
                                        .background(primaryContainer)
                                        .clickable{
                                            navigator.push(EditScreen(serverId = selectedServerId)) },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Add,
                                        tint = onSurface,
                                        modifier = Modifier.size(32.dp),
                                        contentDescription = "edit_server_button"
                                    )
                                }
                            }
                        }
                    }
                }
                return@Box
            }

            val scrollState = rememberScrollState()
            val tabs = listOf(SshTab(serverId, configId), InboundTab(configId, serverId), StreamTab, OutboundTab, ProTab)

            TabNavigator(tabs.first()) { tabNavigator ->
                Box(
                    modifier = Modifier
                        .padding(adaptiveAll)
                        .clip(RoundedCornerShape(24.dp))
                        .shadow(8.dp)
                        .blurredBackground(
                            blurHolder = globalBlurHolder,
                            blurRadius = 48.dp,
                            tileMode = TileMode.Mirror
                        )
                        .background(surfaceVariant),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .blurredContent(localBlurHolder),
                        contentAlignment = Alignment.Center
                    ) { AnimatedTabTransition(tabNavigator) }
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = dimen.mediumPadding)
                            .clip(CircleShape)
                            .zIndex(1f)
                            .blurredBackground(
                                blurHolder = localBlurHolder,
                                blurRadius = 8.dp,
                                tileMode = TileMode.Mirror
                            )
                            .background(onInvertSurface)
                            .horizontalScroll(scrollState),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        tabs.forEach { tab ->
                            val isSelected = tabNavigator.current == tab
                            Box(
                                modifier = Modifier
                                    .height(48.dp)
                                    .clickable { tabNavigator.current = tab }
                                    .padding(horizontal = dimen.mediumPadding),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = tab.options.title,
                                    color = if (isSelected) primary else onSurface
                                )

                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .padding(bottom = 2.dp)
                                            .height(3.dp)
                                            .width(28.dp)
                                            .clip(CircleShape)
                                            .background(primary)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
