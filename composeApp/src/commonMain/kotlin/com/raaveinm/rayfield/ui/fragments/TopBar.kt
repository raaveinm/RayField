package com.raaveinm.rayfield.ui.fragments

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.raaveinm.rayfield.data.ssh.ServerInfo

@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    onNavigationIconClick: () -> Unit,
    searchBarState: (() -> Unit)? = null,
    serverData: ServerInfo? = null,
) {

}