package com.raaveinm.rayfield.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.raaveinm.rayfield.domain.helpers.LocalWindowSize
import com.raaveinm.rayfield.domain.helpers.WindowSize
import com.raaveinm.rayfield.ui.fragments.ConnectionInfoCard
import com.raaveinm.rayfield.ui.mock.mockServers
import com.raaveinm.rayfield.ui.theme.LocalDimensions

class MainScreen(
    val modifier: Modifier = Modifier
) : Screen {

    @Composable
    override fun Content() {
        val clipboardManager = LocalClipboardManager.current
        val state = rememberLazyGridState()
        val dimen = LocalDimensions.current
        val windowSize = LocalWindowSize.current
        val mediumPadding = dimen.mediumPadding // 16.dp

        val padding = PaddingValues(
            top = dimen.sMediumMargin,
            bottom = dimen.mediumMargin,
            start = if (windowSize != WindowSize.COMPACT) dimen.extraSmallMargin
            else dimen.mediumPadding,
            end = if (windowSize != WindowSize.COMPACT) dimen.extraSmallMargin
            else dimen.mediumPadding
        )

        Column(modifier = modifier.fillMaxSize()) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 360.dp),
                state = state,
                contentPadding = padding,
                horizontalArrangement = Arrangement.spacedBy(mediumPadding),
                verticalArrangement = Arrangement.spacedBy(mediumPadding),
                modifier = Modifier.fillMaxSize()
            ) {
                items(mockServers()) { server ->
                    ConnectionInfoCard(
                        serverState = server,
                        modifier = Modifier.fillMaxWidth(),
                        onCopyClick = { text -> 
                            clipboardManager.setText(AnnotatedString(text))
                        },
                        onQrClick = { /* Handle QR */ },
                        onShareClick = { /* Handle Share */ },
                        onEditClick = { /* Handle Edit */ }
                    )
                }
            }
        }
    }
}
