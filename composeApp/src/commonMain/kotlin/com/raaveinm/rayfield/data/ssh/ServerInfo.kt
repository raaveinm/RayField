package com.raaveinm.rayfield.data.ssh

data class ServerInfo(
    val ip: String,
    val port: Int = 22,
    val sshPassword: String? = null,
    val username: String = "root",
    val serverName: String? = null,
    val iconUri: String? = null
)
