package com.raaveinm.rayfield.ui.fragments.edit

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GeneratingTokens
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.ktor.websocket.Frame

//
// Created by Kirill "Raaveinm" on 5/11/26.
//

@Composable
fun CryptoTextField(
    state: TextFieldState,
    label: String,
    onGenerateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    PasswordTextField(
        state = state,
        label = { Frame.Text(label) },
        modifier = modifier,
        trailingIcon = {
            IconButton(onClick = onGenerateClick) {
                Icon(
                    imageVector = Icons.Filled.GeneratingTokens,
                    contentDescription = "Generate new $label",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}