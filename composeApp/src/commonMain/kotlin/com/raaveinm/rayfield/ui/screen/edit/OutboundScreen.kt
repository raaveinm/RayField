package com.raaveinm.rayfield.ui.screen.edit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.raaveinm.rayfield.data.xray.Configurations
import com.raaveinm.rayfield.ui.adapters.AdaptivePadding
import com.raaveinm.rayfield.ui.adapters.IpAutoFormatTransformation
import com.raaveinm.rayfield.ui.fragments.BlurredDropDown
import com.raaveinm.rayfield.ui.fragments.edit.SettingOutlinedText
import com.raaveinm.rayfield.ui.state.GlobalBlurHolder
import com.raaveinm.rayfield.ui.state.configuration.EditIntent
import com.raaveinm.rayfield.ui.state.configuration.EditScreenModel
import io.github.neilyich.glassmorphism.blurredBackground
import io.github.neilyich.glassmorphism.rememberBlurHolder

@Composable
fun Screen.OutboundScreen() {
    val globalBlurHolder = GlobalBlurHolder.current ?: rememberBlurHolder()
    val editScreenModel = koinScreenModel<EditScreenModel>()
    val state by editScreenModel.state.collectAsState()
    val outbound = state.outbound
    val onSurface = MaterialTheme.colorScheme.onSurface

    Box(
        Modifier
            .fillMaxSize()
            .blurredBackground(blurHolder = globalBlurHolder, blurRadius = 96.dp, tileMode = TileMode.Clamp),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = AdaptivePadding.adaptiveAll,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ///////////////////////////////////////////////
            // Protocol Selector
            ///////////////////////////////////////////////
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Protocol", color = onSurface)
                    BlurredDropDown(
                        blurHolder = globalBlurHolder,
                        items = Configurations.protocol.entries.map { it.name },
                        selectedItem = outbound.protocol.name,
                        onItemSelected = { selected ->
                            editScreenModel.processIntent(
                                EditIntent.UpdateOutboundProtocol(Configurations.protocol.valueOf(selected))
                            )
                        }
                    )
                }
            }
            ///////////////////////////////////////////////
            // Tag Input
            ///////////////////////////////////////////////
            item {
                SettingOutlinedText(
                    state = editScreenModel.outboundTagState,
                    label = { Text("Outbound Tag (e.g. proxy)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            ///////////////////////////////////////////////
            // Dynamic Protocol Configuration
            ///////////////////////////////////////////////
            if (outbound.protocol == Configurations.protocol.VLESS || outbound.protocol == Configurations.protocol.SHADOWSOCKS) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        SettingOutlinedText(
                            state = editScreenModel.outboundAddressState,
                            label = { Text("Server Address") },
                            modifier = Modifier.weight(0.7f),
                            isDone = false,
                            keyboardType = KeyboardType.Number,
                            inputTransformation = IpAutoFormatTransformation
                        )
                        SettingOutlinedText(
                            state = editScreenModel.outboundPortState,
                            label = { Text("Port") },
                            modifier = Modifier.weight(0.3f),
                            keyboardType = KeyboardType.Number
                        )
                    }
                }

                item {
                    if (outbound.protocol == Configurations.protocol.VLESS) {
                        SettingOutlinedText(
                            state = editScreenModel.outboundIdState,
                            label = { Text("VLESS User UUID") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Cipher", color = onSurface)
                                BlurredDropDown(
                                    blurHolder = globalBlurHolder,
                                    items = Configurations.shadowsocksMethod.entries.map { it.name },
                                    selectedItem = state.outbound.shadowsocksMethod?.name ?: Configurations.shadowsocksMethod.AES_256_GCM.name,
                                    onItemSelected = { selected ->
                                        editScreenModel.processIntent(
                                            EditIntent.UpdateOutboundShadowsocksMethod(Configurations.shadowsocksMethod.valueOf(selected))
                                        )
                                    }
                                )
                            }
                            SettingOutlinedText(
                                state = editScreenModel.outboundPasswordState,
                                label = { Text("Shadowsocks Password") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}
