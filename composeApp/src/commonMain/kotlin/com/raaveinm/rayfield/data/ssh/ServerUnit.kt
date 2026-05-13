package com.raaveinm.rayfield.data.ssh

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "server_units")
data class ServerUnit(
    @PrimaryKey val serverId: String,
    val serverName: String? = null,
    val serverIp: String,
    val serverSshLogin: String,
    val serverSshPassword: String?,
    val serverSshPrivateKey: String?,
    val serverSshPort: Int,
    val iconLocation: String? = null
    ) {
    constructor(
        serverId: String,
        serverName: String?,
        serverIp: String,
        serverSshLogin: String,
        serverSshPassword: String?,
        serverSshPrivateKey: String?,
        serverSshPort: Int
    ) : this(serverId, serverName, serverIp, serverSshLogin, serverSshPassword, serverSshPrivateKey, serverSshPort, null)
}