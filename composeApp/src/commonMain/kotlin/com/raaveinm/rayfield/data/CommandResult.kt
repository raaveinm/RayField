package com.raaveinm.rayfield.data

data class CommandResult(
    val exitCode: Int,
    val stdout: String,
    val error: String? = null
)