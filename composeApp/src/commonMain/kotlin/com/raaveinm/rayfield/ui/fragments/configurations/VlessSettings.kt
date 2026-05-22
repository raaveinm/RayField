package com.raaveinm.rayfield.ui.fragments.configurations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.raaveinm.rayfield.data.xray.Configurations
import com.raaveinm.rayfield.data.xray.XrayConfig
import com.raaveinm.rayfield.ui.fragments.BlurredDropDown
import com.raaveinm.rayfield.ui.fragments.edit.SettingOutlinedText
import com.raaveinm.rayfield.ui.state.configuration.EditIntent
import com.raaveinm.rayfield.ui.state.configuration.EditScreenModel
import com.raaveinm.rayfield.ui.theme.LocalDimensions
import io.github.neilyich.glassmorphism.BlurHolder

//
// Created by Kirill "Raaveinm" on 5/19/26.
//

@Composable
fun VlessSettings(
    users: List<XrayConfig.VlessUser>,
    editScreenModel: EditScreenModel,
    onSurface: Color,
    globalBlurHolder: BlurHolder
) {
    val state by editScreenModel.state.collectAsState()

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.mediumPadding),
    ) {
        users.forEachIndexed { index, user ->
            key(user.id) {
                val userEmailState = rememberTextFieldState(user.email)

                // Sync from TextFieldState to Model
                LaunchedEffect(userEmailState.text, index) {
                    val currentSettings =
                        state.inbound.settings as? XrayConfig.VlessInboundSettings ?: return@LaunchedEffect
                    val newEmail = userEmailState.text.toString()
                    if (index < currentSettings.users.size && currentSettings.users[index].email != newEmail) {
                        val updatedUsers = currentSettings.users.toMutableList()
                        updatedUsers[index] = updatedUsers[index].copy(email = newEmail)
                        editScreenModel.processIntent(
                            EditIntent.UpdateInbound(
                                state.inbound.copy(settings = currentSettings.copy(users = updatedUsers))
                            )
                        )
                    }
                }

                // Sync from Model to TextFieldState (in case of external changes, e.g. generation)
                LaunchedEffect(user.email) {
                    if (userEmailState.text.toString() != user.email) {
                        userEmailState.setTextAndPlaceCursorAtEnd(user.email)
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.smallPadding)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.mediumPadding),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "User ${index + 1}",
                            color = onSurface,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = {
                            val currentSettings =
                                state.inbound.settings as? XrayConfig.VlessInboundSettings ?: return@IconButton
                            val updatedUsers = currentSettings.users.toMutableList()
                            if (index < updatedUsers.size) {
                                updatedUsers.removeAt(index)
                                editScreenModel.processIntent(
                                    EditIntent.UpdateInbound(
                                        state.inbound.copy(settings = currentSettings.copy(users = updatedUsers))
                                    )
                                )
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Delete user",
                                tint = onSurface
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.mediumPadding),
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
                        horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.mediumPadding),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = "UUID", color = onSurface, modifier = Modifier.width(64.dp))
                        androidx.compose.foundation.text.selection.SelectionContainer {
                            Text(
                                text = user.id,
                                color = onSurface.copy(alpha = 0.7f),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(vertical = LocalDimensions.current.smallPadding)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.mediumPadding),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = "Flow", color = onSurface, modifier = Modifier.width(64.dp))
                        BlurredDropDown(
                            blurHolder = globalBlurHolder,
                            items = Configurations.vlessFlow.entries.map { it.name },
                            selectedItem = user.flow?.name ?: Configurations.vlessFlow.NONE.name,
                            onItemSelected = { selectedName ->
                                val flow = Configurations.vlessFlow.valueOf(selectedName)
                                val currentSettings =
                                    state.inbound.settings as? XrayConfig.VlessInboundSettings ?: return@BlurredDropDown
                                if (index < currentSettings.users.size) {
                                    val updatedUsers = currentSettings.users.toMutableList()
                                    updatedUsers[index] = updatedUsers[index].copy(flow = flow)
                                    editScreenModel.processIntent(
                                        EditIntent.UpdateInbound(
                                            state.inbound.copy(settings = currentSettings.copy(users = updatedUsers))
                                        )
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }

        Button(
            modifier = Modifier.wrapContentWidth().padding(top = LocalDimensions.current.mediumPadding),
            onClick = {
                editScreenModel.generateUuid()
                val currentSettings = state.inbound.settings as? XrayConfig.VlessInboundSettings
                    ?: XrayConfig.VlessInboundSettings()
                val newUser = XrayConfig.VlessUser(
                    id = editScreenModel.uuid.value,
                    email = "user${currentSettings.users.size + 1}@rayfield.com"
                )
                editScreenModel.processIntent(
                    EditIntent.UpdateInbound(
                        state.inbound.copy(
                            settings = currentSettings.copy(users = currentSettings.users + newUser)
                        )
                    )
                )
            }
        ) {
            Text("add user")
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = LocalDimensions.current.smallPadding),
            color = onSurface.copy(alpha = 0.2f)
        )

        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.mediumPadding),
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = "Decryption",
                color = onSurface,
                style = MaterialTheme.typography.titleMedium
            )
            BlurredDropDown(
                blurHolder = globalBlurHolder,
                items = Configurations.vlessDecryption.entries.map { it.name },
                selectedItem = state.inbound.settings.let {
                    (it as? XrayConfig.VlessInboundSettings)?.decryption?.name
                } ?: Configurations.vlessDecryption.NONE.name,
                onItemSelected = { selectedName ->
                    val decryption = Configurations.vlessDecryption.valueOf(selectedName)
                    val currentSettings =
                        state.inbound.settings as? XrayConfig.VlessInboundSettings
                            ?: return@BlurredDropDown
                    editScreenModel.processIntent(
                        EditIntent.UpdateInbound(
                            state.inbound.copy(settings = currentSettings.copy(decryption = decryption))
                        )
                    )
                }
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = LocalDimensions.current.smallPadding),
            color = onSurface.copy(alpha = 0.2f)
        )

        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.mediumPadding),
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = "Fallbacks",
                color = onSurface,
                style = MaterialTheme.typography.titleMedium
            )
            SettingOutlinedText(
                state = editScreenModel.fallbackDestState,
                label = { Text("Port") },
                modifier = Modifier.fillMaxWidth(),
                keyboardType = KeyboardType.Number
            )
        }
    }
}
