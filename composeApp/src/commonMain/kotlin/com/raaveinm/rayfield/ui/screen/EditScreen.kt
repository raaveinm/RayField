package com.raaveinm.rayfield.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.raaveinm.rayfield.domain.helpers.LocalWindowSize
import com.raaveinm.rayfield.domain.helpers.WindowSize
import com.raaveinm.rayfield.ui.animations.AnimatedTabTransition
import com.raaveinm.rayfield.ui.fragments.DisplayGrid
import com.raaveinm.rayfield.ui.navigation.InboundTab
import com.raaveinm.rayfield.ui.navigation.OutboundTab
import com.raaveinm.rayfield.ui.navigation.ProTab
import com.raaveinm.rayfield.ui.navigation.SshTab
import com.raaveinm.rayfield.ui.navigation.StreamTab
import com.raaveinm.rayfield.ui.state.GlobalBlurHolder
import com.raaveinm.rayfield.ui.theme.LocalDimensions
import io.github.neilyich.glassmorphism.blurredBackground
import io.github.neilyich.glassmorphism.blurredContent
import io.github.neilyich.glassmorphism.rememberBlurHolder

//
// Created by Kirill "Raaveinm" on 5/3/26.
//

data class EditScreen (
    val serverId: String? = null
) : Screen {

    override val key: ScreenKey = "EditScreen:${serverId ?: "root"}"

    @Preview
    @Composable
    override fun Content() {
        val globalBlurHolder = GlobalBlurHolder.current ?: rememberBlurHolder()
        val localBlurHolder = rememberBlurHolder()
        val navigator = LocalNavigator.currentOrThrow

        val dimen = LocalDimensions.current

        val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.96f)
        val onInvertSurface = MaterialTheme.colorScheme.surface.copy(alpha = .84f)
        val primary = MaterialTheme.colorScheme.primary
        val onSurface = MaterialTheme.colorScheme.onSurface

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (serverId == null) {
                DisplayGrid(navigator)
                return@Box
            }

            val windowSize = LocalWindowSize.current
            val scrollState = rememberScrollState()
            val tabs = listOf(SshTab(), InboundTab, StreamTab, OutboundTab, ProTab)

            val padding = PaddingValues(
                top = dimen.sMediumMargin,
                bottom = dimen.sMediumMargin,
                start = when (windowSize) {
                    WindowSize.EXPANDED -> dimen.sMediumMargin
                    WindowSize.MEDIUM -> dimen.extraSmallMargin
                    else -> dimen.mediumPadding
                },
                end = when (windowSize) {
                    WindowSize.EXPANDED -> dimen.sMediumMargin
                    WindowSize.MEDIUM -> dimen.extraSmallMargin
                    else -> dimen.mediumPadding
                }
            )

            TabNavigator(tabs.first()) { tabNavigator ->
                Box(
                    modifier = Modifier
                        .padding(padding)
                        .clip(RoundedCornerShape(24.dp))
                        .shadow(8.dp)
                        .blurredBackground(
                            blurHolder = globalBlurHolder,
                            blurRadius = 48.dp,
                            tileMode = TileMode.Mirror
                        )
                        .background(surfaceVariant),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .blurredContent(localBlurHolder),
                        contentAlignment = Alignment.Center
                    ) { AnimatedTabTransition(tabNavigator) }
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = dimen.mediumPadding)
                            .clip(CircleShape)
                            .zIndex(1f)
                            .blurredBackground(
                                blurHolder = localBlurHolder,
                                blurRadius = 8.dp,
                                tileMode = TileMode.Mirror
                            )
                            .background(onInvertSurface)
                            .horizontalScroll(scrollState),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        tabs.forEach { tab ->
                            val isSelected = tabNavigator.current == tab
                            Box(
                                modifier = Modifier
                                    .height(48.dp)
                                    .clickable { tabNavigator.current = tab }
                                    .padding(horizontal = dimen.mediumPadding),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = tab.options.title,
                                    color = if (isSelected) primary else onSurface
                                )

                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .padding(bottom = 2.dp)
                                            .height(3.dp)
                                            .width(28.dp)
                                            .clip(CircleShape)
                                            .background(primary)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
