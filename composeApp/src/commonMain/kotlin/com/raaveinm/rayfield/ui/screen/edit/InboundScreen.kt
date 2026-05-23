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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
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
import com.raaveinm.rayfield.data.xray.Configurations
import com.raaveinm.rayfield.data.xray.XrayConfig
import com.raaveinm.rayfield.ui.adapters.AdaptivePadding
import com.raaveinm.rayfield.ui.adapters.IpAutoFormatTransformation
import com.raaveinm.rayfield.ui.fragments.BlurredDropDown
import com.raaveinm.rayfield.ui.fragments.ErrorCard
import com.raaveinm.rayfield.ui.fragments.configurations.ShadowsocksConfiguration
import com.raaveinm.rayfield.ui.fragments.configurations.VlessSettings
import com.raaveinm.rayfield.ui.fragments.configurations.VlessStreamSettings
import com.raaveinm.rayfield.ui.fragments.edit.SettingOutlinedText
import com.raaveinm.rayfield.ui.screen.LocalSharedEditModel
import com.raaveinm.rayfield.ui.state.GlobalBlurHolder
import com.raaveinm.rayfield.ui.state.configuration.EditIntent
import com.raaveinm.rayfield.ui.theme.LocalDimensions
import io.github.neilyich.glassmorphism.blurredBackground
import io.github.neilyich.glassmorphism.rememberBlurHolder
import kotlinx.coroutines.launch

//
// Created by Kirill "Raaveinm" on 5/4/26.
//

@Composable
fun InboundScreen() {
    val globalBlurHolder = GlobalBlurHolder.current ?: rememberBlurHolder()
    val lazyState = rememberLazyListState()
    val editScreenModel = LocalSharedEditModel.current
    val state by editScreenModel.state.collectAsState()
    val scope = rememberCoroutineScope()

    val onSurface = MaterialTheme.colorScheme.onSurface

    Box(
        Modifier
            .fillMaxSize()
            .blurredBackground(
                blurHolder = globalBlurHolder,
                blurRadius = 24.dp,
                tileMode = TileMode.Clamp
            ),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = lazyState,
            contentPadding = AdaptivePadding.adaptiveAll,
            verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.mediumPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.mediumPadding),
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
                    horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.mediumPadding),
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
                    horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.mediumPadding),
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
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = onSurface.copy(alpha = 0.2f)
                )
            }
            item {
                if (state.inbound.inboundProtocol == Configurations.inboundProtocol.SHADOWSOCKS && 
                    state.stream.security != Configurations.security.NONE) {
                    ErrorCard("Warning: Shadowsocks with ${state.stream.security.name} is " +
                            "non-standard.Standard ss:// links will not work.")
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
                        VlessStreamSettings(
                            editScreenModel = editScreenModel,
                            globalBlurHolder = globalBlurHolder,
                            onSurface = onSurface
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

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = LocalDimensions.current.smallPadding),
                            color = onSurface.copy(alpha = 0.2f)
                        )
                        // Shadowsocks Fallback Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Fallback to Shadowsocks", color = onSurface)
                            Switch(
                                checked = state.inbound.isShadowsocksFallback,
                                onCheckedChange = {
                                    editScreenModel.processIntent(EditIntent.UpdateShadowsocksFallback(it))
                                }
                            )
                        }

                        if (state.inbound.isShadowsocksFallback) {
                            ErrorCard(
                                "Info: Shadowsocks fallback enabled. VLESS traffic will be handled normally, " +
                                        "while non-compliant traffic will be routed to the internal Shadowsocks inbound."
                            )

                            Text(
                                "Shadowsocks Fallback Settings",
                                style = MaterialTheme.typography.titleMedium,
                                color = onSurface,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
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

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = LocalDimensions.current.smallPadding),
                            color = onSurface.copy(alpha = 0.2f)
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
                    horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.mediumPadding),
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
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = onSurface.copy(alpha = 0.2f)
                )
            }
            item {
                Button(
                    modifier = Modifier.fillMaxWidth().padding(top = LocalDimensions.current.mediumPadding),
                    onClick = { editScreenModel.processIntent(EditIntent.Save)}
                ) {
                    Text("Save Server Configuration")
                }
            }
        }
    }
}
