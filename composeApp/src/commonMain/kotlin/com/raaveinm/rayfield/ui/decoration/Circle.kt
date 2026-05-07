package com.raaveinm.rayfield.ui.decoration

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp


//
// Created by Kirill "Raaveinm" on 5/6/26.
//

data class Circle(
    val coordinates: Pair<Dp, Dp>,
    val color: Color = Color.Unspecified,
    val radius: Dp,
    val attachedEnd: Boolean = false,
    val attachedBottom: Boolean = false
)
