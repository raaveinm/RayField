package com.raaveinm.rayfield.ui.adapters

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

//
// Created by Kirill "Raaveinm" on 5/6/26.
//

@Composable
fun AnyImage(
    picture: Any?,
    name: String,
    size: Dp,
    textBackground: Color,
    text: Color,
) {
    val commonModifier = Modifier
        .size(size)
        .clip(CircleShape)

    when (picture) {
        null -> AnyImagePlaceholder(name, commonModifier, textBackground, text)
        is DrawableResource -> {
            Image(
                painter = painterResource(picture),
                contentDescription = "server_icon",
                modifier = commonModifier,
                contentScale = ContentScale.Crop
            )
        }
        else -> {
            SubcomposeAsyncImage(
                model = picture,
                contentDescription = "server_icon",
                modifier = commonModifier,
                contentScale = ContentScale.Crop
            ) {
                val state by painter.state.collectAsState()
                if (state is AsyncImagePainter.State.Success) {
                    Image(
                        painter = painter,
                        contentDescription = "server_icon",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    AnyImagePlaceholder(name, Modifier.fillMaxSize(), textBackground, text)
                }
            }
        }
    }
}

@Composable
private fun AnyImagePlaceholder(
    name: String,
    modifier: Modifier,
    textBackground: Color,
    text: Color,
) {
    Text(
        text = name.take(1).uppercase(),
        modifier = modifier
            .background(textBackground)
            .wrapContentHeight(Alignment.CenterVertically),
        style = MaterialTheme.typography.headlineLarge,
        textAlign = TextAlign.Center,
        color = text
    )
}
