package com.raaveinm.rayfield.data.xray.types

data class CommandResult(
    val exitCode: Int,
    val stdout: String,
    val error: String? = null
)