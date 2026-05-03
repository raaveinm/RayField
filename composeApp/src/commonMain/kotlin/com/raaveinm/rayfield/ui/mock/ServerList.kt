package com.raaveinm.rayfield.ui.mock

import com.raaveinm.rayfield.data.xray.types.ServerState

//
// Created by Kirill "Raaveinm" on 5/3/26.
//

val mockServers = {
    List(18) { index ->
        ServerState(
            serverId = index.toString(),
            serverName = when(index) {
                2 -> null
                6 -> null
                8 -> null
                12 -> null
                15 -> "frankfurt"
                else -> "server_name"
            },
            serverAddress = "192.168.123.123:443",
            sharedLink = "vless://fff73709-bide-120b-a853-2b9s3feas2rr" +
                    "@192.168.123.123:443?type=tcp&encryption=none",
            protocol = "vless"
        )
    }
}