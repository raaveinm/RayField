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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import rayfield.composeapp.generated.resources.*

//
// Created by Kirill "Raaveinm" on 5/6/26.
//

@Composable
fun AnyImage(
    picture: String?,
    name: String,
    size: Dp,
    textBackground: Color,
    text: Color,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
) {
    val commonModifier = modifier
        .size(size)
        .clip(shape)

    when {
        picture == null -> AnyImagePlaceholder(name, commonModifier, textBackground, text)
        picture.startsWith("res:") -> {
            val resName = picture.removePrefix("res:")
            val drawableResource = getDrawableByName(resName)

            if (drawableResource != null) {
                Image(
                    painter = painterResource(drawableResource),
                    contentDescription = "server_icon",
                    modifier = commonModifier,
                    contentScale = ContentScale.Crop
                )
            } else AnyImagePlaceholder(name, commonModifier, textBackground, text)
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

private fun getDrawableByName(name: String): DrawableResource? {
    return when (name) {
        "flag_armenia" -> Res.drawable.flag_armenia
        "flag_austria" -> Res.drawable.flag_austria
        "flag_estonia" -> Res.drawable.flag_estonia
        "flag_finland" -> Res.drawable.flag_finland
        "flag_france" -> Res.drawable.flag_france
        "flag_georgia" -> Res.drawable.flag_georgia
        "flag_germany" -> Res.drawable.flag_germany
        "flag_greece" -> Res.drawable.flag_greece
        "flag_italy" -> Res.drawable.flag_italy
        "flag_latvia" -> Res.drawable.flag_latvia
        "flag_norway" -> Res.drawable.flag_norway
        "flag_poland" -> Res.drawable.flag_poland
        "flag_sweden" -> Res.drawable.flag_sweden
        "flag_uk" -> Res.drawable.flag_uk
        "flag_ukraine" -> Res.drawable.flag_ukraine
        "flag_united_states" -> Res.drawable.flag_united_states
        "flag_pride" -> Res.drawable.flag_pride
        "flag_netherlands" -> Res.drawable.flag_netherlands
        else -> null
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
