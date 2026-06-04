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
import com.raaveinm.rayfield.data.ssh.ServerUnit
import com.raaveinm.rayfield.domain.helpers.Logger
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import com.raaveinm.rayfield.data.xray.types.ServerState
import com.raaveinm.rayfield.domain.ssh.ServerConfiguration
import com.raaveinm.rayfield.domain.xray.ShareLinkGenerator
import kotlinx.coroutines.flow.first
import kotlin.random.Random

//
// Created by Kirill "Raaveinm" on 5/11/26.
//

class EditScreenModel(
    val serverDao: ServerDao,
    private val cypherService: CypherService,
    private val shareLinkGenerator: ShareLinkGenerator,
    private val initialConfigId: String? = null,
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

    val logAccessPathState = TextFieldState()
    val logErrorPathState = TextFieldState()
    val logMaskAddressState = TextFieldState()

    // SSH States
    val sshPortState = TextFieldState("22")
    val sshLoginState = TextFieldState("root")
    val sshPasswordState = TextFieldState()
    val sshPathToPkeyState = TextFieldState()

    private val _state = MutableStateFlow(
        EditDraftState(
            serverId = initialServerId ?: "",
            isLoading = initialServerId != null || initialConfigId != null
        )
    )
    val state = _state.asStateFlow()

    var uuid = MutableStateFlow("")

    init {
        when {
            initialConfigId != null -> populateFormFromConfigId(initialConfigId)
            initialServerId != null -> populateFormFromId(initialServerId)
            else -> {
                _state.update { it.copy(isLoading = false) }

                val s = _state.value
                connectionNameState.setTextAndPlaceCursorAtEnd(s.connectionName)
                serverAddressState.setTextAndPlaceCursorAtEnd(s.serverAddress)

                _state.update { it.copy(serverIcon = s.serverIcon) }
                
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

                // SSH initialization
                sshPortState.setTextAndPlaceCursorAtEnd("22")
                sshLoginState.setTextAndPlaceCursorAtEnd("root")
                sshPasswordState.setTextAndPlaceCursorAtEnd("")
                sshPathToPkeyState.setTextAndPlaceCursorAtEnd("")

                val outbound = s.outbound
                outboundTagState.setTextAndPlaceCursorAtEnd(outbound.tag ?: "proxy")

                when (outbound.protocol) {
                    Configurations.protocol.VLESS -> {
                        val vlessSettings = outbound.settings?.let {
                            XrayConfigBuilder.jsonFormatter.decodeFromJsonElement<XrayConfig.VlessOutboundSettings>(it)
                        }
                        val firstVlessServer = vlessSettings?.vnext?.firstOrNull()
                        if (firstVlessServer != null) {
                            outboundAddressState.setTextAndPlaceCursorAtEnd(firstVlessServer.address)
                            outboundPortState.setTextAndPlaceCursorAtEnd(firstVlessServer.port.toString())
                            outboundIdState.setTextAndPlaceCursorAtEnd(firstVlessServer.users.firstOrNull()?.id ?: "")
                        }
                    }
                    Configurations.protocol.SHADOWSOCKS -> {
                        val ssSettings = outbound.settings?.let {
                            XrayConfigBuilder.jsonFormatter.decodeFromJsonElement<XrayConfig.ShadowsocksOutboundSettings>(it)
                        }
                        val firstSsServer = ssSettings?.servers?.firstOrNull()
                        if (firstSsServer != null) {
                            outboundAddressState.setTextAndPlaceCursorAtEnd(firstSsServer.address)
                            outboundPortState.setTextAndPlaceCursorAtEnd(firstSsServer.port.toString())
                            outboundPasswordState.setTextAndPlaceCursorAtEnd(firstSsServer.password)
                        }
                    }
                    else -> {}
                }

                val pro = s.pro
                logAccessPathState.setTextAndPlaceCursorAtEnd(pro.log.access ?: "")
                logErrorPathState.setTextAndPlaceCursorAtEnd(pro.log.error ?: "")
                logMaskAddressState.setTextAndPlaceCursorAtEnd(pro.log.maskAddress ?: "")
            }
        }
    }

    private fun populateFormFromConfigId(configId: String) {
        screenModelScope.launch {
            val serverState = serverDao.getServerStateById(configId)
            if (serverState != null) {
                populateFormFromId(serverState.serverId)
            } else {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun populateFormFromId(initialServerId: String) {
        screenModelScope.launch {
            val server = serverDao.getServerUnitById(initialServerId) ?: return@launch
            
            // Base SSH/Server properties mapping
            connectionNameState.setTextAndPlaceCursorAtEnd(server.serverName ?: "")
            serverAddressState.setTextAndPlaceCursorAtEnd(server.serverIp)
            sshPortState.setTextAndPlaceCursorAtEnd(server.serverSshPort.toString())
            sshLoginState.setTextAndPlaceCursorAtEnd(server.serverSshLogin)
            sshPasswordState.setTextAndPlaceCursorAtEnd(server.serverSshPassword ?: "")
            sshPathToPkeyState.setTextAndPlaceCursorAtEnd(server.serverSshPrivateKey ?: "")

            val states = serverDao.getServerStatesForServer(initialServerId).first()
            val icons = states.associate { it.configId to it.iconLocation }
            _state.update { it.copy(serverIcon = server.iconLocation, configIcons = icons) }

            val jsonConfig = server.serverJsonConfig
            if (!jsonConfig.isNullOrBlank()) {
                try {
                    // Parse the root JSON config string
                    val config = XrayConfigBuilder.parseJson(jsonConfig)
                    
                    // Unpack Inbound Layer
                    val inbound = config.inbounds?.firstOrNull()
                    var unpackedInboundSettings: XrayConfig.InboundSettings?
                    var stream: XrayConfig.StreamSettings?

                    if (inbound != null) {
                        listenState.setTextAndPlaceCursorAtEnd(inbound.listen)
                        portState.setTextAndPlaceCursorAtEnd(inbound.port.toString())
                        
                        // Dynamic unpacking of dynamic JsonObject settings
                        unpackedInboundSettings = when (inbound.protocol) {
                            Configurations.inboundProtocol.VLESS -> {
                                XrayConfigBuilder.jsonFormatter.decodeFromJsonElement<XrayConfig.VlessInboundSettings>(inbound.settings)
                            }
                            Configurations.inboundProtocol.SHADOWSOCKS -> {
                                val ssInbound = XrayConfigBuilder.jsonFormatter.decodeFromJsonElement<XrayConfig.ShadowsocksInboundSettings>(inbound.settings)
                                shadowsocksPasswordState.setTextAndPlaceCursorAtEnd(ssInbound.password)
                                shadowsocksEmailState.setTextAndPlaceCursorAtEnd(ssInbound.email)
                                ssInbound
                            }
                        }

                        // Unpack Inbound Stream Settings
                        stream = inbound.streamSettings
                        if (stream != null) {
                            xhttpPathState.setTextAndPlaceCursorAtEnd(stream.xhttpSettings?.path ?: "")
                            tlsServerNameState.setTextAndPlaceCursorAtEnd(stream.tlsSettings?.serverName ?: "")
                            
                            stream.realitySettings?.let { reality ->
                                realityDestState.setTextAndPlaceCursorAtEnd(reality.dest ?: "")
                                realityCustomTargetState.setTextAndPlaceCursorAtEnd(reality.customTarget ?: "")
                                realityServerNamesState.setTextAndPlaceCursorAtEnd(reality.serverNames.joinToString(", "))
                                realityPrivateKeyState.setTextAndPlaceCursorAtEnd(reality.privateKey)
                                realityPublicKeyState.setTextAndPlaceCursorAtEnd(reality.password)
                                realityShortIdsState.setTextAndPlaceCursorAtEnd(reality.shortIds.joinToString(", "))
                                _isCustomTarget.value = !reality.customTarget.isNullOrBlank()
                            }
                        }

                        // Update local Draft State for discrete variables
                        _state.update { current ->
                            val updatedInbound = current.inbound.copy(
                                inboundListen = inbound.listen,
                                inboundPort = inbound.port,
                                inboundProtocol = inbound.protocol,
                                settings = unpackedInboundSettings ?: current.inbound.settings
                            )

                            val finalInbound = if (unpackedInboundSettings is XrayConfig.ShadowsocksInboundSettings) {
                                updatedInbound.copy(
                                    shadowsocksPassword = unpackedInboundSettings.password,
                                    shadowsocksEmail = unpackedInboundSettings.email,
                                    shadowsocksMethod = unpackedInboundSettings.method,
                                    shadowsocksNetwork = unpackedInboundSettings.network,
                                    shadowsocksUsers = unpackedInboundSettings.users
                                )
                            } else updatedInbound

                            current.copy(
                                inbound = finalInbound,
                                stream = current.stream.copy(
                                    network = stream?.network ?: Configurations.transportNetwork.TCP,
                                    security = stream?.security ?: Configurations.security.NONE,
                                    tlsSettings = stream?.tlsSettings,
                                    realitySettings = stream?.realitySettings,
                                    xhttpSettings = stream?.xhttpSettings
                                )
                            )
                        }
                    }

                    // Unpack Outbound Layer
                    val outbound = config.outbounds?.firstOrNull()
                    if (outbound != null) {
                        outboundTagState.setTextAndPlaceCursorAtEnd(outbound.tag ?: "proxy")
                        
                        if (outbound.protocol == Configurations.protocol.VLESS) {
                            val vlessOut = XrayConfigBuilder.jsonFormatter
                                .decodeFromJsonElement<XrayConfig.VlessOutboundSettings>(
                                    outbound.settings ?: JsonObject(emptyMap()))
                            val serverNode = vlessOut.vnext.firstOrNull()
                            outboundAddressState.setTextAndPlaceCursorAtEnd(serverNode?.address ?: "")
                            outboundPortState.setTextAndPlaceCursorAtEnd(serverNode?.port?.toString() ?: "443")
                            outboundIdState.setTextAndPlaceCursorAtEnd(serverNode?.users?.firstOrNull()?.id ?: "")
                        } else if (outbound.protocol == Configurations.protocol.SHADOWSOCKS) {
                            val ssOut = XrayConfigBuilder.jsonFormatter
                                .decodeFromJsonElement<XrayConfig.ShadowsocksOutboundSettings>(
                                    outbound.settings ?: JsonObject(emptyMap()))
                            val serverNode = ssOut.servers.firstOrNull()
                            outboundAddressState.setTextAndPlaceCursorAtEnd(serverNode?.address ?: "")
                            outboundPortState.setTextAndPlaceCursorAtEnd(serverNode?.port?.toString() ?: "443")
                            outboundPasswordState.setTextAndPlaceCursorAtEnd(serverNode?.password ?: "")
                            
                            // Update discrete state fields for Shadowsocks outbound
                            _state.update { it.copy(
                                outbound = it.outbound.copy(
                                    shadowsocksMethod = serverNode?.method
                                )
                            ) }
                        }

                        _state.update { it.copy(
                            outbound = it.outbound.copy(
                                protocol = outbound.protocol,
                                tag = outbound.tag
                            )
                        ) }
                    }

                    // Unpack Pro Layer (Logs & Routing)
                    val log = config.log
                    if (log != null) {
                        logAccessPathState.setTextAndPlaceCursorAtEnd(log.access ?: "")
                        logErrorPathState.setTextAndPlaceCursorAtEnd(log.error ?: "")
                        logMaskAddressState.setTextAndPlaceCursorAtEnd(log.maskAddress ?: "")
                    }
                    
                    _state.update { it.copy(
                        pro = it.pro.copy(
                            log = log ?: XrayConfig.LogConfig(),
                            routing = config.routing ?: XrayConfig.RoutingConfig()
                        ),
                        isLoading = false
                    ) }

                } catch (e: Exception) {
                    Logger.e("EditScreenModel", "Error parsing JSON: ${e.message}")
                    _state.update { it.copy(isLoading = false) }
                }
            } else {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun processIntent(intent: EditIntent) {
        when (intent) {
            is EditIntent.UpdateName -> _state.update { it.copy(connectionName = intent.name) }
            is EditIntent.SetIconServer -> _state.update { it.copy(serverIcon = intent.icon) }
            is EditIntent.SetIconUserConfig -> _state.update {
                it.copy(configIcons = it.configIcons + (intent.id to intent.icon))
            }
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
            is EditIntent.UpdateShadowsocksFallback -> _state.update { it.copy(inbound = it.inbound.copy(isShadowsocksFallback = intent.enabled)) }
            is EditIntent.UpdateVlessDecryption -> _state.update { it.copy(inbound = it.inbound.copy(vlessDecryption = intent.decryption)) }
            
            is EditIntent.UpdateOutboundProtocol -> _state.update { it.copy(outbound = it.outbound.copy(protocol = intent.protocol)) }
            is EditIntent.UpdateOutboundShadowsocksMethod -> _state.update { it.copy(outbound = it.outbound.copy(shadowsocksMethod = intent.method)) }

            is EditIntent.UpdateLogLevel -> _state.update {
                it.copy(pro = it.pro.copy(log = it.pro.log.copy(loglevel = intent.level)))
            }
            is EditIntent.UpdateDnsLogEnabled -> _state.update {
                it.copy(pro = it.pro.copy(log = it.pro.log.copy(dnsLog = intent.enabled)))
            }
            is EditIntent.UpdateRoutingDomainStrategy -> _state.update {
                it.copy(pro = it.pro.copy(routing = it.pro.routing.copy(domainStrategy = intent.strategy)))
            }

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
            is EditIntent.UpdateRealityTarget -> {
                _state.update {
                    it.copy(stream = it.stream.copy(
                        realitySettings = (it.stream.realitySettings ?: XrayConfig.RealitySettings(
                            serverNames = emptyList(),
                            privateKey = "",
                            password = ""
                        )).copy(target = intent.target)
                    ))
                }
                
                // AUTO-POPULATING FIELDS (UX Magic)
                val targetDomain = intent.target.domain
                realityDestState.setTextAndPlaceCursorAtEnd("$targetDomain:443")
                realityServerNamesState.setTextAndPlaceCursorAtEnd(targetDomain)
            }
            is EditIntent.UpdateRealityFingerprint -> _state.update { 
                it.copy(stream = it.stream.copy(fingerprint = intent.fingerprint))
            }
            is EditIntent.UpdateTlsFingerprint -> _state.update { 
                it.copy(stream = it.stream.copy(fingerprint = intent.fingerprint))
            }
            
            EditIntent.Save -> { screenModelScope.launch { saveServer() } }
            EditIntent.Delete -> {
                screenModelScope.launch {
                    val currentId = _state.value.serverId
                    if (currentId.isNotBlank()) {
                        val server = serverDao.getServerUnitById(currentId)
                        if (server != null) {
                            serverDao.deleteServerUnit(server)
                            _state.update { it.copy(isSaved = true) }
                        }
                    }
                }
            }
            EditIntent.Cancel -> { /* Handle cancel */ }
        }
    }

    suspend fun saveServer() {
        val currentState = _state.value

        var ssFallbackInbound: XrayConfig.InboundConfig? = null
        val ssFallbackPort = Random.nextInt(10000, 60000)

        val inboundSettingsJson = when (currentState.inbound.inboundProtocol) {
            Configurations.inboundProtocol.VLESS -> {
                var currentVlessSettings = currentState.inbound.settings as? XrayConfig.VlessInboundSettings
                    ?: XrayConfig.VlessInboundSettings()

                val processedUsers = if (currentVlessSettings.users.isEmpty()) {
                    listOf(XrayConfig.VlessUser(id = cypherService.generateUuid(), email = "default@rayfield.com"))
                } else {
                    currentVlessSettings.users.map { user ->
                        user.copy(flow = if (user.flow == Configurations.vlessFlow.NONE) null else user.flow)
                    }
                }

                if (currentState.inbound.isShadowsocksFallback) {
                    currentVlessSettings = currentVlessSettings.copy(
                        fallbacks = (currentVlessSettings.fallbacks ?: emptyList()) + 
                            XrayConfig.VlessFallback(dest = "127.0.0.1", port = ssFallbackPort)
                    )

                    // Prepare the hidden Shadowsocks inbound
                    val method = currentState.inbound.shadowsocksMethod ?: Configurations.shadowsocksMethod.AES_256_GCM
                    val password = shadowsocksPasswordState.text.toString().takeIf { it.isNotBlank() } ?: cypherService.generateUuid()
                    val email = shadowsocksEmailState.text.toString().takeIf { it.isNotBlank() } ?: "fallback@xray.com"

                    ssFallbackInbound = XrayConfig.InboundConfig(
                        listen = "127.0.0.1",
                        port = ssFallbackPort,
                        protocol = Configurations.inboundProtocol.SHADOWSOCKS,
                        settings = XrayConfigBuilder.shadowsocksSettingBuilder(
                            network = currentState.inbound.shadowsocksNetwork,
                            method = method,
                            password = password,
                            email = email,
                            users = listOf(
                                XrayConfig.ShadowsocksUser(
                                    password = password,
                                    method = method,
                                    email = email
                                )
                            )
                        ),
                        tag = "inbound-ss-fallback"
                    )
                }

                currentVlessSettings = currentVlessSettings.copy(
                    users = processedUsers,
                    decryption = currentState.inbound.vlessDecryption
                )

                XrayConfigBuilder.toSettings(currentVlessSettings)
            }
            Configurations.inboundProtocol.SHADOWSOCKS -> {
                val method = currentState.inbound.shadowsocksMethod ?: Configurations.shadowsocksMethod.AES_256_GCM
                val password = shadowsocksPasswordState.text.toString().takeIf { it.isNotBlank() } ?: cypherService.generateUuid()
                val email = shadowsocksEmailState.text.toString().takeIf { it.isNotBlank() } ?: "love@xray.com"

                XrayConfigBuilder.shadowsocksSettingBuilder(
                    network = currentState.inbound.shadowsocksNetwork,
                    method = method,
                    password = password,
                    email = email,
                    users = currentState.inbound.shadowsocksUsers.ifEmpty {
                        listOf(
                            XrayConfig.ShadowsocksUser(
                                password = password,
                                method = method,
                                email = email
                            )
                        )
                    }
                )
            }
        }

        val finalStreamSettings = XrayConfig.StreamSettings(
            network = currentState.stream.network,
            security = currentState.stream.security,
            tlsSettings = if (currentState.stream.security == Configurations.security.TLS) {
                XrayConfig.TlsSettings(
                    serverName = tlsServerNameState.text.toString().takeIf { it.isNotBlank() } ?: "google.com",
                    fingerprint = currentState.stream.fingerprint
                )
            } else null,
            realitySettings = if (currentState.stream.security == Configurations.security.REALITY) {
                val pKey = realityPrivateKeyState.text.toString().takeIf { it.isNotBlank() }
                val pubKey = realityPublicKeyState.text.toString().takeIf { it.isNotBlank() }

                // Auto-generate keys if missing
                val finalKeyPair = if (pKey == null || pubKey == null) {
                    cypherService.generateKeyPair()
                } else null

                val sNames = realityServerNamesState.text.toString().split(",").map { it.trim() }.filter { it.isNotBlank() }
                val sIds = realityShortIdsState.text.toString().split(",").map { it.trim() }.filter { it.isNotBlank() }

                val activeTargetDomain = if (_isCustomTarget.value) {
                    realityCustomTargetState.text.toString().takeIf { it.isNotBlank() } ?: "google.com"
                } else {
                    (currentState.stream.realitySettings?.target ?: Configurations.targetOptions.GITHUB).domain
                }

                XrayConfig.RealitySettings(
                    dest = realityDestState.text.toString().takeIf { it.isNotBlank() } ?: (activeTargetDomain + ":443"),
                    target = if (_isCustomTarget.value) null else currentState.stream.realitySettings?.target ?: Configurations.targetOptions.GITHUB,
                    customTarget = if (_isCustomTarget.value) realityCustomTargetState.text.toString().takeIf { it.isNotBlank() } else null,
                    serverNames = sNames.ifEmpty { listOf(activeTargetDomain) },
                    privateKey = pKey ?: finalKeyPair!!.privateKey,
                    password = pubKey ?: finalKeyPair!!.publicKey,
                    shortIds = sIds.ifEmpty { listOf("") },
                    fingerprint = currentState.stream.fingerprint
                )
            } else null,
            xhttpSettings = if (currentState.stream.network == Configurations.transportNetwork.XHTTP) {
                XrayConfig.XhttpSettings(path = xhttpPathState.text.toString().takeIf { it.isNotBlank() } ?: "/download")
            } else null
        )

        // Assemble Outbound Config
        val outboundSettingsJson = when (currentState.outbound.protocol) {
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
                                method = currentState.outbound.shadowsocksMethod ?: Configurations.shadowsocksMethod.AES_256_GCM,
                                password = outboundPasswordState.text.toString()
                            )
                        )
                    )
                )
            }
            else -> null
        }

        // Assemble Global Root XrayConfig Object
        val finalXrayConfig = XrayConfig(
            log = XrayConfig.LogConfig(
                access = logAccessPathState.text.toString().takeIf { it.isNotBlank() },
                error = logErrorPathState.text.toString().takeIf { it.isNotBlank() },
                loglevel = currentState.pro.log.loglevel,
                dnsLog = currentState.pro.log.dnsLog,
                maskAddress = logMaskAddressState.text.toString().takeIf { it.isNotBlank() }
            ),
            routing = XrayConfig.RoutingConfig(
                domainStrategy = currentState.pro.routing.domainStrategy
            ),
            inbounds = listOfNotNull(
                XrayConfig.InboundConfig(
                    listen = listenState.text.toString(),
                    port = portState.text.toString().toIntOrNull() ?: 443,
                    protocol = currentState.inbound.inboundProtocol,
                    settings = inboundSettingsJson,
                    streamSettings = finalStreamSettings,
                    tag = "inbound-main"
                ),
                ssFallbackInbound
            ),
            outbounds = listOf(
                XrayConfig.OutboundConfig(
                    tag = outboundTagState.text.toString().takeIf { it.isNotBlank() } ?: "proxy",
                    protocol = currentState.outbound.protocol,
                    settings = outboundSettingsJson
                )
            )
        )

        val jsonString = XrayConfigBuilder.buildJson(finalXrayConfig)

        val existingServer = if (currentState.serverId.isNotBlank()) {
            serverDao.getServerUnitById(currentState.serverId)
        } else null

        val targetServerId = currentState.serverId.takeIf { it.isNotBlank() }
            ?: "server_${Random.nextInt(10000)}"

        val targetServerUnit = if (existingServer != null) {
            existingServer.copy(
                serverName = connectionNameState.text.toString().takeIf { it.isNotBlank() },
                serverIp = serverAddressState.text.toString(),
                serverSshLogin = sshLoginState.text.toString().takeIf { it.isNotBlank() } ?: existingServer.serverSshLogin,
                serverSshPassword = sshPasswordState.text.toString(),
                serverSshPrivateKey = sshPathToPkeyState.text.toString(),
                serverSshPort = sshPortState.text.toString().toIntOrNull() ?: existingServer.serverSshPort,
                serverJsonConfig = jsonString,
                iconLocation = currentState.serverIcon
            )
        } else {
            ServerUnit(
                serverId = targetServerId, // USE THE SAVED ID
                serverName = connectionNameState.text.toString().takeIf { it.isNotBlank() },
                serverIp = serverAddressState.text.toString(),
                serverSshLogin = sshLoginState.text.toString().takeIf { it.isNotBlank() } ?: "root",
                serverSshPassword = sshPasswordState.text.toString(),
                serverSshPrivateKey = sshPathToPkeyState.text.toString(),
                serverSshPort = sshPortState.text.toString().toIntOrNull() ?: 22,
                serverJsonConfig = jsonString,
                iconLocation = currentState.serverIcon
            )
        }

        serverDao.insertServerUnit(targetServerUnit)
        
        // --- Database Reconciliation (Link Generation & Sync) ---
        val existingStates = serverDao.getServerStatesForServer(targetServerUnit.serverId).first()
        val existingStateMap = existingStates.associateBy { it.configId }

        data class StateDraft(val id: String, val link: String, val name: String, val json: String)
        val currentUsersList = mutableListOf<StateDraft>()

        when (currentState.inbound.inboundProtocol) {
            Configurations.inboundProtocol.VLESS -> {
                val settings = XrayConfigBuilder.jsonFormatter.decodeFromJsonElement<XrayConfig.VlessInboundSettings>(inboundSettingsJson)
                settings.users.forEach { user ->
                    val freshLink = shareLinkGenerator.generateVlessLink(
                        serverIp = targetServerUnit.serverIp,
                        port = finalXrayConfig.inbounds?.firstOrNull()?.port ?: 443,
                        user = user,
                        stream = finalStreamSettings
                    )
                    val freshJson = shareLinkGenerator.generateClientJson(
                        serverIp = targetServerUnit.serverIp,
                        port = finalXrayConfig.inbounds?.firstOrNull()?.port ?: 443,
                        protocol = Configurations.protocol.VLESS,
                        vlessUser = user,
                        stream = finalStreamSettings,
                        tag = targetServerUnit.serverName
                    )
                    currentUsersList.add(StateDraft(user.id, freshLink, user.email, freshJson))
                }
            }
            Configurations.inboundProtocol.SHADOWSOCKS -> {
                val settings = XrayConfigBuilder.jsonFormatter.decodeFromJsonElement<XrayConfig.ShadowsocksInboundSettings>(inboundSettingsJson)
                
                // If we have explicit users, use them. Otherwise, use the global settings as the only user.
                if (currentState.inbound.shadowsocksUsers.isEmpty()) {
                    val globalUser = XrayConfig.ShadowsocksUser(
                        password = settings.password,
                        method = settings.method,
                        email = settings.email
                    )
                    val globalFreshLink = shareLinkGenerator.generateShadowsocksLink(
                        serverIp = targetServerUnit.serverIp,
                        port = finalXrayConfig.inbounds?.firstOrNull()?.port ?: 443,
                        user = globalUser,
                        stream = finalStreamSettings
                    )
                    val globalFreshJson = shareLinkGenerator.generateClientJson(
                        serverIp = targetServerUnit.serverIp,
                        port = finalXrayConfig.inbounds?.firstOrNull()?.port ?: 443,
                        protocol = Configurations.protocol.SHADOWSOCKS,
                        ssUser = globalUser,
                        stream = finalStreamSettings,
                        tag = targetServerUnit.serverName
                    )
                    currentUsersList.add(StateDraft("global_${targetServerUnit.serverId}", globalFreshLink, settings.email, globalFreshJson))
                } else {
                    settings.users.forEachIndexed { index, user ->
                        val freshLink = shareLinkGenerator.generateShadowsocksLink(
                            serverIp = targetServerUnit.serverIp,
                            port = finalXrayConfig.inbounds?.firstOrNull()?.port ?: 443,
                            user = user,
                            stream = finalStreamSettings
                        )
                        val freshJson = shareLinkGenerator.generateClientJson(
                            serverIp = targetServerUnit.serverIp,
                            port = finalXrayConfig.inbounds?.firstOrNull()?.port ?: 443,
                            protocol = Configurations.protocol.SHADOWSOCKS,
                            ssUser = user,
                            stream = finalStreamSettings,
                            tag = targetServerUnit.serverName
                        )
                        currentUsersList.add(StateDraft("user_${index}_${targetServerUnit.serverId}", freshLink, user.email, freshJson))
                    }
                }
            }
        }

        val currentIds = currentUsersList.map { it.id }.toSet()

        currentUsersList.forEach { draft ->
            if (existingStateMap.containsKey(draft.id)) {
                val stateToUpdate = existingStateMap[draft.id]!!
                val newIcon = currentState.configIcons[draft.id]
                if (stateToUpdate.sharedLink != draft.link || stateToUpdate.connectionName != draft.name || stateToUpdate.serverAddress != targetServerUnit.serverIp || stateToUpdate.jsonSettings != draft.json || stateToUpdate.iconLocation != newIcon) {
                    serverDao.insertServerState(stateToUpdate.copy(
                        sharedLink = draft.link,
                        connectionName = draft.name,
                        serverAddress = targetServerUnit.serverIp,
                        jsonSettings = draft.json,
                        iconLocation = newIcon
                    ))
                }
            } else {
                val newState = ServerState(
                    configId = draft.id,
                    serverId = targetServerUnit.serverId,
                    connectionName = draft.name,
                    serverAddress = targetServerUnit.serverIp,
                    sharedLink = draft.link,
                    iconLocation = currentState.configIcons[draft.id],
                    protocol = currentState.inbound.inboundProtocol.name.lowercase(),
                    jsonSettings = draft.json
                )
                serverDao.insertServerState(newState)
            }
        }

        // Delete removed users
        val idsToDelete = existingStateMap.keys - currentIds
        idsToDelete.forEach { id ->
            serverDao.deleteServerState(existingStateMap[id]!!)
        }

        _state.update { it.copy(serverId = targetServerUnit.serverId, isSaved = true) }
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


    ///////////////////////////////////////////////
    // Server configuration
    ///////////////////////////////////////////////
    fun installServer() {
        val currentServerId = _state.value.serverId
        if (currentServerId.isBlank()) {
            Logger.e("EditScreenModel", "Cannot install: serverId is blank. Save first.")
            return
        }

        val s = ServerConfiguration()
        screenModelScope.launch {
            try {
                val server = serverDao.getServerUnitById(currentServerId)
                if (server == null) {
                    Logger.e("EditScreenModel", "Server not found in DB: $currentServerId")
                    return@launch
                }

                val jsonConfig = server.serverJsonConfig
                if (jsonConfig.isNullOrBlank()) {
                    Logger.e("EditScreenModel", "No JSON configuration found in DB for $currentServerId")
                    return@launch
                }

                s.setupConnection(
                    serverIp = server.serverIp,
                    serverPort = server.serverSshPort,
                    sshUsername = server.serverSshLogin,
                    sshPassword = server.serverSshPassword,
                    sshPrivateKey = server.serverSshPrivateKey
                )

                val installationResult = s.runInstallation()
                Logger.i("EditScreenModel", "Installation result: $installationResult")

                val configRes = s.runConfiguration(jsonConfig)
                Logger.i("EditScreenModel", "Configuration result: $configRes")

                val startRes = s.startConfiguration()
                Logger.i("EditScreenModel", "Start result: $startRes")

                val isRunning = s.verifyInstallation()
                Logger.i("EditScreenModel", "Is Xray running: $isRunning")

            } catch (e: Exception) {
                Logger.e("EditScreenModel", "Installation failed: ${e.message}")
            } finally {
                s.terminateSession()
            }
        }
    }

    fun restartConfiguration() {
        val currentServerId = _state.value.serverId
        if (currentServerId.isBlank()) {
            Logger.e("EditScreenModel", "Cannot restart: serverId is blank. Save first.")
            return
        }

        val s = ServerConfiguration()
        screenModelScope.launch {
            try {
                val server = serverDao.getServerUnitById(currentServerId)
                if (server == null) {
                    Logger.e("EditScreenModel", "Server not found in DB: $currentServerId")
                    return@launch
                }
                val jsonConfig = server.serverJsonConfig
                if (jsonConfig.isNullOrBlank()) {
                    Logger.e(
                        "EditScreenModel",
                        "No JSON configuration found in DB for $currentServerId"
                    )
                    return@launch
                }
                s.setupConnection(
                    serverIp = server.serverIp,
                    serverPort = server.serverSshPort,
                    sshUsername = server.serverSshLogin,
                    sshPassword = server.serverSshPassword,
                    sshPrivateKey = server.serverSshPrivateKey
                )
                s.reassembleConfiguration(jsonConfig)
                val restartRes = s.startConfiguration()
                Logger.i("EditScreenModel", "Restart result: $restartRes")
                val isRunning = s.verifyInstallation()
                Logger.i("EditScreenModel", "Is Xray running: $isRunning")
            } catch (e: Exception) {
                Logger.e("EditScreenModel", "Restart failed: ${e.message}")
            } finally {
                s.terminateSession()
            }
        }
    }
}
