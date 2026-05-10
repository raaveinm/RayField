package com.raaveinm.rayfield.ui.state

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.raaveinm.rayfield.data.database.ServerDao
import com.raaveinm.rayfield.data.ssh.ServerUnit
import com.raaveinm.rayfield.data.xray.types.ServerState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class MainScreenModel(
    serverDao: ServerDao
) : ScreenModel {

    val serverStates: StateFlow<List<ServerState>> = serverDao.getAllServerStates()
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val serverUnits: StateFlow<List<ServerUnit>> = serverDao.getAllServerUnits()
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getServerById(serverId: String): ServerUnit? = serverUnits.value.find { it.serverId == serverId }
}