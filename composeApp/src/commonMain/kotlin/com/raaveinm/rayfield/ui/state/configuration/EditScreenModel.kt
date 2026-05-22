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
import com.raaveinm.rayfield.domain.xray.ShareLinkGenerator
import kotlinx.coroutines.flow.first

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
            isLoading = initialServerId != null
        )
    )
    val state = _state.asStateFlow()

    var uuid = MutableStateFlow("")
    var privateKey = MutableStateFlow("")
    var publicKey = MutableStateFlow("")
    var shortId = MutableStateFlow("")

    init {
        if (initialServerId != null) {
            populateFormFromId(initialServerId)
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

            // SSH initialization
            sshPortState.setTextAndPlaceCursorAtEnd("22")
            sshLoginState.setTextAndPlaceCursorAtEnd("root")
            sshPasswordState.setTextAndPlaceCursorAtEnd("")
            sshPathToPkeyState.setTextAndPlaceCursorAtEnd("")

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

            val pro = s.pro
            logAccessPathState.setTextAndPlaceCursorAtEnd(pro.log.access ?: "")
            logErrorPathState.setTextAndPlaceCursorAtEnd(pro.log.error ?: "")
            logMaskAddressState.setTextAndPlaceCursorAtEnd(pro.log.maskAddress ?: "")
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

            val jsonConfig = server.serverJsonConfig
            if (!jsonConfig.isNullOrBlank()) {
                try {
                    // Parse the root JSON config string
                    val config = XrayConfigBuilder.parseJson(jsonConfig)
                    
                    // Unpack Inbound Layer
                    val inbound = config.inbounds?.firstOrNull()
                    var unpackedInboundSettings: XrayConfig.InboundSettings? = null
                    var stream: XrayConfig.StreamSettings? = null

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
                        _state.update { it.copy(
                            inbound = it.inbound.copy(
                                inboundListen = inbound.listen,
                                inboundPort = inbound.port,
                                inboundProtocol = inbound.protocol,
                                settings = unpackedInboundSettings ?: it.inbound.settings
                            ),
                            stream = it.stream.copy(
                                network = stream?.network ?: Configurations.transportNetwork.TCP,
                                security = stream?.security ?: Configurations.security.NONE,
                                tlsSettings = stream?.tlsSettings,
                                realitySettings = stream?.realitySettings,
                                xhttpSettings = stream?.xhttpSettings
                            )
                        ) }
                    }

                    // Unpack Outbound Layer
                    val outbound = config.outbounds?.firstOrNull()
                    if (outbound != null) {
                        outboundTagState.setTextAndPlaceCursorAtEnd(outbound.tag ?: "proxy")
                        
                        if (outbound.protocol == Configurations.protocol.VLESS) {
                            val vlessOut = XrayConfigBuilder.jsonFormatter.decodeFromJsonElement<XrayConfig.VlessOutboundSettings>(outbound.settings ?: JsonObject(emptyMap()))
                            val serverNode = vlessOut.vnext.firstOrNull()
                            outboundAddressState.setTextAndPlaceCursorAtEnd(serverNode?.address ?: "")
                            outboundPortState.setTextAndPlaceCursorAtEnd(serverNode?.port?.toString() ?: "443")
                            outboundIdState.setTextAndPlaceCursorAtEnd(serverNode?.users?.firstOrNull()?.id ?: "")
                        } else if (outbound.protocol == Configurations.protocol.SHADOWSOCKS) {
                            val ssOut = XrayConfigBuilder.jsonFormatter.decodeFromJsonElement<XrayConfig.ShadowsocksOutboundSettings>(outbound.settings ?: JsonObject(emptyMap()))
                            val serverNode = ssOut.servers.firstOrNull()
                            outboundAddressState.setTextAndPlaceCursorAtEnd(serverNode?.address ?: "")
                            outboundPortState.setTextAndPlaceCursorAtEnd(serverNode?.port?.toString() ?: "443")
                            outboundPasswordState.setTextAndPlaceCursorAtEnd(serverNode?.password ?: "")
                        }

                        _state.update { it.copy(
                            outbound = it.outbound.copy(
                                protocol = outbound.protocol,
                                tag = outbound.tag
                            )
                        ) }
                    }

                    // 6. Unpack Pro Layer (Logs & Routing)
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
        val currentState = _state.value

        val inboundSettingsJson = when (currentState.inbound.inboundProtocol) {
            Configurations.inboundProtocol.VLESS -> {
                var currentVlessSettings = currentState.inbound.settings as? XrayConfig.VlessInboundSettings
                    ?: XrayConfig.VlessInboundSettings()
                
                // Ensure at least one user exists
                if (currentVlessSettings.users.isEmpty()) {
                    currentVlessSettings = currentVlessSettings.copy(
                        users = listOf(XrayConfig.VlessUser(id = cypherService.generateUuid(), email = "default@rayfield.com"))
                    )
                }

                // FORCEFULLY set to NONE before saving
                currentVlessSettings = currentVlessSettings.copy(
                    decryption = Configurations.vlessDecryption.NONE
                )

                XrayConfigBuilder.toSettings(currentVlessSettings)
            }
            Configurations.inboundProtocol.SHADOWSOCKS -> {
                XrayConfigBuilder.shadowsocksSettingBuilder(
                    network = currentState.inbound.shadowsocksNetwork,
                    method = currentState.inbound.shadowsocksMethod ?: Configurations.shadowsocksMethod.AES_256_GCM,
                    password = shadowsocksPasswordState.text.toString().takeIf { it.isNotBlank() } ?: cypherService.generateUuid(),
                    email = shadowsocksEmailState.text.toString().takeIf { it.isNotBlank() } ?: "love@xray.com"
                )
            }
        }

        // Compile Stream Settings (Transport & Security Layers)
        val streamSettings = XrayConfig.StreamSettings(
            network = currentState.stream.network,
            security = currentState.stream.security,
            tlsSettings = if (currentState.stream.security == Configurations.security.TLS) {
                XrayConfig.TlsSettings(serverName = tlsServerNameState.text.toString().takeIf { it.isNotBlank() } ?: "google.com")
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
                    shortIds = sIds.ifEmpty { listOf("") }
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
            inbounds = listOf(
                XrayConfig.InboundConfig(
                    listen = listenState.text.toString(),
                    port = portState.text.toString().toIntOrNull() ?: 443,
                    protocol = currentState.inbound.inboundProtocol,
                    settings = inboundSettingsJson,
                    streamSettings = streamSettings,
                    tag = "inbound-main"
                )
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

        // 1. Take the ID from the current state, or generate a new one ONLY IF it is not there
        val targetServerId = currentState.serverId.takeIf { it.isNotBlank() }
            ?: "server_${kotlin.random.Random.nextInt(10000)}"

        val targetServerUnit = if (existingServer != null) {
            existingServer.copy(
                serverName = connectionNameState.text.toString().takeIf { it.isNotBlank() },
                serverIp = serverAddressState.text.toString(),
                serverSshLogin = sshLoginState.text.toString().takeIf { it.isNotBlank() } ?: existingServer.serverSshLogin,
                serverSshPassword = sshPasswordState.text.toString(),
                serverSshPrivateKey = sshPathToPkeyState.text.toString(),
                serverSshPort = sshPortState.text.toString().toIntOrNull() ?: existingServer.serverSshPort,
                serverJsonConfig = jsonString
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
                iconLocation = null
            )
        }

        serverDao.insertServerUnit(targetServerUnit)
        
        // --- Database Reconciliation (Link Generation & Sync) ---
        val existingStates = serverDao.getServerStatesForServer(targetServerUnit.serverId).first()
        val existingStateMap = existingStates.associateBy { it.configId }

        val currentUsersList = mutableListOf<Triple<String, String, String>>() // ID, FreshLink, Email/Name

        when (currentState.inbound.inboundProtocol) {
            Configurations.inboundProtocol.VLESS -> {
                val settings = XrayConfigBuilder.jsonFormatter.decodeFromJsonElement<XrayConfig.VlessInboundSettings>(inboundSettingsJson)
                settings.users.forEach { user ->
                    val freshLink = shareLinkGenerator.generateVlessLink(
                        serverIp = targetServerUnit.serverIp,
                        port = finalXrayConfig.inbounds?.firstOrNull()?.port ?: 443,
                        user = user,
                        stream = streamSettings
                    )
                    currentUsersList.add(Triple(user.id, freshLink, user.email))
                }
            }
            Configurations.inboundProtocol.SHADOWSOCKS -> {
                val settings = XrayConfigBuilder.jsonFormatter.decodeFromJsonElement<XrayConfig.ShadowsocksInboundSettings>(inboundSettingsJson)
                // Use global settings as a user for link generation
                val globalFreshLink = shareLinkGenerator.generateShadowsocksLink(
                    serverIp = targetServerUnit.serverIp,
                    port = finalXrayConfig.inbounds?.firstOrNull()?.port ?: 443,
                    user = XrayConfig.ShadowsocksUser(
                        password = settings.password,
                        method = settings.method,
                        email = settings.email
                    )
                )
                currentUsersList.add(Triple("global_${targetServerUnit.serverId}", globalFreshLink, settings.email))

                // Multi-users reconciliation
                settings.users.forEachIndexed { index, user ->
                    val freshLink = shareLinkGenerator.generateShadowsocksLink(
                        serverIp = targetServerUnit.serverIp,
                        port = finalXrayConfig.inbounds?.firstOrNull()?.port ?: 443,
                        user = user
                    )
                    // Use index-based ID for Shadowsocks users if they don't have unique IDs
                    currentUsersList.add(Triple("user_${index}_${targetServerUnit.serverId}", freshLink, user.email))
                }
            }
        }

        val currentIds = currentUsersList.map { it.first }.toSet()

        currentUsersList.forEach { (id, freshLink, name) ->
            if (existingStateMap.containsKey(id)) {
                val stateToUpdate = existingStateMap[id]!!
                if (stateToUpdate.sharedLink != freshLink || stateToUpdate.connectionName != name || stateToUpdate.serverAddress != targetServerUnit.serverIp) {
                    serverDao.insertServerState(stateToUpdate.copy(
                        sharedLink = freshLink,
                        connectionName = name,
                        serverAddress = targetServerUnit.serverIp
                    ))
                }
            } else {
                val newState = ServerState(
                    configId = id,
                    serverId = targetServerUnit.serverId,
                    connectionName = name,
                    serverAddress = targetServerUnit.serverIp,
                    sharedLink = freshLink,
                    protocol = currentState.inbound.inboundProtocol.name.lowercase(),
                    jsonSettings = ""
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
}
