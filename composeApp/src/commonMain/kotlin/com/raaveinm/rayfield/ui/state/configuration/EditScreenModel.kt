package com.raaveinm.rayfield.ui.state.configuration

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.raaveinm.rayfield.data.database.ServerDao
import com.raaveinm.rayfield.data.xray.Configurations
import com.raaveinm.rayfield.data.xray.XrayConfig
import com.raaveinm.rayfield.domain.xray.CypherService
import com.raaveinm.rayfield.domain.xray.XrayConfigBuilder
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
    val shadowsocksEmailState = TextFieldState()
    val vmessAlterIdState = TextFieldState()
    val trojanPasswordState = TextFieldState()
    val fallbackDestState = TextFieldState()
    val tlsServerNameState = TextFieldState()
    val tlsFingerprintState = TextFieldState() // TODO("Dropdown with supported fingerprints")
    val realityCustomTargetState = TextFieldState()
    private val _isCustomTarget = MutableStateFlow(false)
    val isCustomTarget = _isCustomTarget.asStateFlow()

    fun setCustomTargetMode(isCustom: Boolean) {
        _isCustomTarget.value = isCustom
    }

    val realityDestState = TextFieldState()
    val realityServerNamesState = TextFieldState()
    val realityPrivateKeyState = TextFieldState()
    val realityPublicKeyState = TextFieldState()
    val realityShortIdsState = TextFieldState()

    val xhttpPathState = TextFieldState()

    val outboundTagState = TextFieldState("proxy")
    val outboundAddressState = TextFieldState()
    val outboundPortState = TextFieldState()
    val outboundIdState = TextFieldState()
    val outboundPasswordState = TextFieldState()

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
                    shadowsocksEmailState.setTextAndPlaceCursorAtEnd(s.inbound.shadowsocksEmail)
                    vmessAlterIdState.setTextAndPlaceCursorAtEnd(s.inbound.vmessAlterId.toString())
                    trojanPasswordState.setTextAndPlaceCursorAtEnd(s.inbound.trojanPassword ?: "")
                    fallbackDestState.setTextAndPlaceCursorAtEnd(s.inbound.fallbackDest.toString())
                } else { _state.update { it.copy(isLoading = false) } }
            } else {
                _state.update { it.copy(isLoading = false) }

                val s = _state.value
                connectionNameState.setTextAndPlaceCursorAtEnd(s.connectionName)
                serverAddressState.setTextAndPlaceCursorAtEnd(s.serverAddress)
                portState.setTextAndPlaceCursorAtEnd(s.inbound.inboundPort.toString())
                listenState.setTextAndPlaceCursorAtEnd(s.inbound.inboundListen)
                shadowsocksPasswordState.setTextAndPlaceCursorAtEnd(s.inbound.shadowsocksPassword ?: "")
                shadowsocksEmailState.setTextAndPlaceCursorAtEnd(s.inbound.shadowsocksEmail)
                vmessAlterIdState.setTextAndPlaceCursorAtEnd(s.inbound.vmessAlterId.toString())
                trojanPasswordState.setTextAndPlaceCursorAtEnd(s.inbound.trojanPassword ?: "")
                fallbackDestState.setTextAndPlaceCursorAtEnd(s.inbound.fallbackDest.toString())
                val stream = s.stream
                tlsServerNameState.setTextAndPlaceCursorAtEnd(stream.tlsSettings?.serverName ?: "")
                realityDestState.setTextAndPlaceCursorAtEnd(stream.realitySettings?.dest ?: "")
                realityCustomTargetState.setTextAndPlaceCursorAtEnd(stream.realitySettings?.customTarget ?: "")
                _isCustomTarget.value = !stream.realitySettings?.customTarget.isNullOrBlank()

                realityServerNamesState.setTextAndPlaceCursorAtEnd(stream.realitySettings?.serverNames?.joinToString(", ") ?: "")
                realityPrivateKeyState.setTextAndPlaceCursorAtEnd(stream.realitySettings?.privateKey ?: "")
                realityPublicKeyState.setTextAndPlaceCursorAtEnd(stream.realitySettings?.password ?: "")
                realityShortIdsState.setTextAndPlaceCursorAtEnd(stream.realitySettings?.shortIds?.joinToString(", ") ?: "")
                xhttpPathState.setTextAndPlaceCursorAtEnd(stream.xhttpSettings?.path ?: "")

                val outbound = s.outbound
                outboundTagState.setTextAndPlaceCursorAtEnd(outbound.tag ?: "proxy")

                when (outbound.protocol) {
                    Configurations.protocol.VLESS -> {
                        val vlessSettings = outbound.settings as? XrayConfig.VlessOutboundSettings
                        val firstVlessServer = vlessSettings?.vnext?.firstOrNull()
                        if (firstVlessServer != null) {
                            outboundAddressState.setTextAndPlaceCursorAtEnd(firstVlessServer.address)
                            outboundPortState.setTextAndPlaceCursorAtEnd(firstVlessServer.port.toString())
                            outboundIdState.setTextAndPlaceCursorAtEnd(firstVlessServer.users.firstOrNull()?.id ?: "")
                        }
                    }
                    Configurations.protocol.SHADOWSOCKS -> {
                        val ssSettings = outbound.settings as? XrayConfig.ShadowsocksOutboundSettings
                        val firstSsServer = ssSettings?.servers?.firstOrNull()
                        if (firstSsServer != null) {
                            outboundAddressState.setTextAndPlaceCursorAtEnd(firstSsServer.address)
                            outboundPortState.setTextAndPlaceCursorAtEnd(firstSsServer.port.toString())
                            outboundPasswordState.setTextAndPlaceCursorAtEnd(firstSsServer.password)
                        }
                    }
                    else -> {}
                }
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
            is EditIntent.UpdateShadowsocksNetwork -> _state.update { it.copy(inbound = it.inbound.copy(shadowsocksNetwork = intent.network)) }
            is EditIntent.UpdateShadowsocksEmail -> _state.update { it.copy(inbound = it.inbound.copy(shadowsocksEmail = intent.email)) }
            is EditIntent.UpdateShadowsocksUsers -> _state.update { it.copy(inbound = it.inbound.copy(shadowsocksUsers = intent.users)) }
            is EditIntent.UpdateVmessAlterId -> _state.update { it.copy(inbound = it.inbound.copy(vmessAlterId = intent.alterId)) }
            is EditIntent.UpdateTrojanPassword -> _state.update { it.copy(inbound = it.inbound.copy(trojanPassword = intent.password)) }
            is EditIntent.UpdateFallbackDest -> _state.update { it.copy(inbound = it.inbound.copy(fallbackDest = intent.port)) }
            
            is EditIntent.UpdateOutboundProtocol -> _state.update { it.copy(outbound = it.outbound.copy(protocol = intent.protocol)) }
            is EditIntent.UpdateOutboundShadowsocksMethod -> _state.update { it.copy(outbound = it.outbound.copy(shadowsocksMethod = intent.method)) }

            is EditIntent.UpdateInbound -> _state.update { it.copy(inbound = intent.inbound) }
            is EditIntent.UpdateStream -> _state.update { it.copy(stream = intent.stream) }
            is EditIntent.UpdateOutbound -> _state.update { it.copy(outbound = intent.outbound) }
            is EditIntent.UpdatePro -> _state.update { it.copy(pro = intent.pro) }

            is EditIntent.UpdateStreamNetwork -> _state.update {
                it.copy(stream = it.stream.copy(
                    network = intent.network,
                    xhttpSettings = if (intent.network == Configurations.transportNetwork.XHTTP && it.stream.xhttpSettings == null) {
                        XrayConfig.XhttpSettings()
                    } else it.stream.xhttpSettings
                ))
            }
            is EditIntent.UpdateStreamSecurity -> _state.update {
                it.copy(stream = it.stream.copy(
                    security = intent.security,
                    realitySettings = if (intent.security == Configurations.security.REALITY && it.stream.realitySettings == null) {
                        XrayConfig.RealitySettings(
                            serverNames = emptyList(),
                            privateKey = "",
                            password = ""
                        )
                    } else it.stream.realitySettings,
                    tlsSettings = if (intent.security == Configurations.security.TLS && it.stream.tlsSettings == null) {
                        XrayConfig.TlsSettings()
                    } else it.stream.tlsSettings
                ))
            }
            is EditIntent.UpdateRealityTarget -> _state.update {
                it.copy(stream = it.stream.copy(
                    realitySettings = (it.stream.realitySettings ?: XrayConfig.RealitySettings(
                        serverNames = emptyList(),
                        privateKey = "",
                        password = ""
                    )).copy(target = intent.target)
                ))
            }
            is EditIntent.UpdateRealityFingerprint -> {}
            is EditIntent.UpdateTlsFingerprint -> {}
            
            EditIntent.Save -> { screenModelScope.launch { saveServer() } }
            EditIntent.Cancel -> { /* Handle cancel */ }
        }
    }

    suspend fun saveServer() {
        // Sync TextFieldStates back to state before saving
        _state.update { it ->
            it.copy(
            connectionName = connectionNameState.text.toString(),
            serverAddress = serverAddressState.text.toString(),
            inbound = it.inbound.copy(
                inboundPort = portState.text.toString().toIntOrNull() ?: 0,
                inboundListen = listenState.text.toString(),
                shadowsocksPassword = shadowsocksPasswordState.text.toString(),
                shadowsocksEmail = shadowsocksEmailState.text.toString(),
                vmessAlterId = vmessAlterIdState.text.toString().toIntOrNull() ?: 0,
                trojanPassword = trojanPasswordState.text.toString(),
                fallbackDest = fallbackDestState.text.toString().toIntOrNull() ?: 0
            ),
            stream = it.stream.copy(
                tlsSettings = (it.stream.tlsSettings ?: XrayConfig.TlsSettings()).copy(
                    serverName = tlsServerNameState.text.toString()
                ),
                realitySettings = (it.stream.realitySettings ?: XrayConfig.RealitySettings(
                    serverNames = emptyList(),
                    privateKey = "",
                    password = ""
                )).copy(
                    dest = realityDestState.text.toString().takeIf { it.isNotBlank() },
                    target = if (_isCustomTarget.value) null else it.stream.realitySettings?.target ?: Configurations.targetOptions.GITHUB,
                    customTarget = if (_isCustomTarget.value) realityCustomTargetState.text.toString().takeIf { it.isNotBlank() } else null,
                    serverNames = realityServerNamesState.text.toString().split(",").map { it.trim() }.filter { it.isNotBlank() },
                    privateKey = realityPrivateKeyState.text.toString(),
                    password = realityPublicKeyState.text.toString(),
                    shortIds = realityShortIdsState.text.toString().split(",").map { it.trim() }.filter { it.isNotBlank() }
                ),
                xhttpSettings = (it.stream.xhttpSettings ?: XrayConfig.XhttpSettings()).copy(
                    path = xhttpPathState.text.toString()
                )
            ),
            outbound = it.outbound.copy(
                tag = outboundTagState.text.toString().takeIf { it.isNotBlank() },
                settings = when (it.outbound.protocol) {
                    Configurations.protocol.VLESS -> {
                        XrayConfigBuilder.toSettings(
                            XrayConfig.VlessOutboundSettings(
                                vnext = listOf(
                                    XrayConfig.VnextServer(
                                        address = outboundAddressState.text.toString(),
                                        port = outboundPortState.text.toString().toIntOrNull() ?: 443,
                                        users = listOf(XrayConfig.VnextUser(id = outboundIdState.text.toString()))
                                    )
                                )
                            )
                        )
                    }
                    Configurations.protocol.SHADOWSOCKS -> {
                        XrayConfigBuilder.toSettings(
                            XrayConfig.ShadowsocksOutboundSettings(
                                servers = listOf(
                                    XrayConfig.ShadowsocksOutboundServer(
                                        address = outboundAddressState.text.toString(),
                                        port = outboundPortState.text.toString().toIntOrNull() ?: 443,
                                        method = it.outbound.shadowsocksMethod ?: Configurations.shadowsocksMethod.AES_256_GCM,
                                        password = outboundPasswordState.text.toString()
                                    )
                                )
                            )
                        )
                    }
                    else -> null
                }
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
    realityPrivateKeyState.setTextAndPlaceCursorAtEnd(keyPair.privateKey)
    realityPublicKeyState.setTextAndPlaceCursorAtEnd(keyPair.publicKey)
    _state.update {
        it.copy(stream = it.stream.copy(
            realityKeyPair = keyPair,
            realityPrivateKey = keyPair.privateKey,
            realityPublicKey = keyPair.publicKey
        ))
    }
}

fun generateShortId() {
    val newShortId = cypherService.generateShortId()
    realityShortIdsState.setTextAndPlaceCursorAtEnd(newShortId)
    _state.update { it.copy(stream = it.stream.copy(realityShortId = newShortId)) }
}
}
