package com.raaveinm.rayfield.ui.fragments.configurations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.raaveinm.rayfield.data.xray.Configurations
import com.raaveinm.rayfield.ui.fragments.BlurredDropDown
import com.raaveinm.rayfield.ui.fragments.edit.CryptoTextField
import com.raaveinm.rayfield.ui.state.configuration.EditDraftState
import com.raaveinm.rayfield.ui.state.configuration.EditIntent
import com.raaveinm.rayfield.ui.state.configuration.EditScreenModel
import io.github.neilyich.glassmorphism.BlurHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

//
// Created by Kirill "Raaveinm" on 5/19/26.
//

@Composable
fun ShadowsocksConfiguration(
    globalBlurHolder: BlurHolder,
    editScreenModel: EditScreenModel,
    state: EditDraftState,
    scope: CoroutineScope,
    shadowsocksPasswordState: TextFieldState,
    onGenerateClick: () -> Unit,
    onSurface: androidx.compose.ui.graphics.Color,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Shadowsocks encryption method", color = onSurface, modifier = Modifier)
            BlurredDropDown(
                blurHolder = globalBlurHolder,
                items = Configurations.shadowsocksMethod.entries.map { it.name },
                selectedItem = state.inbound.shadowsocksMethod?.name
                    ?: Configurations.shadowsocksMethod.AES_128_GCM.name,
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Shadowsocks password", color = onSurface, modifier = Modifier)
            CryptoTextField(
                state = shadowsocksPasswordState,
                label = "Shadowsocks password",
                onGenerateClick = { onGenerateClick() },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}