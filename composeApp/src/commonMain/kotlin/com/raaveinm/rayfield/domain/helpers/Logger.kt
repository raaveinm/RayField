package com.raaveinm.rayfield.domain.helpers

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object Logger {
    fun e(tag: String, message: String)
    fun w(tag: String, message: String)
    fun i(tag: String, message: String)
    fun d(tag: String, message: String)
    fun v(tag: String, message: String)
}