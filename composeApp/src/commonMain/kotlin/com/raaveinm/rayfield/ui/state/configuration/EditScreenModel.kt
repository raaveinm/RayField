package com.raaveinm.rayfield.ui.state.configuration

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.raaveinm.rayfield.data.database.ServerDao
import com.raaveinm.rayfield.domain.xray.CypherService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

//
// Created by Kirill "Raaveinm" on 5/11/26.
//

class EditScreenModel(
    val serverDao: ServerDao,
    private val cypherService: CypherService,
    private val initialServerId: String? = null
) : ScreenModel {
    val connectionNameState = TextFieldState()
    val serverAddressState = TextFieldState()
    val portState = TextFieldState()
    val listenState = TextFieldState()
    val shadowsocksPasswordState = TextFieldState()
    val vmessAlterIdState = TextFieldState()
    val trojanPasswordState = TextFieldState()
    val fallbackDestState = TextFieldState()

    private val _state = MutableStateFlow(
        EditDraftState(
            serverId = initialServerId ?: "",
            isLoading = initialServerId != null
        )
    )
    val state = _state.asStateFlow()

    var uuid = MutableStateFlow("")
    var privateKey = MutableStateFlow("")
    var publicKey = MutableStateFlow("")
    var shortId = MutableStateFlow("")

    init {
        screenModelScope.launch {
            if (initialServerId != null) {
                val server = serverDao.getServerUnitById(initialServerId)
                if (server != null) {
                    connectionNameState.setTextAndPlaceCursorAtEnd(server.serverName ?: "")
                    serverAddressState.setTextAndPlaceCursorAtEnd(server.serverIp)
                    
                    _state.update { it.copy(
                        serverId = server.serverId,
                        connectionName = server.serverName ?: "",
                        serverAddress = server.serverIp,
                        isLoading = false
                    ) }
                    // TODO: Parse server.serverJsonConfig if present to fill other fields
                    
                    // For now, let's use the current state values to initialize TextFieldStates
                    val s = _state.value
                    portState.setTextAndPlaceCursorAtEnd(s.inbound.inboundPort.toString())
                    listenState.setTextAndPlaceCursorAtEnd(s.inbound.inboundListen)
                    shadowsocksPasswordState.setTextAndPlaceCursorAtEnd(s.inbound.shadowsocksPassword ?: "")
                    vmessAlterIdState.setTextAndPlaceCursorAtEnd(s.inbound.vmessAlterId.toString())
                    trojanPasswordState.setTextAndPlaceCursorAtEnd(s.inbound.trojanPassword ?: "")
                    fallbackDestState.setTextAndPlaceCursorAtEnd(s.inbound.fallbackDest.toString())
                }
else {
                    _state.update { it.copy(isLoading = false) }
                }
            } else {
                _state.update { it.copy(isLoading = false) }
                
                // Initialize with default values if new
                val s = _state.value
                connectionNameState.setTextAndPlaceCursorAtEnd(s.connectionName)
                serverAddressState.setTextAndPlaceCursorAtEnd(s.serverAddress)
                portState.setTextAndPlaceCursorAtEnd(s.inbound.inboundPort.toString())
                listenState.setTextAndPlaceCursorAtEnd(s.inbound.inboundListen)
                shadowsocksPasswordState.setTextAndPlaceCursorAtEnd(s.inbound.shadowsocksPassword ?: "")
                vmessAlterIdState.setTextAndPlaceCursorAtEnd(s.inbound.vmessAlterId.toString())
                trojanPasswordState.setTextAndPlaceCursorAtEnd(s.inbound.trojanPassword ?: "")
                fallbackDestState.setTextAndPlaceCursorAtEnd(s.inbound.fallbackDest.toString())
            }
        }
    }

    fun processIntent(intent: EditIntent) {
        when (intent) {
            is EditIntent.UpdateName -> _state.update { it.copy(connectionName = intent.name) }
            is EditIntent.UpdateInboundPort -> _state.update { it.copy(inbound = it.inbound.copy(inboundPort = intent.port)) }
            is EditIntent.UpdateInboundListen -> _state.update { it.copy(inbound = it.inbound.copy(inboundListen = intent.listen)) }
            is EditIntent.UpdateInboundProtocol -> _state.update { it.copy(inbound = it.inbound.copy(inboundProtocol = intent.protocol)) }
            is EditIntent.UpdateInboundId -> _state.update { it.copy(inbound = it.inbound.copy(inboundId = intent.id)) }
            is EditIntent.UpdateShadowsocksPassword -> _state.update { it.copy(inbound = it.inbound.copy(shadowsocksPassword = intent.password)) }
            is EditIntent.UpdateShadowsocksMethod -> _state.update { it.copy(inbound = it.inbound.copy(shadowsocksMethod = intent.method)) }
            is EditIntent.UpdateVmessAlterId -> _state.update { it.copy(inbound = it.inbound.copy(vmessAlterId = intent.alterId)) }
            is EditIntent.UpdateTrojanPassword -> _state.update { it.copy(inbound = it.inbound.copy(trojanPassword = intent.password)) }
            is EditIntent.UpdateFallbackDest -> _state.update { it.copy(inbound = it.inbound.copy(fallbackDest = intent.port)) }
            
            is EditIntent.UpdateInbound -> _state.update { it.copy(inbound = intent.inbound) }
            is EditIntent.UpdateStream -> _state.update { it.copy(stream = intent.stream) }
            is EditIntent.UpdateOutbound -> _state.update { it.copy(outbound = intent.outbound) }
            is EditIntent.UpdatePro -> _state.update { it.copy(pro = intent.pro) }
            
            EditIntent.Save -> { screenModelScope.launch { saveServer() } }
            EditIntent.Cancel -> { /* Handle cancel */ }
        }
    }

    suspend fun saveServer() {
        // Sync TextFieldStates back to state before saving
        _state.update { it.copy(
            connectionName = connectionNameState.text.toString(),
            serverAddress = serverAddressState.text.toString(),
            inbound = it.inbound.copy(
                inboundPort = portState.text.toString().toIntOrNull() ?: 0,
                inboundListen = listenState.text.toString(),
                shadowsocksPassword = shadowsocksPasswordState.text.toString(),
                vmessAlterId = vmessAlterIdState.text.toString().toIntOrNull() ?: 0,
                trojanPassword = trojanPasswordState.text.toString(),
                fallbackDest = fallbackDestState.text.toString().toIntOrNull() ?: 0
            )
        ) }
        val configuration = _state.value
    }

    fun generateUuid() {
        val newUuid = cypherService.generateUuid()
        uuid.value = newUuid
        _state.update { it.copy(inbound = it.inbound.copy(inboundId = newUuid)) }
    }

    fun generateRealityKeys() {
        val keyPair = cypherService.generateKeyPair()
        privateKey.value = keyPair.privateKey
        publicKey.value = keyPair.publicKey
        _state.update {
            it.copy(
                stream = it.stream.copy(
                    realityKeyPair = keyPair,
                    realityPrivateKey = keyPair.privateKey,
                    realityPublicKey = keyPair.publicKey
                )
            )
        }
    }

    fun generateShortId() {
        val newShortId = cypherService.generateShortId()
        shortId.value = newShortId
        _state.update { it.copy(stream = it.stream.copy(realityShortId = newShortId)) }
    }
}
