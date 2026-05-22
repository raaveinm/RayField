package com.raaveinm.rayfield.ui.fragments.configurations

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable

//
// Created by Kirill "Raaveinm" on 5/19/26.
//

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.raaveinm.rayfield.data.xray.Configurations
import com.raaveinm.rayfield.ui.fragments.BlurredDropDown
import com.raaveinm.rayfield.ui.fragments.edit.SettingOutlinedText
import com.raaveinm.rayfield.ui.state.configuration.EditIntent
import com.raaveinm.rayfield.ui.state.configuration.EditScreenModel
import com.raaveinm.rayfield.ui.theme.LocalDimensions
import io.github.neilyich.glassmorphism.BlurHolder

@Composable
fun VlessStreamSettings(
    editScreenModel: EditScreenModel,
    globalBlurHolder: BlurHolder,
    onSurface: Color
) {
    val state by editScreenModel.state.collectAsState()
    val stream = state.stream

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Stream Settings (Transport & Security)",
            style = MaterialTheme.typography.titleMedium,
            color = onSurface
        )

        ///////////////////////////////////////////////
        // NetWork
        ///////////////////////////////////////////////

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Network", color = onSurface)
            BlurredDropDown(
                blurHolder = globalBlurHolder,
                items = Configurations.transportNetwork.entries.map { it.name },
                selectedItem = stream.network.name,
                onItemSelected = {
                    editScreenModel.processIntent(EditIntent.UpdateStreamNetwork(Configurations.transportNetwork.valueOf(it)))
                }
            )
        }

        if (stream.network == Configurations.transportNetwork.XHTTP) {
            SettingOutlinedText(
                state = editScreenModel.xhttpPathState,
                label = { Text("xHTTP Path (e.g. /download)") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            color = onSurface.copy(alpha = 0.2f)
        )

        ///////////////////////////////////////////////
        // Security
        ///////////////////////////////////////////////

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Security", color = onSurface)
            BlurredDropDown(
                blurHolder = globalBlurHolder,
                items = Configurations.security.entries.map { it.name },
                selectedItem = stream.security.name,
                onItemSelected = {
                    editScreenModel.processIntent(EditIntent.UpdateStreamSecurity(Configurations.security.valueOf(it)))
                }
            )
        }

        when (stream.security) {
            Configurations.security.TLS -> {
                SettingOutlinedText(
                    state = editScreenModel.tlsServerNameState,
                    label = { Text("TLS Server Name (SNI)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Configurations.security.REALITY -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SettingOutlinedText(
                        state = editScreenModel.realityDestState,
                        label = { Text("Reality Destination") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    val isCustomMode by editScreenModel.isCustomTarget.collectAsState()
                    val targetOptionsNames = Configurations.targetOptions.entries.map { it.name }
                    val dropdownItems = targetOptionsNames + "CUSTOM_MANUAL"

                    val currentTargetName = if (isCustomMode) {
                        "CUSTOM_MANUAL"
                    } else {
                        stream.realitySettings?.target?.name ?: Configurations.targetOptions.GITHUB.name
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Mask Target", color = onSurface)
                        BlurredDropDown(
                            blurHolder = globalBlurHolder,
                            items = dropdownItems,
                            selectedItem = currentTargetName,
                            onItemSelected = { selected ->
                                if (selected == "CUSTOM_MANUAL") {
                                    editScreenModel.setCustomTargetMode(true)
                                } else {
                                    editScreenModel.setCustomTargetMode(false)
                                    editScreenModel.processIntent(
                                        EditIntent.UpdateRealityTarget(Configurations.targetOptions.valueOf(selected))
                                    )
                                }
                            }
                        )
                    }

                    AnimatedVisibility (isCustomMode) {
                        SettingOutlinedText(
                            state = editScreenModel.realityCustomTargetState,
                            label = { Text("Custom Mask URL (e.g. my-mask-site.com)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    SettingOutlinedText(
                        state = editScreenModel.realityServerNamesState,
                        label = { Text("Server Names (comma separated)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    SettingOutlinedText(
                        state = editScreenModel.realityShortIdsState,
                        label = { Text("Short IDs (comma separated)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = onSurface.copy(alpha = 0.2f)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = { editScreenModel.generateRealityKeys() },
                            modifier = Modifier.wrapContentWidth().padding(end = LocalDimensions.current.smallPadding)
                        ) {
                            Text("Gen Keys")
                        }
                        Button(
                            onClick = { editScreenModel.generateShortId() },
                            modifier = Modifier.wrapContentWidth().padding(start = LocalDimensions.current.smallPadding)
                        ) {
                            Text("Gen ShortID")
                        }
                    }

                    SettingOutlinedText(
                        state = editScreenModel.realityPrivateKeyState,
                        label = { Text("Reality Private Key") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    SettingOutlinedText(
                        state = editScreenModel.realityPublicKeyState,
                        label = { Text("Reality Public Key") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            Configurations.security.NONE -> {}
        }
    }
}