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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import com.raaveinm.rayfield.domain.helpers.LocalWindowSize
import com.raaveinm.rayfield.domain.helpers.WindowSize
import com.raaveinm.rayfield.ui.fragments.ConnectionInfoCard
import com.raaveinm.rayfield.ui.mock.mockList
import com.raaveinm.rayfield.ui.navigation.EditTab
import com.raaveinm.rayfield.ui.state.MainScreenModel
import com.raaveinm.rayfield.ui.theme.LocalDimensions

class MainScreen : Screen {

    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<MainScreenModel>()
        val serverStates by screenModel.serverStates.collectAsState()
        val mockList = mockList

        val clipboardManager = LocalClipboardManager.current
        val navigator = LocalTabNavigator.current
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

        Column(modifier = Modifier.fillMaxSize()) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 360.dp),
                state = state,
                contentPadding = padding,
                horizontalArrangement = Arrangement.spacedBy(mediumPadding),
                verticalArrangement = Arrangement.spacedBy(mediumPadding),
                modifier = Modifier.fillMaxSize()
            ) {
                items(mockList) { serverState -> // TODO (serverState to read from db)
                    ConnectionInfoCard(
                        serverState = serverState,
                        modifier = Modifier.fillMaxWidth(),
                        onCopyClick = { text -> 
                            clipboardManager.setText(AnnotatedString(text))
                        },
                        onQrClick = { /* Handle QR */ },
                        onShareClick = { /* Handle Share */ },
                        onEditClick = { navigator.current = EditTab(serverState.serverId) }
                    )
                }
            }
        }
    }
}
