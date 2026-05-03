package com.raaveinm.rayfield.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.raaveinm.rayfield.data.xray.types.ServerState
import com.raaveinm.rayfield.ui.fragments.ServerCard
import com.raaveinm.rayfield.ui.theme.LocalDimensions

data class MainScreen(
    val padding: PaddingValues,
    val modifier: Modifier = Modifier
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val state = rememberLazyGridState()
        val mediumPadding = LocalDimensions.current.mediumPadding // 16.dp

        val mockServers = remember {
            List(18) { index ->
                ServerState(
                    serverName = if (index == 4) "frankfurt" else "server_name",
                    serverAddress = "192.168.123.123:443",
                    sharedLink = "vless://fff73709-bide-120b-a853-2b9s3feas2rr" +
                            "@192.168.123.123:443?type=tcp&encryption=none",
                    protocol = "vless"
                )
            }
        }

        Column(modifier = modifier.fillMaxSize()) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 360.dp),
                state = state,
                contentPadding = padding,
                horizontalArrangement = Arrangement.spacedBy(mediumPadding),
                verticalArrangement = Arrangement.spacedBy(mediumPadding),
                modifier = Modifier.fillMaxSize()
            ) {
                items(mockServers) { server ->
                    ServerCard(
                        serverState = server,
                        modifier = Modifier.fillMaxWidth(),
                        onQrClick = { /* Handle QR */ },
                        onShareClick = { /* Handle Share */ },
                        onEditClick = { /* Handle Edit */ }
                    )
                }
            }
        }
    }
}
