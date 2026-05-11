package com.raaveinm.rayfield.ui.fragments.edit

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.ktor.websocket.Frame

//
// Created by Kirill "Raaveinm" on 5/11/26.
//

@Composable
fun CryptoTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    onGenerateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Frame.Text(label) },
        modifier = modifier.fillMaxWidth(),
        trailingIcon = {
            IconButton(onClick = onGenerateClick) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Generate new $label",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        singleLine = true
    )
}