package com.raaveinm.rayfield.domain

import com.raaveinm.rayfield.data.CommandResult

//
// Created by Kirill "Raaveinm" on 4/29/26.
//

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