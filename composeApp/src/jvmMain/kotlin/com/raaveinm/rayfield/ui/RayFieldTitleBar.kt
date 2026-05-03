package com.raaveinm.rayfield.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RoundedCorner
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowScope
import com.raaveinm.rayfield.domain.helpers.PlatformIdentity

@Composable
fun WindowScope.RayFieldTitleBar(
    onClose: () -> Unit,
    onMaximize: () -> Unit,
    onMinimize: () -> Unit,
    isMaximized: Boolean = false,
    platform: PlatformIdentity.Platforms = PlatformIdentity().currentPlatform
) {
    CompositionLocalProvider(LocalLayoutDirection provides
        if (PlatformIdentity.Platforms.MacOS == platform) LayoutDirection.Rtl
        else LayoutDirection.Ltr
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .background(MaterialTheme.colorScheme.surface),
            verticalAlignment = Alignment.CenterVertically
        ) {
            WindowDraggableArea(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .padding(start = 16.dp),
            )

            Row(
                modifier = Modifier.fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                TitleBarButton(
                    icon = Icons.Default.Remove,
                    onClick = onMinimize,
                    hoverColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                )

                TitleBarButton(
                    icon = if (isMaximized) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                    onClick = onMaximize,
                    hoverColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                )

                TitleBarButton(
                    icon = Icons.Default.Close,
                    onClick = onClose,
                    hoverColor = MaterialTheme.colorScheme.errorContainer
                )
            }
        }
    }
}

@Composable
private fun TitleBarButton(
    icon: ImageVector,
    onClick: () -> Unit,
    hoverColor: Color,
    hoverIconColor: Color = MaterialTheme.colorScheme.onSurface
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(46.dp)
            .background(if (isHovered) hoverColor else Color.Transparent)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .hoverable(interactionSource),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = if (isHovered) hoverIconColor else MaterialTheme.colorScheme.onSurface
        )
    }
}