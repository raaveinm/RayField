package com.raaveinm.rayfield.ui.fragments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import com.raaveinm.rayfield.data.ssh.ServerUnit
import com.raaveinm.rayfield.domain.helpers.WindowSize
import com.raaveinm.rayfield.domain.helpers.calculateWindowSize
import com.raaveinm.rayfield.ui.adapters.horizontalMouseScroll
import com.raaveinm.rayfield.ui.screen.EditScreen
import com.raaveinm.rayfield.ui.theme.LocalDimensions

//
// Created by Kirill "Raaveinm" on 5/4/26.
//


@Composable fun DisplayGrid(
    serverList: List<ServerUnit>? = null,
    onClick: (ServerUnit) -> Unit = {},
    onLongClick: (ServerUnit) -> Unit = {}
) {
    val dimen = LocalDimensions.current
    val windowSize = calculateWindowSize()
    val state = rememberLazyGridState()

    val combinedServers = (serverList ?: emptyList())// + mockServers

    if (windowSize != WindowSize.COMPACT) {
        LazyHorizontalGrid(
            modifier = Modifier
                .horizontalMouseScroll(state)
                .wrapContentSize()
                .height((158 * 2).dp + dimen.sMediumPadding),
            rows = GridCells.Fixed(2),
            state = state,
            horizontalArrangement = Arrangement.spacedBy(dimen.sMediumPadding),
            verticalArrangement = Arrangement.spacedBy(dimen.sMediumPadding),
        ) {
            items(combinedServers) { server ->
                ServerInfoCard(
                    modifier = Modifier.wrapContentSize(),
                    server = server,
                    onClick = { onClick(server) }
                )
            }
        }
    } else {
        val padding = PaddingValues(
            top = dimen.sMediumMargin,
            bottom = dimen.mediumMargin,
            start = dimen.mediumPadding,
            end = dimen.mediumPadding
        )
        LazyVerticalGrid(
            modifier = Modifier
                .wrapContentSize()
                .width((158 * 2).dp + dimen.sMediumPadding),
            columns = GridCells.Fixed(2),
            contentPadding = padding,
            state = state,
            horizontalArrangement = Arrangement.spacedBy(dimen.sMediumPadding),
            verticalArrangement = Arrangement.spacedBy(dimen.sMediumPadding),
        ) {
            items(combinedServers) { server ->
                ServerInfoCard(
                    modifier = Modifier.wrapContentSize(),
                    server = server,
                    onClick = { onClick(server) }
                )
            }
        }
    }
}