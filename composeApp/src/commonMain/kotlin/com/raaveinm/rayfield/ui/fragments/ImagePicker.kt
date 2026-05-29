package com.raaveinm.rayfield.ui.fragments

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.raaveinm.rayfield.ui.adapters.AnyImage
import com.raaveinm.rayfield.ui.mock.flags

//
// Created by Kirill "Raaveinm" on 5/28/26.
//

@Composable
fun ImagePicker(
    modifier: Modifier = Modifier,
    onImageSelected: (anyImage: String) -> Unit,
    onCustomSelected: () -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(64.dp),
        modifier = modifier.height(300.dp),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(flags.filterNotNull()) { flag ->
            AnyImage(
                picture = flag,
                name = "sudo-pacman-suy",
                size = 64.dp,
                textBackground = MaterialTheme.colorScheme.surfaceVariant,
                text = MaterialTheme.colorScheme.onSurfaceVariant,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.clickable { onImageSelected(flag) }
            )
        }
        item {
            IconButton(
                modifier = Modifier.size(64.dp),
                onClick = { onCustomSelected() }
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "Add Custom",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }
    }
}
