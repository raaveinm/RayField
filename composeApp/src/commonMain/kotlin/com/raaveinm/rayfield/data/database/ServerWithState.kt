package com.raaveinm.rayfield.data.database

import androidx.room3.Embedded
import androidx.room3.Relation
import com.raaveinm.rayfield.data.ssh.ServerUnit
import com.raaveinm.rayfield.data.xray.types.ServerState

//
// Created by Kirill "Raaveinm" on 5/7/26.
//

data class ServerWithState(
    @Embedded val server: ServerUnit,
    @Relation(
        parentColumn = "serverId",
        entityColumn = "serverId"
    )
    val states: List<ServerState>
)
