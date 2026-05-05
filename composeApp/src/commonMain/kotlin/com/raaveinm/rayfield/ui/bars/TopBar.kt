package com.raaveinm.rayfield.ui.bars

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.raaveinm.rayfield.data.ssh.ServerUnit
import com.raaveinm.rayfield.ui.theme.LocalDimensions
import io.github.neilyich.glassmorphism.BlurHolder
import io.github.neilyich.glassmorphism.blurredBackground
import io.github.neilyich.glassmorphism.rememberBlurHolder
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    onNavigationIconClick: () -> Unit = {},
    searchBarState: SearchBarState? = null,
    textFieldState: TextFieldState? = null,
    serverInfo: ServerUnit? = null,
    icon: ImageVector = Icons.Outlined.Home,
    blurHolder: BlurHolder,
) {
    val scope = rememberCoroutineScope()
    val tintColor = MaterialTheme.colorScheme.primary.copy(alpha = .64f)

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(26.dp))
            .blurredBackground(
                blurHolder = blurHolder,
                blurRadius = 8.dp,
                tileMode = TileMode.Decal
            )
            .background(tintColor),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onNavigationIconClick) {
            Icon(
                icon,
                contentDescription = "pop_back",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        when {
            searchBarState != null && textFieldState != null -> {
                SearchBar(
                    modifier = Modifier.height(56.dp),
                    colors = SearchBarDefaults.colors (
                        containerColor = Color.Transparent,
                    ),
                    state = searchBarState,
                    inputField = {
                        SearchBarDefaults.InputField(
                            textFieldState = textFieldState,
                            searchBarState = searchBarState,
                            onSearch = { _: String ->
                                scope.launch {
                                    searchBarState.animateToCollapsed()
                                }
                            },
                            placeholder = { Text("Search") },
                            trailingIcon = {
                                Icon(Icons.Default.Search, contentDescription = "Search")
                            },
                        )
                    }
                )
            }

            serverInfo != null -> {
                Column(
                    modifier = Modifier
                        .padding(start = LocalDimensions.current.mediumPadding)
                        .weight(1f)
                ) {
                    Text(
                        text = serverInfo.serverName ?: serverInfo.serverIp,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1
                    )
                    Text(
                        text = if (serverInfo.serverName != null) serverInfo.serverIp
                        else "${serverInfo.serverSshLogin}@${serverInfo.serverIp}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }

            else -> {
                Text(
                    text = "RayField",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(start = LocalDimensions.current.mediumPadding)
                        .weight(1f)
                )
            }
        }
    }
}
//
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun TopBarWithSearchPreview() {
    val searchState: SearchBarState = rememberSearchBarState()
    val textFieldState = rememberTextFieldState()
    TopBar(
        searchBarState = searchState,
        textFieldState = textFieldState,
        blurHolder = rememberBlurHolder()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun TopBarWithSearchProcessPreview() {
    val searchState: SearchBarState = rememberSearchBarState()
    val textFieldState = rememberTextFieldState("Sample Text")
    TopBar(
        searchBarState = searchState,
        textFieldState = textFieldState,
        blurHolder = rememberBlurHolder()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun TopBarWithServerInfoPreview() {
    TopBar(
        serverInfo = ServerUnit(
            serverIp = "192.168.123.123",serverName = "Home Server",
            serverId = "1", serverSshLogin = "user", serverSshPassword = "",
            serverSshPrivateKey = null, serverSshPort = 22
        ),
        blurHolder = rememberBlurHolder()
    )
}
