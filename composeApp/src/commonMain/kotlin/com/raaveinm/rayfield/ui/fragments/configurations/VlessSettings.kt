package com.raaveinm.rayfield.ui.fragments.configurations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.raaveinm.rayfield.data.xray.Configurations
import com.raaveinm.rayfield.data.xray.XrayConfig
import com.raaveinm.rayfield.ui.fragments.BlurredDropDown
import com.raaveinm.rayfield.ui.fragments.edit.SettingOutlinedText
import com.raaveinm.rayfield.ui.state.configuration.EditScreenModel
import io.github.neilyich.glassmorphism.BlurHolder

//
// Created by Kirill "Raaveinm" on 5/19/26.
//

@Composable
fun VlessSettings(
    users: List<XrayConfig.VlessUser>,
    editScreenModel: EditScreenModel,
    onSurface: Color,
    globalBlurHolder: BlurHolder,
) {
    users.forEach { user ->
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = "User email", color = onSurface)
                SettingOutlinedText(
                    state = rememberTextFieldState(user.email),
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardType = KeyboardType.Email,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = "User id", color = onSurface)
                SettingOutlinedText(
                    state = rememberTextFieldState(user.id),
                    label = { Text("UUID") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardType = KeyboardType.Text,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = "Flow", color = onSurface, modifier = Modifier)
                BlurredDropDown(
                    blurHolder = globalBlurHolder,
                    items = Configurations.vlessFlow.entries.map { it.name },
                    selectedItem = user.flow?.name ?: Configurations.vlessFlow.NONE.name,
                ) {
                    // TODO
                }
            }
        }
    }
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
            // TODO
        }
    }
}
