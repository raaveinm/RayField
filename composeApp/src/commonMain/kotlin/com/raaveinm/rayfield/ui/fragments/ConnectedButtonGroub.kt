package com.raaveinm.rayfield.ui.fragments

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.raaveinm.rayfield.ui.theme.LocalDimensions

//
// Created by Kirill "Raaveinm" on 5/6/26.
//

@Composable
fun ConnectedButtonGroup(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    val surfaceVariant = MaterialTheme.colorScheme.inverseSurface
    val onSurface = MaterialTheme.colorScheme.inverseOnSurface
    val secondary = MaterialTheme.colorScheme.secondary
    val onSecondary = MaterialTheme.colorScheme.onSecondary

    val dimen = LocalDimensions.current

    LazyRow {
        items(options.size) { index ->
            val option = options[index]
            val isSelected = option == selectedOption

            val color = if (isSelected) secondary else surfaceVariant
            val contentColor = if (isSelected) onSecondary else onSurface

            val (topStart, topEnd, bottomEnd, bottomStart) = when {
                isSelected -> listOf(CornerSize(100.dp), CornerSize(100.dp), CornerSize(100.dp), CornerSize(100.dp))
                index == 0 -> listOf(
                    MaterialTheme.shapes.medium.topStart,
                    MaterialTheme.shapes.small.topEnd,
                    MaterialTheme.shapes.small.bottomEnd,
                    MaterialTheme.shapes.medium.bottomStart
                )
                index == options.lastIndex -> listOf(
                    MaterialTheme.shapes.small.topStart,
                    MaterialTheme.shapes.medium.topEnd,
                    MaterialTheme.shapes.medium.bottomEnd,
                    MaterialTheme.shapes.small.bottomStart
                )
                else -> listOf(
                    MaterialTheme.shapes.small.topStart,
                    MaterialTheme.shapes.small.topEnd,
                    MaterialTheme.shapes.small.bottomEnd,
                    MaterialTheme.shapes.small.bottomStart
                )
            }

            val shape = RoundedCornerShape(topStart, topEnd, bottomEnd, bottomStart)

            Box(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .clip(shape)
                    .background(color)
                    .clickable { onOptionSelected(option) }
            ){
                Text(
                    text = options[index],
                    color = contentColor,
                    modifier = Modifier.padding(dimen.smallPadding)
                )
            }
        }
    }
}