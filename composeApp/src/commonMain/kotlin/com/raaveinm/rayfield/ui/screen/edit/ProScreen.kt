package com.raaveinm.rayfield.ui.screen.edit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.raaveinm.rayfield.data.xray.Configurations
import com.raaveinm.rayfield.ui.adapters.AdaptivePadding
import com.raaveinm.rayfield.ui.fragments.BlurredDropDown
import com.raaveinm.rayfield.ui.fragments.edit.SettingOutlinedText
import com.raaveinm.rayfield.ui.screen.LocalSharedEditModel
import com.raaveinm.rayfield.ui.state.GlobalBlurHolder
import com.raaveinm.rayfield.ui.state.configuration.EditIntent
import com.raaveinm.rayfield.ui.theme.LocalDimensions
import io.github.neilyich.glassmorphism.blurredBackground
import io.github.neilyich.glassmorphism.rememberBlurHolder

@Composable
fun Screen.ProScreen() {
    val globalBlurHolder = GlobalBlurHolder.current ?: rememberBlurHolder()
    val lazyState = rememberLazyListState()
    val editScreenModel = LocalSharedEditModel.current
    val state by editScreenModel.state.collectAsState()
    val pro = state.pro
    val onSurface = MaterialTheme.colorScheme.onSurface

    Box(
        Modifier
            .fillMaxSize()
            .blurredBackground(blurHolder = globalBlurHolder, blurRadius = 96.dp, tileMode = TileMode.Clamp),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = lazyState,
            contentPadding = AdaptivePadding.adaptiveAll,
            verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.mediumPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ///////////////////////////////////////////////
            // Log Configuration
            ///////////////////////////////////////////////
            item {
                Text(
                    text = "Logging Settings",
                    style = MaterialTheme.typography.titleMedium,
                    color = onSurface,
                    modifier = Modifier.fillMaxWidth()
                )
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = LocalDimensions.current.smallPadding),
                    color = onSurface.copy(alpha = 0.2f)
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.mediumPadding),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Log Level", color = onSurface)
                    BlurredDropDown(
                        blurHolder = globalBlurHolder,
                        items = Configurations.loglevel.entries.map { it.name },
                        selectedItem = pro.log.loglevel?.name ?: Configurations.loglevel.ERROR.name,
                        onItemSelected = { selected ->
                            editScreenModel.processIntent(
                                EditIntent.UpdateLogLevel(Configurations.loglevel.valueOf(selected))
                            )
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
                    Text(text = "Enable DNS Logging", color = onSurface)
                    Switch(
                        checked = pro.log.dnsLog ?: false,
                        onCheckedChange = { isChecked ->
                            editScreenModel.processIntent(EditIntent.UpdateDnsLogEnabled(isChecked))
                        }
                    )
                }
            }

            item {
                SettingOutlinedText(
                    state = editScreenModel.logAccessPathState,
                    label = { Text("Access Log Path") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                SettingOutlinedText(
                    state = editScreenModel.logErrorPathState,
                    label = { Text("Error Log Path") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                SettingOutlinedText(
                    state = editScreenModel.logMaskAddressState,
                    label = { Text("Mask Address") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            ///////////////////////////////////////////////
            // Routing Configuration
            ///////////////////////////////////////////////
            item {
                Spacer(modifier = Modifier.height(LocalDimensions.current.smallPadding))
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = LocalDimensions.current.smallPadding),
                    color = onSurface.copy(alpha = 0.2f)
                )
                Text(
                    text = "Routing Settings",
                    style = MaterialTheme.typography.titleMedium,
                    color = onSurface,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.mediumPadding),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Domain Strategy", color = onSurface)
                    BlurredDropDown(
                        blurHolder = globalBlurHolder,
                        items = Configurations.routingDomainStrategy.entries.map { it.name },
                        selectedItem = pro.routing.domainStrategy?.name ?: Configurations.routingDomainStrategy.AS_IS.name,
                        onItemSelected = { selected ->
                            editScreenModel.processIntent(
                                EditIntent.UpdateRoutingDomainStrategy(Configurations.routingDomainStrategy.valueOf(selected))
                            )
                        }
                    )
                }
            }

            ///////////////////////////////////////////////
            // Save Configuration Action
            ///////////////////////////////////////////////
            item {
                Spacer(modifier = Modifier.height(LocalDimensions.current.mediumPadding))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { editScreenModel.processIntent(EditIntent.Save) }
                ) {
                    Text("Save Full Configuration")
                }
            }
        }
    }
}
