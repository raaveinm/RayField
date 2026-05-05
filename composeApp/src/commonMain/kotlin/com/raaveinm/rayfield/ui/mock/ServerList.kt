package com.raaveinm.rayfield.ui.mock

import com.raaveinm.rayfield.data.xray.types.ServerState
import com.raaveinm.rayfield.data.ssh.ServerUnit
import kotlin.random.Random

//
// Created by Kirill "Raaveinm" on 5/3/26.
//

val mockList = List(32) { index ->
    val frst = Random.nextInt(10,255)
    val scnd = Random.nextInt(10, 255)
    val thrd = Random.nextInt(10, 255)
    val frth = "$frst.$scnd.$thrd"
    ServerState(
        serverId = index.toString(),
        connectionName = when(index) {
            2 -> null
            6 -> null
            8 -> null
            12 -> null
            15 -> "frankfurt"
            18 -> null
            19 -> "london"
            22 -> "amnezia_wg<3"
            24 -> "askdv"
            26 -> "kde_foundation"
            27 -> "aezakmi"
            30 -> "very_long_name_very_long_name_very_long_name_very_long_name_very_long_name_very_" +
                    "long_name_very_long_name_very_long_name_very_long_name_very_long_name_very_" +
                    "long_name_very_long_name_very_long_name_very_long_name_very_long_name_"
            else -> "server_name"
        },
        serverAddress = "$frst.$scnd.$thrd.$frth",
        sharedLink = "vless://fff73709-bide-120b-a853-2b9s3feas2rr" +
                "@$frst.$scnd.$thrd.$frth:443?type=tcp&encryption=none",
        protocol = "vless",
        jsonSettings = ""
    )
}

val mockServers = List(9) { index ->
    ServerUnit(
        serverId = index.toString(),
        serverSshLogin = "vps_$index",
        serverSshPassword = List(8) { Random.nextInt(0, 10) }.joinToString(""),
        serverSshPrivateKey = null,
        serverSshPort = 22,
        serverName = when (index) {
            1 -> "frankfurt"
            4 -> "london"
            5 -> "stockholm"
            6 -> "berlin"
            else -> null
        },
        serverIp = "${Random.nextInt(10,255)}." +
                "${Random.nextInt(10, 255)}." +
                "${Random.nextInt(10, 255)}.$index"
    )
}
