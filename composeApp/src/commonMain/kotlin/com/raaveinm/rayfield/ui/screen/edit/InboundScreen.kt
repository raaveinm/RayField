package com.raaveinm.rayfield.ui.screen.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.raaveinm.rayfield.data.xray.Configurations
import com.raaveinm.rayfield.data.xray.XrayConfig
import com.raaveinm.rayfield.ui.adapters.AdaptivePadding
import com.raaveinm.rayfield.ui.adapters.IpAutoFormatTransformation
import com.raaveinm.rayfield.ui.fragments.BlurredDropDown
import com.raaveinm.rayfield.ui.fragments.configurations.ShadowsocksConfiguration
import com.raaveinm.rayfield.ui.fragments.configurations.VlessSettings
import com.raaveinm.rayfield.ui.fragments.configurations.VlessStreamSettings
import com.raaveinm.rayfield.ui.fragments.edit.SettingOutlinedText
import com.raaveinm.rayfield.ui.state.GlobalBlurHolder
import com.raaveinm.rayfield.ui.state.configuration.EditIntent
import com.raaveinm.rayfield.ui.state.configuration.EditScreenModel
import io.github.neilyich.glassmorphism.blurredBackground
import io.github.neilyich.glassmorphism.rememberBlurHolder
import kotlinx.coroutines.launch
import org.koin.core.parameter.parametersOf

//
// Created by Kirill "Raaveinm" on 5/4/26.
//

@Composable
fun Screen.InboundScreen(configId: String? = null, serverId: String? = null) {
    val globalBlurHolder = GlobalBlurHolder.current ?: rememberBlurHolder()
    val lazyState = rememberLazyListState()
    val editScreenModel = koinScreenModel<EditScreenModel> { parametersOf(configId, serverId) }
    val state by editScreenModel.state.collectAsState()
    val scope = rememberCoroutineScope()

    val onSurface = MaterialTheme.colorScheme.onSurface

    Box(
        Modifier
            .fillMaxSize()
            .blurredBackground(
                blurHolder = globalBlurHolder,
                blurRadius = 96.dp,
                tileMode = TileMode.Clamp
            ),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = lazyState,
            contentPadding = AdaptivePadding.adaptiveAll,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Listen Address", color = onSurface)
                    SettingOutlinedText(
                        state = editScreenModel.listenState,
                        label = { Text("IP") },
                        modifier = Modifier.weight(0.7f),
                        isDone = false,
                        isError = editScreenModel.listenState.text.toString().isEmpty(),
                        keyboardType = KeyboardType.Number,
                        inputTransformation = IpAutoFormatTransformation
                    )
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Port", color = onSurface)
                    SettingOutlinedText(
                        state = editScreenModel.portState,
                        label = { Text("Port") },
                        modifier = Modifier.weight(0.7f),
                        isError = editScreenModel.listenState.text.toString().isEmpty(),
                        isDone = false,
                        keyboardType = KeyboardType.Number
                    )
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Inbound Protocol", color = onSurface, modifier = Modifier)
                    BlurredDropDown(
                        blurHolder = globalBlurHolder,
                        items = Configurations.inboundProtocol.entries.map { it.name },
                        selectedItem = state.inbound.inboundProtocol.name,
                        onItemSelected = { 
                            scope.launch {
                                editScreenModel.processIntent(
                                    EditIntent.UpdateInboundProtocol(
                                        Configurations.inboundProtocol.valueOf(it)
                                    )
                                )
                            }
                        }
                    )
                }
            }
            item {
                when (state.inbound.inboundProtocol) {
                    Configurations.inboundProtocol.SHADOWSOCKS -> {
                        ShadowsocksConfiguration(
                            globalBlurHolder = globalBlurHolder,
                            editScreenModel = editScreenModel,
                            state = state,
                            scope = scope,
                            shadowsocksPasswordState = editScreenModel.shadowsocksPasswordState,
                            shadowsocksEmailState = editScreenModel.shadowsocksEmailState,
                            onSurface = onSurface,
                            onGenerateClick = {
                                editScreenModel.generateUuid()
                                editScreenModel.shadowsocksPasswordState.setTextAndPlaceCursorAtEnd(editScreenModel.uuid.value)
                            }
                        )
                    }
                    Configurations.inboundProtocol.VLESS -> {
                        val vlessSettings = state.inbound.settings as? XrayConfig.VlessInboundSettings
                        VlessSettings(
                            users = vlessSettings?.users ?: emptyList(),
                            editScreenModel = editScreenModel,
                            onSurface = onSurface,
                            globalBlurHolder = globalBlurHolder,
                        )
                        VlessStreamSettings(
                            editScreenModel = editScreenModel,
                            globalBlurHolder = globalBlurHolder,
                            onSurface = onSurface
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Fallback Destination", color = onSurface)
                    SettingOutlinedText(
                        state = editScreenModel.fallbackDestState,
                        label = { Text("Port") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardType = KeyboardType.Number
                    )
                }
            }
            item {
                Button(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    onClick = { editScreenModel.processIntent(EditIntent.Save)}
                ) {
                    Text("Save Server Configuration")
                }
            }
        }
    }
}
