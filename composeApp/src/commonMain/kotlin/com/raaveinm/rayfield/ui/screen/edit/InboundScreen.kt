package com.raaveinm.rayfield.ui.screen.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.raaveinm.rayfield.data.xray.Configurations
import com.raaveinm.rayfield.ui.adapters.AdaptivePadding
import com.raaveinm.rayfield.ui.fragments.BlurredDropDown
import com.raaveinm.rayfield.ui.fragments.edit.CryptoTextField
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

    val portState = remember { TextFieldState() }
    val listenState = remember { TextFieldState() }
    val shadowsocksPasswordState = remember { TextFieldState() }
    val vmessAlterIdState = remember { TextFieldState() }
    val trojanPasswordState = remember { TextFieldState() }
    val fallbackDestState = remember { TextFieldState() }

    LaunchedEffect(state.isLoading) {
        if (!state.isLoading) {
            if (portState.text.isEmpty()) portState.edit { replace(0, length, state.inbound.inboundPort.toString()) }
            if (listenState.text.isEmpty()) listenState.edit { replace(0, length, state.inbound.inboundListen) }
            if (shadowsocksPasswordState.text.isEmpty()) shadowsocksPasswordState.edit { replace(0, length, state.inbound.shadowsocksPassword ?: "") }
            if (vmessAlterIdState.text.isEmpty()) vmessAlterIdState.edit { replace(0, length, state.inbound.vmessAlterId.toString()) }
            if (trojanPasswordState.text.isEmpty()) trojanPasswordState.edit { replace(0, length, state.inbound.trojanPassword ?: "") }
            if (fallbackDestState.text.isEmpty()) fallbackDestState.edit { replace(0, length, state.inbound.fallbackDest.toString()) }
        }
    }

    LaunchedEffect(portState.text) {
        if (!state.isLoading) editScreenModel.processIntent(EditIntent.UpdateInboundPort(portState.text.toString().toIntOrNull() ?: 0))
    }
    LaunchedEffect(listenState.text) {
        if (!state.isLoading) editScreenModel.processIntent(EditIntent.UpdateInboundListen(listenState.text.toString()))
    }
    LaunchedEffect(shadowsocksPasswordState.text) {
        if (!state.isLoading) editScreenModel.processIntent(EditIntent.UpdateShadowsocksPassword(shadowsocksPasswordState.text.toString()))
    }
    LaunchedEffect(vmessAlterIdState.text) {
        if (!state.isLoading) editScreenModel.processIntent(EditIntent.UpdateVmessAlterId(vmessAlterIdState.text.toString().toIntOrNull() ?: 0))
    }
    LaunchedEffect(trojanPasswordState.text) {
        if (!state.isLoading) editScreenModel.processIntent(EditIntent.UpdateTrojanPassword(trojanPasswordState.text.toString()))
    }
    LaunchedEffect(fallbackDestState.text) {
        if (!state.isLoading) editScreenModel.processIntent(EditIntent.UpdateFallbackDest(fallbackDestState.text.toString().toIntOrNull() ?: 0))
    }

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
                    Text(text = "Inbound Protocol", color = onSurface, modifier = Modifier)
                    BlurredDropDown(
                        blurHolder = globalBlurHolder,
                        items = Configurations.protocol.entries.map { it.name },
                        selectedItem = state.inbound.inboundProtocol.name,
                        onItemSelected = { 
                            scope.launch {
                                editScreenModel.processIntent(
                                    EditIntent.UpdateInboundProtocol(
                                        Configurations.protocol.valueOf(it)
                                    )
                                )
                            }
                        }
                    )
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Inbound Port", color = onSurface)
                    SettingOutlinedText(
                        state = portState,
                        label = { Text("Port") },
                        modifier = Modifier.fillMaxWidth(),
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
                    Text(text = "Inbound Listen", color = onSurface)
                    SettingOutlinedText(
                        state = listenState,
                        label = { Text("Listen Interface") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            when (state.inbound.inboundProtocol) {
                Configurations.protocol.SHADOWSOCKS -> {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Shadowsocks encryption method", color = onSurface, modifier = Modifier) 
                            BlurredDropDown(
                                blurHolder = globalBlurHolder,
                                items = Configurations.shadowsocksMethod.entries.map { it.name },
                                selectedItem = state.inbound.shadowsocksMethod?.name ?: Configurations.shadowsocksMethod.AES_128_GCM.name,
                                onItemSelected = {
                                    scope.launch {
                                        editScreenModel.processIntent(
                                            EditIntent.UpdateShadowsocksMethod(
                                                Configurations.shadowsocksMethod.valueOf(it)
                                            )
                                        )
                                    }
                                }
                            )
                        }
                    }
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Shadowsocks password", color = onSurface, modifier = Modifier)
                            CryptoTextField(
                                state = shadowsocksPasswordState,
                                label = "Shadowsocks password",
                                onGenerateClick = { /*TODO*/ },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
                else -> { /* Other protocols have generic or no settings here for now */ }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Fallback Destination", color = onSurface)
                    SettingOutlinedText(
                        state = fallbackDestState,
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
