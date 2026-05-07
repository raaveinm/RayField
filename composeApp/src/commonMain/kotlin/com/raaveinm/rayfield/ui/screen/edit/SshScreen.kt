package com.raaveinm.rayfield.ui.screen.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.raaveinm.rayfield.data.ssh.ServerUnit
import com.raaveinm.rayfield.domain.helpers.LocalWindowSize
import com.raaveinm.rayfield.domain.helpers.WindowSize
import com.raaveinm.rayfield.ui.adapters.IpAutoFormatTransformation
import com.raaveinm.rayfield.ui.fragments.ConnectedButtonGroup
import com.raaveinm.rayfield.ui.fragments.SettingOutlinedText
import com.raaveinm.rayfield.ui.state.GlobalBlurHolder
import com.raaveinm.rayfield.ui.theme.LocalDimensions
import io.github.neilyich.glassmorphism.blurredBackground
import io.github.neilyich.glassmorphism.rememberBlurHolder

//
// Created by Kirill "Raaveinm" on 5/4/26.
//

@Composable
fun SshScreen(serverUnit: ServerUnit? = null) {
    val dimen = LocalDimensions.current
    val windowSize = LocalWindowSize.current

    val globalBlurHolder = GlobalBlurHolder.current ?: rememberBlurHolder()
    val lazyState = rememberLazyListState()

    // TODO("Replace with Intent State according to MVI")
    val ipState = TextFieldState(initialText = serverUnit?.serverIp ?: "")
    val portState = TextFieldState(initialText = serverUnit?.serverSshPort?.toString() ?: "22")
    val options = listOf("Password", "Key")
    var loginState by remember { mutableStateOf(options.first()) }
    val passwordState = TextFieldState(initialText = serverUnit?.serverSshPassword ?: "")
    val pathToFileState = TextFieldState(initialText = serverUnit?.serverSshPrivateKey ?: "")

    val onSurface = MaterialTheme.colorScheme.onSurface

    val padding = PaddingValues(
        vertical = dimen.sMediumMargin,
        horizontal = when(windowSize){
            WindowSize.EXPANDED -> dimen.sMediumMargin
            WindowSize.MEDIUM -> dimen.extraSmallMargin
            WindowSize.COMPACT -> dimen.smallSize
        }
    )

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
            contentPadding = padding,
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
                        state = ipState,
                        label = { Text("Server IP") },
                        modifier = Modifier.weight(0.7f),
                        isDone = false,
                        keyboardType = KeyboardType.Number,
                        inputTransformation = IpAutoFormatTransformation,
                        onKeyboardAction = {
                            // TODO(Handle Done action)
                        }
                    )

                    SettingOutlinedText(
                        state = portState,
                        label = { Text("Port") },
                        modifier = Modifier.weight(0.3f),
                        isDone = false,
                        keyboardType = KeyboardType.Number,
                        onKeyboardAction = {
                            // TODO(Handle Done action)
                        }
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SSH Auth Method",
                        color = onSurface
                    )
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
                        state = passwordState,
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth(),
                        isPassword = true
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text("FilePicker $pathToFileState", color = Color.Cyan)
                    }
                }
            }
        }
    }
}