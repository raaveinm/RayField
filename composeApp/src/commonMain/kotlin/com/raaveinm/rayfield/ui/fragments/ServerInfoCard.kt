package com.raaveinm.rayfield.ui.fragments

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.raaveinm.rayfield.data.ssh.ServerUnit
import com.raaveinm.rayfield.ui.theme.LocalDimensions

//
// Created by Kirill "Raaveinm" on 5/3/26.
//

@Composable
fun ServerInfoCard(
    modifier: Modifier = Modifier,
    server: ServerUnit,
    onClick: () -> Unit = {},
    picture: String? = null,
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
            if (picture == null) {
                Text(
                    text = displayName.take(1).uppercase(),
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(onPrimaryContainer)
                        .wrapContentHeight(Alignment.CenterVertically),
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center,
                    color = surface
                )
            } else {
                AsyncImage(
                    model = picture,
                    contentDescription = "server_icon",
                    modifier = Modifier.size(96.dp).clip(CircleShape),
                    contentScale = ContentScale.FillBounds
                )
            }

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
            )
        )
    }
}