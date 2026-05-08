package com.raaveinm.rayfield.ui.mock

import com.raaveinm.rayfield.data.xray.types.ServerState
import com.raaveinm.rayfield.data.ssh.ServerUnit
import kotlin.random.Random

//
// Created by Kirill "Raaveinm" on 5/3/26.
//

val flags = listOf(
    "res:flag_armenia",
    "res:flag_austria",
    "res:flag_estonia",
    "res:flag_finland",
    "res:flag_france",
    "res:flag_georgia",
    "res:flag_germany",
    "res:flag_greece",
    "res:flag_italy",
    "res:flag_latvia",
    "res:flag_norway",
    "res:flag_poland",
    "res:flag_sweden",
    "res:flag_uk",
    "res:flag_ukraine",
    "res:flag_united_states",
    "https://d.furaffinity.net/art/wildering/1669843122/1669843122.wildering_space-boy-preview.jpg",
    "https://d.furaffinity.net/art/famir/1683234047/1683234047.famir_illustration5%D1%84%D0%B0.png",
    "https://d.furaffinity.net/art/rotarr/1628799832/1628799832.rotarr_2108_10.jpg",
    "https://d.furaffinity.net/art/weir/1676311386/1676311386.weir_weir_avali_by_veepaws_dfp62q4-pre.jpg",
    "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/1808500/cb49dfbcd7175c86e297b35ffc54cf779708f0ae/ss_cb49dfbcd7175c86e297b35ffc54cf779708f0ae.1920x1080.jpg",
    "",
    "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/1808500/1763664f3ea80080867eafa751685b7feec950c8/ss_1763664f3ea80080867eafa751685b7feec950c8.1920x1080.jpg",
    "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/620980/3067664ee110fed550f5c3a1b74340cabd7d53f0/header.jpg",
    "https://avatars.fastly.steamstatic.com/709eb91ba26b8b73461a45649fee57b353b91f67_full.jpg",
    null,null,null,null,null,null,null,
    "res:flag_armenia",
    "res:flag_austria",
    "res:flag_estonia",
    "res:flag_finland",
    "res:flag_france",
    "res:flag_georgia",
    "res:flag_germany",
    "res:flag_greece",
    "res:flag_italy",
    "res:flag_latvia",
    "res:flag_norway",
    "res:flag_poland",
    "res:flag_sweden",
    "res:flag_uk",
    "res:flag_ukraine",
    "res:flag_united_states",
)

val mockList = List(32) { index ->
    val frst = Random.nextInt(10, 255)
    val scnd = Random.nextInt(10, 255)
    val thrd = Random.nextInt(10, 255)
    val frth = "$frst.$scnd.$thrd"
    ServerState(
        configId = index.toString(),
        serverId = index.toString(),
        connectionName = when (index) {
            2 -> "server_name"
            8 -> "server_name"
            12 -> "server_name"
            15 -> "frankfurt"
            19 -> "london"
            22 -> "amnezia_wg<3"
            24 -> "askdv"
            26 -> "kde_foundation"
            27 -> "aezakmi"
            30 -> "very_long_name_very_long_name_very_long_name_very_long_name_very_long_name_very_" +
                    "long_name_very_long_name_very_long_name_very_long_name_very_long_name_very_" +
                    "long_name_very_long_name_very_long_name_very_long_name_very_long_name_"
            else -> null
        },
        serverAddress = "$frst.$scnd.$thrd.$frth",
        sharedLink = "vless://fff73709-bide-120b-a853-2b9s3feas2rr" +
                "@$frst.$scnd.$thrd.$frth:443?type=tcp&encryption=none",
        protocol = "vless",
        jsonSettings = "",
        iconLocation = when(index){
            15 -> "res:flag_germany"
            19 -> "res:flag_uk"
            27 -> "res:flag_poland"
            else -> flags.random()
        }
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
                "${Random.nextInt(10, 255)}.$index",
        iconLocation = when(index){
            0 -> "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/1808500/cb49dfbcd7175c86e297b35ffc54cf779708f0ae/ss_cb49dfbcd7175c86e297b35ffc54cf779708f0ae.1920x1080.jpg"
            1 -> "res:flag_germany"
            3 -> "https://d.furaffinity.net/art/wildering/1669843122/1669843122.wildering_space-boy-preview.jpg"
            4 -> "res:flag_uk"
            5 -> "res:flag_sweden"
            6 -> "res:flag_germany"
            else -> flags.random()
        }
    )
}
