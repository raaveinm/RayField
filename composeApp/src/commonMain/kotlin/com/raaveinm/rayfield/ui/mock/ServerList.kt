package com.raaveinm.rayfield.ui.mock

import com.raaveinm.rayfield.data.xray.types.ServerState
import com.raaveinm.rayfield.data.ssh.ServerUnit
import rayfield.composeapp.generated.resources.Res
import rayfield.composeapp.generated.resources.flag_armenia
import rayfield.composeapp.generated.resources.flag_austria
import rayfield.composeapp.generated.resources.flag_estonia
import rayfield.composeapp.generated.resources.flag_finland
import rayfield.composeapp.generated.resources.flag_france
import rayfield.composeapp.generated.resources.flag_georgia
import rayfield.composeapp.generated.resources.flag_germany
import rayfield.composeapp.generated.resources.flag_greece
import rayfield.composeapp.generated.resources.flag_italy
import rayfield.composeapp.generated.resources.flag_latvia
import rayfield.composeapp.generated.resources.flag_norway
import rayfield.composeapp.generated.resources.flag_poland
import rayfield.composeapp.generated.resources.flag_sweden
import rayfield.composeapp.generated.resources.flag_uk
import rayfield.composeapp.generated.resources.flag_ukraine
import rayfield.composeapp.generated.resources.flag_united_states
import kotlin.random.Random

//
// Created by Kirill "Raaveinm" on 5/3/26.
//

val flags = listOf(
    Res.drawable.flag_armenia,
    Res.drawable.flag_austria,
    Res.drawable.flag_estonia,
    Res.drawable.flag_finland,
    Res.drawable.flag_france,
    Res.drawable.flag_georgia,
    Res.drawable.flag_germany,
    Res.drawable.flag_greece,
    Res.drawable.flag_italy,
    Res.drawable.flag_latvia,
    Res.drawable.flag_norway,
    Res.drawable.flag_poland,
    Res.drawable.flag_sweden,
    Res.drawable.flag_uk,
    Res.drawable.flag_ukraine,
    Res.drawable.flag_united_states,
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
    Res.drawable.flag_armenia,
    Res.drawable.flag_austria,
    Res.drawable.flag_estonia,
    Res.drawable.flag_finland,
    Res.drawable.flag_france,
    Res.drawable.flag_georgia,
    Res.drawable.flag_germany,
    Res.drawable.flag_greece,
    Res.drawable.flag_italy,
    Res.drawable.flag_latvia,
    Res.drawable.flag_norway,
    Res.drawable.flag_poland,
    Res.drawable.flag_sweden,
    Res.drawable.flag_uk,
    Res.drawable.flag_ukraine,
    Res.drawable.flag_united_states,
)

val mockList = List(32) { index ->
    val frst = Random.nextInt(10, 255)
    val scnd = Random.nextInt(10, 255)
    val thrd = Random.nextInt(10, 255)
    val frth = "$frst.$scnd.$thrd"
    ServerState(
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
            15 -> Res.drawable.flag_germany
            19 -> Res.drawable.flag_uk
            27 -> Res.drawable.flag_poland
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
            1 -> Res.drawable.flag_germany
            3 -> "https://d.furaffinity.net/art/wildering/1669843122/1669843122.wildering_space-boy-preview.jpg"
            4 -> Res.drawable.flag_uk
            5 -> Res.drawable.flag_sweden
            6 -> Res.drawable.flag_germany
            else -> flags.random()
        }
    )
}
