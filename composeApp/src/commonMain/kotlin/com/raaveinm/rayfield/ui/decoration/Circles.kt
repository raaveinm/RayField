package com.raaveinm.rayfield.ui.decoration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.zIndex

//
// Created by Kirill "Raaveinm" on 5/6/26.
//

@Composable
fun Circles(circles: List<Circle>) {
    Box(
        modifier = Modifier.background(MaterialTheme.colorScheme.background).zIndex(-1f).fillMaxSize().drawWithCache {
            onDrawBehind {
                circles.forEach { circle ->
                    val x =
                        if (circle.attachedEnd) size.width - circle.coordinates.first.toPx()
                        else circle.coordinates.first.toPx()
                    val y =
                        if (circle.attachedBottom) size.height - circle.coordinates.second.toPx()
                        else circle.coordinates.second.toPx()

                    drawCircle(
                        brush = SolidColor(circle.color),
                        radius = circle.radius.toPx(),
                        center = Offset(x, y)
                    )
                }
            }
        }
    )
}