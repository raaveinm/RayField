package com.raaveinm.rayfield.data.xray.types

data class ServerState(
    var serverId: String, // Many - One with @ServerItem
    var serverName: String? = null,
    val serverAddress: String,
    val sharedLink: String,
    val iconLocation: String? = null,
    val protocol: String
)
