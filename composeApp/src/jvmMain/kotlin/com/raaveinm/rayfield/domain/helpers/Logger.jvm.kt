package com.raaveinm.rayfield.domain.helpers

import java.util.logging.Level
import java.util.logging.Logger as JLogger

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING", "unused")
actual object Logger {
    private val logger: JLogger = JLogger.getLogger("RayField")

    actual fun e(tag: String, message: String) {
        logger.log(Level.SEVERE, "[$tag] $message")
    }

    actual fun w(tag: String, message: String) {
        logger.log(Level.WARNING, "[$tag] $message")
    }

    actual fun i(tag: String, message: String) {
        logger.log(Level.INFO, "[$tag] $message")
    }

    actual fun d(tag: String, message: String) {
        logger.log(Level.FINE, "[$tag] $message")
    }

    actual fun v(tag: String, message: String) {
        logger.log(Level.FINER, "[$tag] $message")
    }
}
