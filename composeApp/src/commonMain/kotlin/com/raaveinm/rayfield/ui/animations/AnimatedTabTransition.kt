package com.raaveinm.rayfield.ui.animations

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.raaveinm.rayfield.ui.navigation.tabIndex

//
// Created by Kirill "Raaveinm" on 5/4/26.
//

@Composable
fun AnimatedTabTransition(navigator: TabNavigator) {
    AnimatedContent(
        targetState = navigator.current,
        transitionSpec = {
            val initialIndex = initialState.tabIndex
            val targetIndex = targetState.tabIndex

            if (targetIndex > initialIndex) {
                slideInHorizontally(initialOffsetX = { it }) togetherWith
                        slideOutHorizontally(targetOffsetX = { -it })
            } else {
                slideInHorizontally(initialOffsetX = { -it }) togetherWith
                        slideOutHorizontally(targetOffsetX = { it })
            }
        },
        label = "TabTransition"
    ) { tab ->
        // FIX: Use tab.key so the incoming and outgoing tabs don't collide!
        navigator.saveableState(key = tab.key, tab = tab) {
            tab.Content()
        }
    }
}