package com.raaveinm.rayfield.ui.fragments

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.raaveinm.rayfield.data.xray.types.ServerState
import com.raaveinm.rayfield.ui.theme.LocalDimensions

@Composable
fun ConnectionInfoCard(
    modifier: Modifier = Modifier,
    serverState: ServerState,
    onCopyClick: (text: String) -> Unit = {},
    onQrClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onEditClick: () -> Unit = {}
) {
    var isExpanded by remember { mutableStateOf(false) }

    val dimensions = LocalDimensions.current
    val onContainerColor = MaterialTheme.colorScheme.onPrimaryContainer

    Card(
        modifier = modifier
            .width(360.dp)
            .wrapContentHeight()
            .animateContentSize(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp,
            pressedElevation = 2.dp,
            focusedElevation = 4.dp,
            hoveredElevation = 6.dp,
            draggedElevation = 12.dp,
            disabledElevation = 0.dp
        )
    ) {
        val displayName = remember(serverState.serverName, serverState.serverAddress) {
            if (serverState.serverName.isNullOrBlank()) serverState.serverAddress
            else serverState.serverName!!
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensions.mediumPadding)
                .padding(vertical = dimensions.mediumPadding),
            verticalArrangement = Arrangement.spacedBy(dimensions.smallPadding)
        ) {
            ///////////////////////////////////////////////
            // Upper Row
            ///////////////////////////////////////////////
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (serverState.iconLocation == null) {
                    Text(
                        text = displayName.take(1).uppercase(),
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(onContainerColor)
                            .wrapContentHeight(Alignment.CenterVertically),
                        style = MaterialTheme.typography.headlineLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
                } else {
                    AsyncImage(
                        model = serverState.iconLocation,
                        contentDescription = "server_icon",
                        modifier = Modifier.size(48.dp).clip(CircleShape),
                        contentScale = ContentScale.FillBounds
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = dimensions.mediumPadding),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = displayName,
                        style = MaterialTheme.typography.titleMedium,
                        color = onContainerColor,
                        maxLines = 1
                    )

                    HorizontalDivider(
                        thickness = 2.dp,
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                        modifier = Modifier.padding(vertical = 2.dp)
                    )

                    Text(
                        text = serverState.protocol.uppercase(),
                        style = MaterialTheme.typography.bodySmall,
                        color = onContainerColor
                    )
                }

                IconButton(
                    onClick = { isExpanded = !isExpanded },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Transparent)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp
                        else Icons.Outlined.KeyboardArrowDown,
                        contentDescription = "expand_card_button",
                        modifier = Modifier.size(dimensions.mediumSize),
                        tint = onContainerColor
                    )
                }
            }

            ///////////////////////////////////////////////
            // Expandable
            ///////////////////////////////////////////////
            if (isExpanded) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = serverState.sharedLink,
                        style = MaterialTheme.typography.labelSmall,
                        color = onContainerColor,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = dimensions.smallPadding)
                            .clickable(true) {
                                onCopyClick(serverState.sharedLink+"#${displayName}")
                            },
                        softWrap = true,
                        maxLines = 3
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ActionButton(
                            icon = Icons.Outlined.QrCode,
                            contentDescription = "qr_code_button",
                            onClick = onQrClick
                        )
                        ActionButton(
                            icon = Icons.Outlined.Share,
                            contentDescription = "share_server_button",
                            onClick = onShareClick
                        )
                        ActionButton(
                            icon = Icons.Outlined.Edit,
                            contentDescription = "edit_server_button",
                            onClick = onEditClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(LocalDimensions.current.largeSize),
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(LocalDimensions.current.mediumSize)
        )
    }
}

@Preview
@Composable
fun ServerCardPreview() {
    MaterialTheme {
        Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            ConnectionInfoCard(
                serverState = ServerState(
                    serverId = "1",
                    serverName = "Frankfurt Production",
                    serverAddress = "192.168.123.123:443",
                    sharedLink = "vless://fff73709-bide-120b-a853-2b9s3feas2rr@192.168.123.123:443?type=tcp&encryption=none#frankfut",
                    protocol = "vless"
                )
            )

            ConnectionInfoCard(
                serverState = ServerState(
                    serverId = "1",
                    serverName = null,
                    serverAddress = "lon.rayfield.net:8080",
                    sharedLink = "trojan://password@lon.rayfield.net:8080?security=tls&sni=lon.rayfield.net#London_TLS",
                    protocol = "trojan",

                ),
            )
        }
    }
}