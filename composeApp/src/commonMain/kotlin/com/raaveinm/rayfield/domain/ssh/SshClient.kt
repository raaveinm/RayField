package com.raaveinm.rayfield.domain.ssh

import com.raaveinm.rayfield.data.xray.types.CommandResult

interface SshClient {
    suspend fun connect (
        host: String,
        port: Int,
        username: String,
        password: String? = null,
        privateKey: ByteArray? = null,
    ) : Boolean

    suspend fun execute(command: String) : CommandResult
    suspend fun disconnect()
}