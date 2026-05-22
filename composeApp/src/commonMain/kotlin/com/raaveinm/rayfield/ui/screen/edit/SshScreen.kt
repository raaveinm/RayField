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
import cafe.adriel.voyager.koin.koinScreenModel
import com.raaveinm.rayfield.ui.adapters.AdaptivePadding
import com.raaveinm.rayfield.ui.adapters.IpAutoFormatTransformation
import com.raaveinm.rayfield.ui.fragments.ConnectedButtonGroup
import com.raaveinm.rayfield.ui.fragments.edit.SettingOutlinedText
import com.raaveinm.rayfield.ui.state.GlobalBlurHolder
import com.raaveinm.rayfield.ui.state.configuration.SshIntent
import com.raaveinm.rayfield.ui.state.configuration.SshScreenModel
import io.github.neilyich.glassmorphism.blurredBackground
import io.github.neilyich.glassmorphism.rememberBlurHolder
import org.koin.core.parameter.parametersOf

//
// Created by Kirill "Raaveinm" on 5/4/26.
//

@Composable
fun Screen.SshScreen(serverId: String? = null) {
    val screenModel = koinScreenModel<SshScreenModel> { parametersOf(serverId) }
    val state by screenModel.state.collectAsState()

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
                        state = screenModel.ipState,
                        label = { Text("Server IP") },
                        modifier = Modifier.weight(0.7f),
                        isDone = false,
                        isError = screenModel.ipState.text.toString().isEmpty(),
                        keyboardType = KeyboardType.Number,
                        inputTransformation = IpAutoFormatTransformation
                    )

                    SettingOutlinedText(
                        state = screenModel.portState,
                        label = { Text("Port") },
                        modifier = Modifier.weight(0.3f),
                        isError = screenModel.portState.text.toString().isEmpty(),
                        isDone = false,
                        keyboardType = KeyboardType.Number
                    )
                }
            }

            item {
                SettingOutlinedText(
                    state = screenModel.loginState,
                    label = { Text("User") },
                    isError = screenModel.loginState.text.toString().isEmpty(),
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
                        state = screenModel.passwordState,
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth(),
                        isPassword = true
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text("FilePicker ${screenModel.pathToPkeyState.text}", color = Color.Cyan)
                    }
                }
            }

            item {
                SettingOutlinedText(
                    state = screenModel.nameState,
                    label = { Text("server displayed name") },
                    modifier = Modifier.fillMaxWidth(),
                    isDone = true,
                    onKeyboardAction = {
                        screenModel.processIntent(SshIntent.Save)
                    }
                )

            }

            item {
                Button(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    onClick = { screenModel.processIntent(SshIntent.Save) }
                ) {
                    Text("Save Server Configuration")
                }
            }


            item {
                Text("server id: ${state.serverId}\nserver name: ${state.name}\nserver ip: " +
                        "${state.ip}\n server login: ${state.login}\nserver password: " +
                        "${state.password}\nserver port: ${state.port}\n",  color = Color.Cyan)
            }
        }
    }
}
}
