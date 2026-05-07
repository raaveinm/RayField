package com.raaveinm.rayfield.data.xray.types

data class ServerState(
    var serverId: String, // Many - One with @ServerUnit
    var connectionName: String? = null,
    val serverAddress: String,
    val sharedLink: String,
    val iconLocation: Any? = null,
    val protocol: String,
    val jsonSettings: String,
)
