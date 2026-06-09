package com.raaveinm.rayfield.domain.helpers

import org.jetbrains.skiko.ClipboardManager as SkikoClipboardManager

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object ClipboardHelper {
    private val clipboardManager = SkikoClipboardManager()

    actual fun setText(text: String) {
        clipboardManager.setText(text)
    }
}