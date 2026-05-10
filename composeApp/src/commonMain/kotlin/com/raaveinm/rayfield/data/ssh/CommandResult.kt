package com.raaveinm.rayfield.data.ssh

data class CommandResult(
    val exitCode: Int,
    val stdout: String,
    val error: String? = null
)