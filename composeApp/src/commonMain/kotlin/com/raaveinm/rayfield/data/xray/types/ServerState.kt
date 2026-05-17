package com.raaveinm.rayfield.data.xray.types

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Ignore
import androidx.room3.PrimaryKey
import com.raaveinm.rayfield.data.ssh.ServerUnit

@Entity(
    tableName = "server_states",
    foreignKeys = [
        ForeignKey(
            entity = ServerUnit::class,
            parentColumns = ["serverId"],
            childColumns = ["serverId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [androidx.room3.Index(value = ["serverId"])]
)
data class ServerState(
    @PrimaryKey val configId: String,
    val serverId: String, // Many - One with @ServerUnit
    var connectionName: String? = null,
    val serverAddress: String,
    val uuid: XrayKeyPair? = null,
    val sharedLink: String,
    @Ignore val iconLocation: String? = null,
    val protocol: String
) {

    // Secondary constructor for Room
    constructor(
        configId: String,
        serverId: String,
        connectionName: String?,
        serverAddress: String,
        uuid: XrayKeyPair?,
        sharedLink: String,
        protocol: String
    ) : this(configId, serverId, connectionName, serverAddress, uuid, sharedLink, null, protocol)
}