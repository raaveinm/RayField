package com.raaveinm.rayfield

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
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


    Box(modifier = modifier) {
        Box(
            modifier = Modifier.blurredContent(blurHolder)
        ) {
            Navigator(
                screen = MainScreen(
                    padding = PaddingValues(
                        top = LocalDimensions.current.sMediumMargin,
                        bottom = LocalDimensions.current.mediumMargin,
                        start = LocalDimensions.current.extraSmallMargin,
                        end = LocalDimensions.current.extraSmallMargin
                    )
                )
            ) { navigator ->
                SlideTransition(navigator)
            }
        }

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
            onHomeNavigation = { /*TODO*/ },
            onEditNavigation = { /*TODO*/ },
            onSettingsNavigation = { /*TODO*/ },
            onRawSshNavigation = { /*TODO*/ },
            onAddNewServerNavigation = { /*TODO*/ },
            selectedDestination = 0
        )
    }
}