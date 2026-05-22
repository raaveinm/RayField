package com.raaveinm.rayfield.ui.fragments

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.raaveinm.rayfield.ui.theme.LocalDimensions
import io.github.neilyich.glassmorphism.BlurHolder
import io.github.neilyich.glassmorphism.blurredBackground
import io.github.neilyich.glassmorphism.rememberBlurHolder

//
// Created by Kirill "Raaveinm" on 5/11/26.
//

@Composable
fun BlurredDropDown(
    modifier: Modifier = Modifier,
    blurHolder: BlurHolder,
    items: List<String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var anchorWidth by remember { mutableIntStateOf(0) }

    Box(
        modifier = modifier
            .onGloballyPositioned { anchorWidth = it.size.width }
            .clip(shape = MaterialTheme.shapes.medium)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .blurredBackground(
                    blurHolder = blurHolder,
                    blurRadius = 96.dp,
                    tileMode = TileMode.Clamp
                )
                .clip(shape = MaterialTheme.shapes.medium)
        ) {
            DropDownMenuItem(
                text = selectedItem,
                isSelected = false,
                onClick = { expanded = true }
            )
        }

        if (expanded) {
            Popup(
                onDismissRequest = { expanded = false },
                properties = PopupProperties(focusable = true)
            ) {
                Column(
                    modifier = Modifier
                        .width(with(LocalDensity.current) { anchorWidth.toDp() })
                        .clip(shape = MaterialTheme.shapes.medium)
                        .blurredBackground(
                            blurHolder = blurHolder,
                            blurRadius = 96.dp,
                            tileMode = TileMode.Clamp
                        )
                ) {
                    items.forEach { item ->
                        DropDownMenuItem(
                            text = item,
                            isSelected = item == selectedItem,
                            onClick = {
                                onItemSelected(item)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DropDownMenuItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val dimen = LocalDimensions.current

    val conditionalBackground = if (isSelected)
        Modifier.background(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.64f))
    else Modifier

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .then(conditionalBackground)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(dimen.smallPadding)
        )
    }
}

@Preview
@Composable
fun DropDownPreview() {
    val blurHolder = rememberBlurHolder()
    BlurredDropDown(
        modifier = Modifier,
        blurHolder = blurHolder,
        items = List(4) { "Item $it" },
        selectedItem = "Item 2",
        onItemSelected = {}
    )
}