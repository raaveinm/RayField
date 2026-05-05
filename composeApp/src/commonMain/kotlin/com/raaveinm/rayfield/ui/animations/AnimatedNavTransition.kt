package com.raaveinm.rayfield.ui.animations

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import cafe.adriel.voyager.core.stack.StackEvent
import cafe.adriel.voyager.navigator.Navigator
import com.raaveinm.rayfield.ui.navigation.LocalBackNavigator

//
// Created by Kirill "Raaveinm" on 5/4/26.
//

@Composable
fun AnimatedNavTransition(navigator: Navigator) {
    val backNavState = LocalBackNavigator.current
    DisposableEffect(navigator) {
        backNavState.value = navigator
        onDispose {
            if (backNavState.value == navigator) {
                backNavState.value = null
            }
        }
    }
    AnimatedContent(
        targetState = navigator.lastItem,
        transitionSpec = {
            when (navigator.lastEvent) {
                StackEvent.Pop -> {
                    slideInHorizontally(initialOffsetX = { -it }) togetherWith
                            slideOutHorizontally(targetOffsetX = { it })
                }
                else -> {
                    slideInHorizontally(initialOffsetX = { it }) togetherWith
                            slideOutHorizontally(targetOffsetX = { -it })
                }
            }
        },
        label = "NavigatorTransition"
    ) { screen ->
        navigator.saveableState("navigator", screen) {
            screen.Content()
        }
    }
}