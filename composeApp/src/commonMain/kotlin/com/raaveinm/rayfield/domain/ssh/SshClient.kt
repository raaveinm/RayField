package com.raaveinm.rayfield.domain.ssh

import com.raaveinm.rayfield.data.ssh.CommandResult

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class SshClient() {
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