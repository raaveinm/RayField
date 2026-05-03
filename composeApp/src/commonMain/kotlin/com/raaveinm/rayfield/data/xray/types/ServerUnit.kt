package com.raaveinm.rayfield.data.xray.types

//
// Created by Kirill "Raaveinm" on 5/3/26.
//

data class ServerUnit(
    val serverId: String,
    val serverSshLogin: String,
    val serverSshPassword: String?,
    val serverSshPrivateKey: String?,
    val serverSshPort: Int,
)
