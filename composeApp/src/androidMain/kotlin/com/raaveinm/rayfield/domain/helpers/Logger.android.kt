package com.raaveinm.rayfield.domain.helpers

import android.util.Log

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING", "unused")
actual object Logger {
    actual fun e(tag: String, message: String) { Log.e(tag, message) }
    actual fun w(tag: String, message: String) { Log.w(tag, message) }
    actual fun i(tag: String, message: String) { Log.i(tag, message) }
    actual fun d(tag: String, message: String) { Log.d(tag, message) }
    actual fun v(tag: String, message: String) { Log.v(tag, message) }
}