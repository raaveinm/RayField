package com.raaveinm.rayfield

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.raaveinm.rayfield.domain.helpers.LocalWindowSize
import com.raaveinm.rayfield.domain.helpers.calculateWindowSize
import com.raaveinm.rayfield.ui.bars.BottomBar
import com.raaveinm.rayfield.ui.bars.TopBar
import com.raaveinm.rayfield.ui.screen.MainScreen
import com.raaveinm.rayfield.ui.theme.LocalDimensions
import io.github.neilyich.glassmorphism.BlurHolder
import io.github.neilyich.glassmorphism.blurredContent
import io.github.neilyich.glassmorphism.rememberBlurHolder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    modifier: Modifier = Modifier,
    blurHolder: BlurHolder = rememberBlurHolder()
) {
    val searchState: SearchBarState = rememberSearchBarState()
    val textFieldState = rememberTextFieldState()
    val windowSize = calculateWindowSize()

    CompositionLocalProvider(LocalWindowSize provides windowSize) {
        Box(modifier = modifier) {
            Box(
                modifier = Modifier.blurredContent(blurHolder)
            ) {
                Navigator(
                    screen = MainScreen()
                ) { navigator ->
                    SlideTransition(navigator)
                }
            }
            Text(windowSize.toString(), color = MaterialTheme.colorScheme.onSurface)
            var currentDestination by rememberSaveable { mutableIntStateOf(0) }

            TopBar(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(top = LocalDimensions.current.largeSize)
                    .align(Alignment.TopCenter),
                searchBarState = searchState,
                textFieldState = textFieldState,
                serverInfo = null,
                blurHolder = blurHolder
            )
            BottomBar(
                modifier = Modifier
                    .wrapContentWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = LocalDimensions.current.extraLargeSize),
                blurHolder = blurHolder,
                onHomeNavigation = { currentDestination = 0 },
                onEditNavigation = { currentDestination = 1 },
                onSettingsNavigation = { currentDestination = 2 },
                onRawSshNavigation = { currentDestination = 3 },
                onAddNewServerNavigation = { currentDestination = -1 },
                selectedDestination = currentDestination
            )
        }
    }
}
