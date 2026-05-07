package com.raaveinm.rayfield.ui.decoration

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

//
// Created by Kirill "Raaveinm" on 5/6/26.
//

@Composable
fun circlesJvm() = listOf(
    Circle(coordinates = Pair((-75).dp, (-50).dp), color = MaterialTheme.colorScheme.errorContainer, radius = 164.dp), // circle 5
    Circle(coordinates = Pair(400.dp, (-144).dp), color = MaterialTheme.colorScheme.errorContainer, radius = 240.dp, attachedEnd = true, attachedBottom = true), // circle 2
    Circle(coordinates = Pair(130.dp, 280.dp), color = MaterialTheme.colorScheme.primary.copy(alpha = .72f), radius = 64.dp, attachedEnd = true, attachedBottom = true), // circle 6
    Circle(coordinates = Pair(199.dp, 84.dp), color = MaterialTheme.colorScheme.primary.copy(alpha = .72f), radius = 96.dp, attachedEnd = true), // circle 4
    Circle(coordinates = Pair(279.dp, (-80).dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = .48f), radius = 280.dp), // circle 7
    Circle(coordinates = Pair((-10).dp, 100.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = .48f), radius = 200.dp, attachedEnd = false, attachedBottom = true), // circle 3
)