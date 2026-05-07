package com.raaveinm.rayfield.ui.bars

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.raaveinm.rayfield.ui.theme.LocalDimensions
import io.github.neilyich.glassmorphism.BlurHolder
import io.github.neilyich.glassmorphism.blurredBackground
import io.github.neilyich.glassmorphism.rememberBlurHolder

@Composable
fun BottomBar(
    modifier: Modifier = Modifier,
    blurHolder: BlurHolder,
    onHomeNavigation: () -> Unit,
    onEditNavigation: () -> Unit,
    onSettingsNavigation: () -> Unit,
    onRawSshNavigation: () -> Unit,
    onAddNewServerNavigation: () -> Unit,
    selectedDestination: Int
) {
    val blurred = MaterialTheme.colorScheme.primary.copy(alpha = .64f)
    val secondaryBlurred = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = .64f)

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(Modifier
            .shadow(6.dp, RoundedCornerShape(50.dp))
            .clip(RoundedCornerShape(50.dp))
            .blurredBackground(
                blurHolder = blurHolder,
                blurRadius = 8.dp,
                tileMode = TileMode.Mirror)
            .background(color = blurred)
        ) {
            Row(Modifier.padding(LocalDimensions.current.sMediumPadding),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val modifier = Modifier.padding(end = LocalDimensions.current.smallPadding)
                ActionIconButton(
                    icon = Icons.Filled.Inbox,
                    contentDescription = "main_screen",
                    onClick = onHomeNavigation,
                    modifier = modifier,
                    color = if (selectedDestination == 0) secondaryBlurred else Color.Transparent
                )
                ActionIconButton(
                    icon = Icons.Outlined.Edit,
                    contentDescription = "edit_screen",
                    onClick = onEditNavigation,
                    modifier = modifier,
                    color = if (selectedDestination == 1) secondaryBlurred else Color.Transparent
                )
                ActionIconButton(
                    icon = Icons.Outlined.Settings,
                    contentDescription = "settings_screen",
                    onClick = onSettingsNavigation,
                    modifier = modifier,
                    color = if (selectedDestination == 2) secondaryBlurred else Color.Transparent
                )
                ActionIconButton(
                    icon = Icons.Outlined.SwapHoriz,
                    contentDescription = "raw_ssh_screen",
                    onClick = onRawSshNavigation,
                    modifier = Modifier,
                    color = if (selectedDestination == 3) secondaryBlurred else Color.Transparent
                )
            }
        }

        FloatingActionButton(
            onClick = onAddNewServerNavigation,
            modifier = Modifier
                .padding(start = LocalDimensions.current.extraLargeSize)
                .sizeIn(maxWidth = 56.dp, maxHeight = 56.dp),
            content = {
                Box(Modifier.fillMaxSize()
                    .blurredBackground(
                    blurHolder = blurHolder,
                    blurRadius = 8.dp,
                    tileMode = TileMode.Mirror),
                    contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "add_new_server",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        )
    }
}

@Composable private fun ActionIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        colors = IconButtonDefaults.iconButtonColors(containerColor = color),
        content = {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(24.dp)
            )
        }
    )
}

@Preview
@Composable
fun BottomBarPreview() {
    BottomBar(
        blurHolder = rememberBlurHolder(),
        onHomeNavigation = {},
        onEditNavigation = {},
        onSettingsNavigation = {},
        onRawSshNavigation = {},
        onAddNewServerNavigation = {},
        selectedDestination = 0
    )
}