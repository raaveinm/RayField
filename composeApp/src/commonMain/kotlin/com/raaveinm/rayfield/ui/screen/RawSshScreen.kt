package com.raaveinm.rayfield.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.raaveinm.rayfield.data.ssh.ConsoleMessageType
import com.raaveinm.rayfield.domain.helpers.LocalWindowSize
import com.raaveinm.rayfield.ui.adapters.AdaptivePadding.adaptiveAll
import com.raaveinm.rayfield.ui.fragments.DisplayGrid
import com.raaveinm.rayfield.ui.state.GlobalBlurHolder
import com.raaveinm.rayfield.ui.state.MainScreenModel
import com.raaveinm.rayfield.ui.state.RawSshScreenModel
import com.raaveinm.rayfield.ui.theme.LocalDimensions
import io.github.neilyich.glassmorphism.blurredBackground
import io.github.neilyich.glassmorphism.rememberBlurHolder

//
// Created by Kirill "Raaveinm" on 5/5/26.
//

data class RawSshScreen(val serverId: String? = null) : Screen {
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<RawSshScreenModel>()
        val mainScreenModel = koinScreenModel<MainScreenModel>()
        val serverList by mainScreenModel.serverUnits.collectAsState()
        val globalBlurHolder = GlobalBlurHolder.current ?: rememberBlurHolder()
        val navigator = LocalNavigator.currentOrThrow
        val dimen = LocalDimensions.current

        val surfaceVariant = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.84f)
        val onInvertSurface = MaterialTheme.colorScheme.surface.copy(alpha = .84f)
        val primary = MaterialTheme.colorScheme.primary
        val onSurface = MaterialTheme.colorScheme.onSurface

        var inputText by remember { mutableStateOf("") }
        val listState = rememberLazyListState()

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (serverId == null) {
                DisplayGrid(serverList = serverList, onClick = { navigator.push(RawSshScreen(serverId = it.serverId)) })
                return@Box
            }

            val server = mainScreenModel.getServerById(serverId)
            LaunchedEffect(serverId) { screenModel.connect(server) }

            Column (
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
                LaunchedEffect(screenModel.history.size) {
                    if (screenModel.history.isNotEmpty()) {
                        listState.animateScrollToItem(screenModel.history.size - 1)
                    }
                }

                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f).fillMaxWidth()
                ) {
                    items(screenModel.history) { message ->
                        Text(
                            text = message.content,
                            color = when (message.type) {
                                ConsoleMessageType.COMMAND -> MaterialTheme.colorScheme.primary
                                ConsoleMessageType.OUTPUT -> MaterialTheme.colorScheme.onSurface
                                ConsoleMessageType.ERROR -> MaterialTheme.colorScheme.error
                                ConsoleMessageType.SYSTEM -> MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.padding(horizontal = dimen.smallPadding),
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                TextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(">>>") },
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (inputText.isNotBlank()) {
                                screenModel.executeCommand(inputText)
                                inputText = ""
                            }
                        }
                    ),
                    trailingIcon = {
                        IconButton(onClick = {
                            if (inputText.isNotBlank()) {
                                screenModel.executeCommand(inputText)
                                inputText = ""
                            }
                        }) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send)
                )
            }
        }
    }
}
