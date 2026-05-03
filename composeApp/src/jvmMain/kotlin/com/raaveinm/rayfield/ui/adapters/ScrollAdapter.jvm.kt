package com.raaveinm.rayfield.ui.adapters

import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
actual fun Modifier.horizontalMouseScroll(state: LazyGridState): Modifier =
    this.onPointerEvent(PointerEventType.Scroll) {
    val delta = it.changes.first().scrollDelta.y
    if (delta != 0f) {
        CoroutineScope(Dispatchers.Main).launch {
            state.scrollBy(delta * 100f)
        }
    }
}
