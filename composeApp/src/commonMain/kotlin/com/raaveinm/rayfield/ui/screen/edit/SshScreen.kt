package com.raaveinm.rayfield.ui.screen.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.raaveinm.rayfield.ui.adapters.AdaptivePadding
import com.raaveinm.rayfield.ui.adapters.IpAutoFormatTransformation
import com.raaveinm.rayfield.ui.fragments.ConnectedButtonGroup
import com.raaveinm.rayfield.ui.fragments.edit.SettingOutlinedText
import com.raaveinm.rayfield.ui.screen.LocalSharedEditModel
import com.raaveinm.rayfield.ui.state.GlobalBlurHolder
import com.raaveinm.rayfield.ui.state.configuration.EditIntent
import io.github.neilyich.glassmorphism.blurredBackground
import io.github.neilyich.glassmorphism.rememberBlurHolder

//
// Created by Kirill "Raaveinm" on 5/4/26.
//

@Composable
fun Screen.SshScreen(serverId: String? = null) {
    val editScreenModel = LocalSharedEditModel.current
    val state by editScreenModel.state.collectAsState()

    val globalBlurHolder = GlobalBlurHolder.current ?: rememberBlurHolder()
    val lazyState = rememberLazyListState()

    val options = listOf("Password", "Key")
    var loginState by remember { mutableStateOf(options.first()) }

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
        if (state.isLoading) {
            CircularProgressIndicator()
        } else {
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
                        SettingOutlinedText(
                            state = editScreenModel.serverAddressState,
                            label = { Text("Server IP") },
                            modifier = Modifier.weight(0.7f),
                            isDone = false,
                            isError = editScreenModel.serverAddressState.text.toString().isEmpty(),
                            keyboardType = KeyboardType.Number,
                            inputTransformation = IpAutoFormatTransformation
                        )

                        SettingOutlinedText(
                            state = editScreenModel.sshPortState,
                            label = { Text("Port") },
                            modifier = Modifier.weight(0.3f),
                            isError = editScreenModel.sshPortState.text.toString().isEmpty(),
                            isDone = false,
                            keyboardType = KeyboardType.Number
                        )
                    }
                }

                item {
                    SettingOutlinedText(
                        state = editScreenModel.sshLoginState,
                        label = { Text("User") },
                        isError = editScreenModel.sshLoginState.text.toString().isEmpty(),
                        modifier = Modifier.fillMaxWidth(),
                        isDone = false
                    )
                }


                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "SSH Auth Method", color = MaterialTheme.colorScheme.onSurface)
                        ConnectedButtonGroup(
                            options = options,
                            selectedOption = loginState,
                            onOptionSelected = { selectedOption -> loginState = selectedOption }
                        )
                    }
                }

                item {
                    if (loginState == "Password") {
                        SettingOutlinedText(
                            state = editScreenModel.sshPasswordState,
                            label = { Text("Password") },
                            modifier = Modifier.fillMaxWidth(),
                            isPassword = true
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text("FilePicker ${editScreenModel.sshPathToPkeyState.text}", color = Color.Cyan)
                        }
                    }
                }

                item {
                    SettingOutlinedText(
                        state = editScreenModel.connectionNameState,
                        label = { Text("server displayed name") },
                        modifier = Modifier.fillMaxWidth(),
                        isDone = true,
                        onKeyboardAction = {
                            editScreenModel.processIntent(EditIntent.Save)
                        }
                    )

                }

                item {
                    androidx.compose.material3.HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                    )
                }
                item {
                    Button(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        onClick = { editScreenModel.processIntent(EditIntent.Save) }
                    ) {
                        Text("Save Server Configuration")
                    }
                }
            }
        }
    }
}
