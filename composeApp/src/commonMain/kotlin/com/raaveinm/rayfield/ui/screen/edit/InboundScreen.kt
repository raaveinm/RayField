package com.raaveinm.rayfield.ui.screen.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.raaveinm.rayfield.data.xray.Configurations
import com.raaveinm.rayfield.ui.adapters.AdaptivePadding
import com.raaveinm.rayfield.ui.fragments.BlurredDropDown
import com.raaveinm.rayfield.ui.fragments.edit.SettingOutlinedText
import com.raaveinm.rayfield.ui.state.GlobalBlurHolder
import com.raaveinm.rayfield.ui.state.configuration.EditIntent
import com.raaveinm.rayfield.ui.state.configuration.EditScreenModel
import io.github.neilyich.glassmorphism.blurredBackground
import io.github.neilyich.glassmorphism.rememberBlurHolder
import org.koin.core.parameter.parametersOf

//
// Created by Kirill "Raaveinm" on 5/4/26.
//

@Composable
fun Screen.InboundScreen(configId: String? = null) {
    val globalBlurHolder = GlobalBlurHolder.current ?: rememberBlurHolder()
    val lazyState = rememberLazyListState()

    val connectionScreenModel = koinScreenModel<EditScreenModel> { parametersOf(configId) }

    val portState = remember { TextFieldState() }


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
                    Text(text = "Inbound Protocol", color = onSurface)
                    BlurredDropDown(
                        modifier = Modifier.weight(1f),
                        blurHolder = globalBlurHolder,
                        items = Configurations.protocol.entries.map { it.name },
                        selectedItem = connectionScreenModel.state.collectAsState().value.inbound.inboundProtocol.name,
                        onItemSelected = {
                            connectionScreenModel.processIntent(
                                EditIntent.UpdateInboundProtocol(
                                    Configurations.protocol.valueOf(it)
                                )
                            )
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
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth(),
                        isPassword = true
                    )
                }
            }
        }
    }
}
