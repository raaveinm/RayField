package com.raaveinm.rayfield.ui.state.configuration

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.raaveinm.rayfield.data.database.ServerDao
import com.raaveinm.rayfield.data.ssh.ServerUnit
import com.raaveinm.rayfield.ui.mock.flags
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

//
// Created by Kirill "Raaveinm" on 5/7/26.
//

class SshScreenModel(
    private val serverDao: ServerDao,
    private val initialServerId: String? = null
) : ScreenModel {
    private val _state = MutableStateFlow(
        SshDraftState(
            serverId = initialServerId ?: "",
            isLoading = initialServerId != null
        )
    )
    val state = _state.asStateFlow()

    ///////////////////////////////////////////////
    // Intent handlers
    ///////////////////////////////////////////////

    init {
        screenModelScope.launch {
            if (initialServerId != null) {
                val server = serverDao.getServerUnitById(initialServerId)

                if (server == null) {
                    _state.value = SshDraftState(
                        serverId = initialServerId,
                        isLoading = false
                    )
                } else {
                    _state.value = SshDraftState(
                        serverId = server.serverId,
                        name = server.serverName ?: "",
                        ip = server.serverIp,
                        login = server.serverSshLogin,
                        password = server.serverSshPassword ?: "",
                        port = server.serverSshPort.toString(),
                        pathToPkey = server.serverSshPrivateKey ?: "",
                        isLoading = false
                    )
                }
            } else {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun processIntent(intent: SshIntent) {
        when (intent) {
            is SshIntent.UpdateName -> _state.update { it.copy(name = intent.name) }
            is SshIntent.UpdateIp -> _state.update { it.copy(ip = intent.ip) }
            is SshIntent.UpdateLogin -> _state.update { it.copy(login = intent.login) }
            is SshIntent.UpdatePassword -> _state.update { it.copy(password = intent.password) }
            is SshIntent.UpdatePathToPkey -> _state.update { it.copy(pathToPkey = intent.pathToPkey) }
            is SshIntent.UpdatePort -> _state.update { it -> it.copy(port = intent.port.filter { it.isDigit() }) }
            SshIntent.Save -> saveServer()
        }
    }

    private fun saveServer() {
        val currentState = _state.value

        if (currentState.ip.isBlank()) return
        if (currentState.login.isBlank()) return
        if (currentState.port.isBlank()) return

        screenModelScope.launch {
            val existingServer = if (currentState.serverId.isNotBlank()) {
                serverDao.getServerUnitById(currentState.serverId)
            } else null

            val serverUnit = ServerUnit(
                serverId = currentState.serverId.takeIf { it.isNotBlank() } ?: "server_${kotlin.random.Random.nextInt(10000)}",
                serverName = currentState.name.takeIf { it.isNotBlank() },
                serverIp = currentState.ip,
                serverSshLogin = currentState.login,
                serverSshPassword = currentState.password,
                serverSshPort = currentState.port.toIntOrNull() ?: 22,
                serverSshPrivateKey = currentState.pathToPkey,
                serverJsonConfig = existingServer?.serverJsonConfig,
                iconLocation = existingServer?.iconLocation ?: flags.random()
            )

            serverDao.insertServerUnit(serverUnit)
            _state.update { it.copy(isSaved = true, serverId = serverUnit.serverId) }
        }
    }
}
