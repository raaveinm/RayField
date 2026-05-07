package com.raaveinm.rayfield

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.raaveinm.rayfield.domain.helpers.CoilInitializer
import com.raaveinm.rayfield.domain.helpers.LocalWindowSize
import com.raaveinm.rayfield.domain.helpers.calculateWindowSize
import com.raaveinm.rayfield.ui.animations.AnimatedTabTransition
import com.raaveinm.rayfield.ui.bars.BottomBar
import com.raaveinm.rayfield.ui.bars.TopBar
import com.raaveinm.rayfield.ui.navigation.AddServerTab
import com.raaveinm.rayfield.ui.navigation.EditTab
import com.raaveinm.rayfield.ui.navigation.HomeTab
import com.raaveinm.rayfield.ui.navigation.LocalBackNavigator
import com.raaveinm.rayfield.ui.navigation.RawSshTab
import com.raaveinm.rayfield.ui.navigation.SettingsTab
import com.raaveinm.rayfield.ui.navigation.tabIndex
import com.raaveinm.rayfield.ui.state.GlobalBlurHolder
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
    val backNavigator = remember { mutableStateOf<Navigator?>(null) }

    CoilInitializer()

    CompositionLocalProvider(
        LocalWindowSize provides windowSize,
        LocalBackNavigator provides backNavigator
    ) {
        Box(modifier = modifier) {
            TabNavigator(HomeTab) { navigator ->
                Box(
                    modifier = Modifier.blurredContent(blurHolder)
                ) { AnimatedTabTransition(navigator) }

                Text(windowSize.toString(), color = MaterialTheme.colorScheme.onSurface)

                TopBar(
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(top = LocalDimensions.current.largeSize)
                        .align(Alignment.TopCenter),
                    onNavigationIconClick = {
                        val currentBackNav = backNavigator.value
                        if (currentBackNav != null && currentBackNav.canPop) {
                            currentBackNav.pop()
                        } else if (navigator.current != HomeTab) {
                            navigator.current = HomeTab
                        }
                    },
                    searchBarState = searchState,
                    textFieldState = textFieldState,
                    serverInfo = null,
                    icon = if (navigator.current == HomeTab && (backNavigator.value?.canPop != true)) 
                        Icons.Outlined.Home else Icons.Outlined.ChevronLeft,
                    blurHolder = blurHolder
                )
                BottomBar(
                    modifier = Modifier
                        .wrapContentWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = LocalDimensions.current.extraLargeSize),
                    blurHolder = blurHolder,
                    onHomeNavigation = { navigator.current = HomeTab },
                    onEditNavigation = { navigator.current = EditTab() },
                    onSettingsNavigation = { navigator.current = SettingsTab },
                    onRawSshNavigation = { navigator.current = RawSshTab },
                    onAddNewServerNavigation = { navigator.current = AddServerTab },
                    selectedDestination = navigator.current.tabIndex
                )
            }
        }
    }
}
