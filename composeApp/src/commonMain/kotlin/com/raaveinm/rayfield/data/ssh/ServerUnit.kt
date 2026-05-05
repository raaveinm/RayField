package com.raaveinm.rayfield.data.ssh

data class ServerUnit(
    val serverId: String,
    val serverName: String? = null,
    val serverIp: String,
    val serverSshLogin: String,
    val serverSshPassword: String?,
    val serverSshPrivateKey: String?,
    val serverSshPort: Int,
)