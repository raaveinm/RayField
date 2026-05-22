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

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.ui.text.input.KeyboardType
import com.raaveinm.rayfield.data.xray.XrayConfig
import com.raaveinm.rayfield.ui.fragments.edit.SettingOutlinedText

@Composable
fun ShadowsocksConfiguration(
    globalBlurHolder: BlurHolder,
    editScreenModel: EditScreenModel,
    state: EditDraftState,
    scope: CoroutineScope,
    shadowsocksPasswordState: TextFieldState,
    shadowsocksEmailState: TextFieldState,
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
            Text(text = "Network", color = onSurface)
            BlurredDropDown(
                blurHolder = globalBlurHolder,
                items = Configurations.shadowSocksNetwork.entries.map { it.name },
                selectedItem = state.inbound.shadowsocksNetwork.name,
                onItemSelected = {
                    scope.launch {
                        editScreenModel.processIntent(
                            EditIntent.UpdateShadowsocksNetwork(
                                Configurations.shadowSocksNetwork.valueOf(it)
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
            Text(text = "Global Method", color = onSurface)
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
            Text(text = "Global Password", color = onSurface)
            CryptoTextField(
                state = shadowsocksPasswordState,
                label = "Global Password",
                onGenerateClick = { onGenerateClick() },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Global Email", color = onSurface)
            SettingOutlinedText(
                state = shadowsocksEmailState,
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = onSurface.copy(alpha = 0.2f))

        Text(
            text = "Users",
            style = MaterialTheme.typography.titleMedium,
            color = onSurface,
            modifier = Modifier.align(Alignment.Start)
        )

        state.inbound.shadowsocksUsers.forEachIndexed { index, user ->
            key(index) {
                val userEmailState = rememberTextFieldState(user.email)
                val userPasswordState = rememberTextFieldState(user.password)

                LaunchedEffect(userEmailState.text, index) {
                    val newEmail = userEmailState.text.toString()
                    if (index < state.inbound.shadowsocksUsers.size && state.inbound.shadowsocksUsers[index].email != newEmail) {
                        val updatedUsers = state.inbound.shadowsocksUsers.toMutableList()
                        updatedUsers[index] = updatedUsers[index].copy(email = newEmail)
                        editScreenModel.processIntent(EditIntent.UpdateShadowsocksUsers(updatedUsers))
                    }
                }

                LaunchedEffect(userPasswordState.text, index) {
                    val newPassword = userPasswordState.text.toString()
                    if (index < state.inbound.shadowsocksUsers.size && state.inbound.shadowsocksUsers[index].password != newPassword) {
                        val updatedUsers = state.inbound.shadowsocksUsers.toMutableList()
                        updatedUsers[index] = updatedUsers[index].copy(password = newPassword)
                        editScreenModel.processIntent(EditIntent.UpdateShadowsocksUsers(updatedUsers))
                    }
                }

                LaunchedEffect(user.email) {
                    if (userEmailState.text.toString() != user.email) {
                        userEmailState.setTextAndPlaceCursorAtEnd(user.email)
                    }
                }
                LaunchedEffect(user.password) {
                    if (userPasswordState.text.toString() != user.password) {
                        userPasswordState.setTextAndPlaceCursorAtEnd(user.password)
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "User ${index + 1}",
                            color = onSurface,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = {
                            val updatedUsers = state.inbound.shadowsocksUsers.toMutableList()
                            if (index < updatedUsers.size) {
                                updatedUsers.removeAt(index)
                                editScreenModel.processIntent(EditIntent.UpdateShadowsocksUsers(updatedUsers))
                            }
                        }) {
                            Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Delete user", tint = onSurface)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = "Email", color = onSurface, modifier = Modifier.width(64.dp))
                        SettingOutlinedText(
                            state = userEmailState,
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
                        Text(text = "Password", color = onSurface, modifier = Modifier.width(64.dp))
                        CryptoTextField(
                            state = userPasswordState,
                            label = "Password",
                            onGenerateClick = {
                                editScreenModel.generateUuid()
                                userPasswordState.setTextAndPlaceCursorAtEnd(editScreenModel.uuid.value)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = "Method", color = onSurface, modifier = Modifier.width(64.dp))
                        BlurredDropDown(
                            blurHolder = globalBlurHolder,
                            items = Configurations.shadowsocksMethod.entries.map { it.name },
                            selectedItem = user.method.name,
                            onItemSelected = { selectedName ->
                                val method = Configurations.shadowsocksMethod.valueOf(selectedName)
                                val updatedUsers = state.inbound.shadowsocksUsers.toMutableList()
                                if (index < updatedUsers.size) {
                                    updatedUsers[index] = updatedUsers[index].copy(method = method)
                                    editScreenModel.processIntent(EditIntent.UpdateShadowsocksUsers(updatedUsers))
                                }
                            }
                        )
                    }
                }
            }
        }

        Button(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            onClick = {
                editScreenModel.generateUuid()
                val newUser = XrayConfig.ShadowsocksUser(
                    password = editScreenModel.uuid.value,
                    method = state.inbound.shadowsocksMethod ?: Configurations.shadowsocksMethod.AES_256_GCM,
                    email = "user${state.inbound.shadowsocksUsers.size + 1}@rayfield.com"
                )
                editScreenModel.processIntent(EditIntent.UpdateShadowsocksUsers(state.inbound.shadowsocksUsers + newUser))
            }
        ) {
            Text("Add User")
        }
    }
}