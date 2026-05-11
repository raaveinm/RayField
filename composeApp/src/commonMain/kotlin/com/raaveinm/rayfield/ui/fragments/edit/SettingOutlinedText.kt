package com.raaveinm.rayfield.ui.fragments.edit

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicSecureTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.delete
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults.FocusedBorderThickness
import androidx.compose.material3.OutlinedTextFieldDefaults.UnfocusedBorderThickness
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation

//
// Created by Kirill "Raaveinm" on 5/6/26.
//

@Composable
fun SettingOutlinedText(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    label: @Composable () -> Unit = {},
    supportingText: @Composable () -> Unit = {},
    isDone: Boolean = false,
    isError: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    inputTransformation: InputTransformation? = null,
    outputTransformation: OutputTransformation? = null,
    isPassword: Boolean = false,
    onKeyboardAction: KeyboardActionHandler? = null
) {
    val scrollState = rememberScrollState()

    val effectiveKeyboardOptions = KeyboardOptions(
        keyboardType = keyboardType,
        imeAction = if (isDone) ImeAction.Done else ImeAction.Next
    )

    if (isPassword) {
        PasswordTextField(state = state, modifier = modifier)
        return
    }

    OutlinedTextField(
        state = state,
        modifier = modifier,
        label = { label() },
        trailingIcon = {
            Row {
                if (state.text.isNotEmpty()) {
                    IconButton(
                        onClick = { state.edit { delete(0, length) } }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "ClearText",
                        )
                    }
                }
            }
        },
        supportingText = supportingText,
        isError = isError,
        keyboardOptions = effectiveKeyboardOptions,
        onKeyboardAction = onKeyboardAction,
        inputTransformation = inputTransformation,
        outputTransformation = outputTransformation,
        lineLimits = TextFieldLineLimits.SingleLine,
        scrollState = scrollState,
        colors = OutlinedTextFieldDefaults.colors(),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PasswordTextField(
    modifier: Modifier = Modifier,
    state: TextFieldState
) {
    var showPassword by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    val mergedTextStyle = LocalTextStyle.current.copy(
        color = MaterialTheme.colorScheme.onSurface
    )

    BasicSecureTextField(
        state = state,
        textStyle = mergedTextStyle,
        textObfuscationMode =
            if (showPassword) TextObfuscationMode.Visible
            else TextObfuscationMode.RevealLastTyped,
        modifier = modifier,
        interactionSource = interactionSource,
        decorator = { innerTextField ->
            OutlinedTextFieldDefaults.DecorationBox(
                value = state.text.toString(),
                innerTextField = innerTextField,
                enabled = true,
                singleLine = true,
                visualTransformation = VisualTransformation.None,
                interactionSource = interactionSource,
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            imageVector =
                                if (showPassword) Icons.Filled.Visibility
                                else Icons.Filled.VisibilityOff,
                            contentDescription =
                                if (showPassword) "Hide password"
                                else "Show password"
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(),
                container = {
                    OutlinedTextFieldDefaults.Container(
                        enabled = true,
                        isError = false,
                        interactionSource = interactionSource,
                        colors = OutlinedTextFieldDefaults.colors(),
                        shape = OutlinedTextFieldDefaults.shape,
                        focusedBorderThickness = FocusedBorderThickness,
                        unfocusedBorderThickness = UnfocusedBorderThickness,
                    )
                }
            )
        }
    )
}