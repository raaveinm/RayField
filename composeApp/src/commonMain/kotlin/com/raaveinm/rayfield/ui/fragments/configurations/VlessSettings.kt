package com.raaveinm.rayfield.ui.fragments.configurations

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Save
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.raaveinm.rayfield.data.xray.Configurations
import com.raaveinm.rayfield.data.xray.XrayConfig
import com.raaveinm.rayfield.ui.adapters.AnyImage
import com.raaveinm.rayfield.ui.fragments.BlurredDropDown
import com.raaveinm.rayfield.ui.fragments.ErrorCard
import com.raaveinm.rayfield.ui.fragments.ImagePicker
import com.raaveinm.rayfield.ui.fragments.WarningLevel
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
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.mediumPadding),
    ) {
        users.forEachIndexed { index, user ->
            val customTextFieldState = rememberTextFieldState()
            var isVisible by remember { mutableStateOf(true) }
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
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = LocalDimensions.current.smallPadding),
                        color = onSurface.copy(alpha = 0.2f)
                    )
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
                    
                    AnimatedVisibility(user.flow == null || user.flow == Configurations.vlessFlow.NONE) {
                        ErrorCard(
                            text = "Uses standard TLS proxy, provides less privacy",
                            onClick = {uriHandler.openUri("https://xtls.github.io/en/config/inbounds/vless.html#userobject")},
                            level = WarningLevel.NOTE
                        )
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
                        SelectionContainer {
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
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.mediumPadding),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AnimatedVisibility(isVisible) {
                            ImagePicker(
//                                modifier = Modifier.weight(1f),
                                onImageSelected = { anyImage ->
                                    editScreenModel.processIntent(
                                        EditIntent.SetIconUserConfig(
                                            user.id,
                                            anyImage
                                        )
                                    )
                                },
                                onCustomSelected = {
                                    isVisible = false
                                    editScreenModel.processIntent(
                                        EditIntent.SetIconUserConfig(
                                            user.id,
                                            null
                                        )
                                    )
                                }
                            )
                        }
                        AnimatedVisibility(!isVisible) {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.mediumPadding),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AnyImage(
                                    picture = state.configIcons[user.id],
                                    name = user.email.ifEmpty { "User" },
                                    size = 96.dp,
                                    textBackground = MaterialTheme.colorScheme.secondaryContainer,
                                    text = MaterialTheme.colorScheme.onSecondaryContainer,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                SettingOutlinedText(
                                    state = customTextFieldState,
                                    modifier = Modifier.weight(1f),
                                    label = { Text("User Icon") },
                                    isDone = true,
                                    keyboardType = KeyboardType.Uri,
                                    trailingIcon = {
                                        IconButton(onClick = { isVisible = true }) {
                                            Icon(
                                                imageVector = Icons.Outlined.Delete,
                                                contentDescription = "delete_icon",
                                                tint = onSurface
                                            )
                                        }
                                    }
                                )
                                IconButton(onClick = {
                                    editScreenModel.processIntent(
                                        EditIntent.SetIconUserConfig(
                                            user.id,
                                            customTextFieldState.text.toString()
                                        )
                                    )
                                }) {
                                    Icon(
                                        imageVector = Icons.Outlined.Save,
                                        modifier = Modifier.size(64.dp),
                                        contentDescription = "save_icon",
                                        tint = onSurface
                                    )
                                }
                            }
                        }
                    }
                    
                    if (index == users.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = LocalDimensions.current.smallPadding),
                            color = onSurface.copy(alpha = 0.2f)
                        )
                    }
                }
            }
        }

        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Button(
                modifier = Modifier.wrapContentWidth()
                    .padding(top = LocalDimensions.current.mediumPadding),
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
            ) { Text("add user") }
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = LocalDimensions.current.smallPadding),
            color = onSurface.copy(alpha = 0.2f)
        )

        val selectedDecryption = state.inbound.vlessDecryption

        AnimatedVisibility (selectedDecryption != Configurations.vlessDecryption.NONE) {
            ErrorCard(
                "ERR: VLESS with ${selectedDecryption.name.lowercase()} is non-standard. " +
                        "Proceed with caution (configuration may be invalid).",
                onClick = { uriHandler.openUri("https://xtls.github.io/en/config/inbounds/vless.html") }
            )
        }

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
                selectedItem = selectedDecryption.name,
                onItemSelected = { selectedName ->
                    val decryption = Configurations.vlessDecryption.valueOf(selectedName)
                    editScreenModel.processIntent(EditIntent.UpdateVlessDecryption(decryption))
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
