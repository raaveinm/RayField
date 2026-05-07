package com.raaveinm.rayfield.ui.fragments

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.raaveinm.rayfield.data.ssh.ServerUnit
import com.raaveinm.rayfield.ui.adapters.AnyImage
import com.raaveinm.rayfield.ui.theme.LocalDimensions
import rayfield.composeapp.generated.resources.Res
import rayfield.composeapp.generated.resources.flag_germany

//
// Created by Kirill "Raaveinm" on 5/3/26.
//

@Composable
fun ServerInfoCard(
    modifier: Modifier = Modifier,
    server: ServerUnit,
    onClick: () -> Unit = {}
) {
    val dimensions = LocalDimensions.current

    val primaryContainer = MaterialTheme.colorScheme.primaryContainer
    val onPrimaryContainer = MaterialTheme.colorScheme.onPrimaryContainer
    val onSurface = MaterialTheme.colorScheme.onSurface
    val surface = MaterialTheme.colorScheme.surface

    Card(
        modifier = modifier.size(158.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = primaryContainer,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp,
            pressedElevation = 2.dp,
            focusedElevation = 4.dp,
            hoveredElevation = 6.dp,
            draggedElevation = 12.dp,
            disabledElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize().clickable{onClick()}.
            padding(
                horizontal = dimensions.mediumPadding,
                vertical = dimensions.smallPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val displayName = server.serverName ?: server.serverIp

            AnyImage(
                picture = server.iconLocation,
                name = displayName,
                size = 96.dp,
                textBackground = onPrimaryContainer,
                text = surface
            )

            Text(
                text = displayName,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = dimensions.sMediumPadding),
                color = onSurface,
                maxLines = 1
            )
        }
    }
}

@Preview
@Composable
fun ServerInfoCardPreview() {
    MaterialTheme {
        ServerInfoCard(
            server = ServerUnit(
                serverId = "1",
                serverName = "Frankfurt",
                serverIp = "192.168.123.123",
                serverSshLogin = "user",
                serverSshPassword = "",
                serverSshPrivateKey = null,
                serverSshPort = 22,
                iconLocation = Res.drawable.flag_germany
            )
        )
    }
}