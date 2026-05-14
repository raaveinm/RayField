package com.raaveinm.rayfield.ui.state.configuration

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.raaveinm.rayfield.data.database.ServerDao
import com.raaveinm.rayfield.data.xray.Configurations
import com.raaveinm.rayfield.data.xray.XrayConfig
import com.raaveinm.rayfield.data.xray.types.ServerState
import com.raaveinm.rayfield.domain.helpers.Logger
import com.raaveinm.rayfield.domain.xray.CypherService
import com.raaveinm.rayfield.domain.xray.ShareLinkGenerator
import com.raaveinm.rayfield.domain.xray.XrayConfigBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.decodeFromJsonElement

//
// Created by Kirill "Raaveinm" on 5/11/26.
//

class EditScreenModel(
    val serverDao: ServerDao,
    private val cypherService: CypherService,
    private val initialConfigId: String? = null,
    private val initialServerId: String? = null
) : ScreenModel {
    private val _state = MutableStateFlow(
        EditDraftState(
            configId = initialConfigId ?: "",
            connectionName = "",
            serverId = initialServerId ?: "",
            serverAddress = "",
            inbound = InboundDraftState(
                inboundProtocol = Configurations.protocol.VLESS,
                inboundPort = 443,
                inboundListen = "0.0.0.0",
                inboundId = "",
                flow = null,
                fallbackDest = 8080
            ),
            stream = StreamDraftState(
                streamNetwork = Configurations.network.TCP,
                streamSecurity = Configurations.security.NONE,
                realityPublicKey = "",
                realityPrivateKey = "",
                realityShortId = "",
                realitySpiderX = "/"
            ),
            outbound = OutboundDraftState(
                outboundType = Configurations.protocol.FREEDOM
            ),
            pro = ProDraftState(
                blockAds = false,
                bypassLocalGeo = false,
                dnsPrimary = "1.1.1.1",
                enableFakedns = false,
                muxEnabled = false,
                muxConcurrency = 8,
                sniffingEnabled = true,
                logLevel = Configurations.loglevel.WARNING,
                enableIp = false,
                enableMetrics = false
            ),
            isLoading = initialConfigId != null || initialServerId != null
        )
    )
    val state = _state.asStateFlow()

    var uuid = MutableStateFlow("")
    var privateKey = MutableStateFlow("")
    var publicKey = MutableStateFlow("")
    var shortId = MutableStateFlow("")

    init {
        screenModelScope.launch {
            if (!initialConfigId.isNullOrBlank()) {
                val serverStateEntity = serverDao.getConfigById(initialConfigId)
                if (serverStateEntity != null) {
                    _state.update { it.copy(
                        serverId = serverStateEntity.serverId,
                        serverAddress = serverStateEntity.serverAddress,
                        connectionName = serverStateEntity.connectionName ?: ""
                    ) }
                    try {
                        val xrayConfig = XrayConfigBuilder.parseJson(serverStateEntity.jsonSettings)
                        val inbound = xrayConfig.inbounds.firstOrNull()
                        val outbound = xrayConfig.outbounds.firstOrNull()
                        val stream = inbound?.streamSettings

                        val json = XrayConfigBuilder.jsonFormatter

                        val inboundDraft = inbound?.let { ib ->
                            val settings = when (ib.protocol) {
                                Configurations.protocol.VLESS -> json.decodeFromJsonElement<XrayConfig.VlessInboundSettings>(ib.settings)
                                Configurations.protocol.VMESS -> json.decodeFromJsonElement<XrayConfig.VMessInboundSettings>(ib.settings)
                                Configurations.protocol.TROJAN -> json.decodeFromJsonElement<XrayConfig.TrojanInboundSettings>(ib.settings)
                                Configurations.protocol.SHADOWSOCKS -> json.decodeFromJsonElement<XrayConfig.ShadowsocksInboundSettings>(ib.settings)
                                else -> null
                            }

                            val clientId = when (settings) {
                                is XrayConfig.VlessInboundSettings -> settings.clients.firstOrNull()?.id
                                is XrayConfig.VMessInboundSettings -> settings.clients.firstOrNull()?.id
                                else -> null
                            }

                            val flow = (settings as? XrayConfig.VlessInboundSettings)?.clients?.firstOrNull()?.flow

                            InboundDraftState(
                                inboundProtocol = ib.protocol,
                                inboundPort = ib.port,
                                inboundListen = ib.listen,
                                inboundId = clientId ?: "",
                                flow = flow,
                                vmessAlterId = (settings as? XrayConfig.VMessInboundSettings)?.clients?.firstOrNull()?.alterId,
                                shadowsocksMethod = (settings as? XrayConfig.ShadowsocksInboundSettings)?.method?.let { m ->
                                    Configurations.shadowsocksMethod.entries.find { it.name.lowercase().replace("_", "-") == m }
                                },
                                shadowsocksPassword = (settings as? XrayConfig.ShadowsocksInboundSettings)?.password,
                                trojanPassword = (settings as? XrayConfig.TrojanInboundSettings)?.clients?.firstOrNull()?.password,
                                fallbackDest = (settings as? XrayConfig.VlessInboundSettings)?.fallbacks?.firstOrNull()?.dest?.toIntOrNull() ?: 8080
                            )
                        } ?: _state.value.inbound

                        val streamDraft = stream?.let { s ->
                            StreamDraftState(
                                streamNetwork = s.network ?: Configurations.network.TCP,
                                streamSecurity = s.security ?: Configurations.security.NONE,
                                wsPath = s.wsSettings?.path,
                                wsHost = s.wsSettings?.headers?.get("Host"),
                                grpcServiceName = s.grpcSettings?.serviceName,
                                kcpSeed = s.kcpSettings?.seed,
                                sniDest = s.realitySettings?.dest,
                                tlsMinVersion = s.tlsSettings?.minVersion,
                                tlsAlpn = s.tlsSettings?.alpn,
                                tlsFingerprint = s.tlsSettings?.fingerprint,
                                realityKeyPair = s.realitySettings?.privateKey?.let { com.raaveinm.rayfield.data.xray.types.XrayKeyPair(publicKey = "", privateKey = it) },
                                realityPublicKey = "",
                                realityPrivateKey = s.realitySettings?.privateKey,
                                realityShortId = s.realitySettings?.shortIds?.firstOrNull(),
                                realitySpiderX = s.realitySettings?.spiderX
                            )
                        } ?: _state.value.stream

                        val outboundDraft = outbound?.let { ob ->
                            OutboundDraftState(
                                outboundType = ob.protocol,
                                domainStrategy = xrayConfig.routing?.domainStrategy
                            )
                        } ?: _state.value.outbound

                        _state.update {
                            it.copy(
                                connectionName = serverStateEntity.connectionName ?: "",
                                inbound = inboundDraft,
                                stream = streamDraft,
                                outbound = outboundDraft,
                                pro = it.pro.copy(
                                    blockAds = xrayConfig.routing?.rules?.any { rule -> rule.domain?.contains("geosite:category-ads-all") == true } ?: false,
                                    logLevel = xrayConfig.log?.loglevel ?: Configurations.loglevel.WARNING,
                                    sniffingEnabled = inbound?.sniffing?.enabled ?: true
                                ),
                                isLoading = false
                            )
                        }
                    } catch (e: Exception) {
                        _state.update {
                            Logger.e("EditScreenModel", e.message ?: "Unknown error")
                            it.copy(isLoading = false)
                        }
                    }
                }
            } else if (!initialServerId.isNullOrBlank()) {
                val server = serverDao.getServerUnitById(initialServerId)
                if (server != null) {
                    _state.update { it.copy(
                        serverId = server.serverId,
                        serverAddress = server.serverIp,
                        isLoading = false
                    ) }
                }
            } else {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun processIntent(intent: EditIntent) {
        when (intent) {
            is EditIntent.UpdateName -> _state.update { it.copy(connectionName = intent.name) }
            EditIntent.Save -> screenModelScope.launch(Dispatchers.IO) { saveServer() }

            // Bulk Updates
            is EditIntent.UpdateInbound -> _state.update { it.copy(inbound = intent.inbound) }
            is EditIntent.UpdateStream -> _state.update { it.copy(stream = intent.stream) }
            is EditIntent.UpdateOutbound -> _state.update { it.copy(outbound = intent.outbound) }
            is EditIntent.UpdatePro -> _state.update { it.copy(pro = intent.pro) }

            // Inbound Updates
            is EditIntent.UpdateInboundProtocol -> _state.update { it.copy(inbound = it.inbound.copy(inboundProtocol = intent.protocol)) }
            is EditIntent.UpdateInboundPort -> _state.update { it.copy(inbound = it.inbound.copy(inboundPort = intent.port)) }
            is EditIntent.UpdateInboundListen -> _state.update { it.copy(inbound = it.inbound.copy(inboundListen = intent.listen)) }
            is EditIntent.UpdateInboundId -> _state.update { it.copy(inbound = it.inbound.copy(inboundId = intent.id)) }
            is EditIntent.UpdateInboundFlow -> _state.update { it.copy(inbound = it.inbound.copy(flow = intent.flow)) }
            is EditIntent.UpdateShadowsocksMethod -> _state.update { it.copy(inbound = it.inbound.copy(shadowsocksMethod = intent.method)) }
            is EditIntent.UpdateShadowsocksPassword -> _state.update { it.copy(inbound = it.inbound.copy(shadowsocksPassword = intent.password)) }
            is EditIntent.UpdateTrojanPassword -> _state.update { it.copy(inbound = it.inbound.copy(trojanPassword = intent.password)) }
            is EditIntent.UpdateVmessAlterId -> _state.update { it.copy(inbound = it.inbound.copy(vmessAlterId = intent.alterId)) }
            is EditIntent.UpdateFallbackDest -> _state.update { it.copy(inbound = it.inbound.copy(fallbackDest = intent.dest)) }

            // Stream Updates
            is EditIntent.UpdateStreamNetwork -> _state.update { it.copy(stream = it.stream.copy(streamNetwork = intent.network)) }
            is EditIntent.UpdateStreamSecurity -> _state.update { it.copy(stream = it.stream.copy(streamSecurity = intent.security)) }
            is EditIntent.UpdateWsPath -> _state.update { it.copy(stream = it.stream.copy(wsPath = intent.path)) }
            is EditIntent.UpdateWsHost -> _state.update { it.copy(stream = it.stream.copy(wsHost = intent.host)) }
            is EditIntent.UpdateGrpcServiceName -> _state.update { it.copy(stream = it.stream.copy(grpcServiceName = intent.name)) }
            is EditIntent.UpdateKcpSeed -> _state.update { it.copy(stream = it.stream.copy(kcpSeed = intent.seed)) }
            is EditIntent.UpdateSniDest -> _state.update { it.copy(stream = it.stream.copy(sniDest = intent.dest)) }
            is EditIntent.UpdateTlsMinVersion -> _state.update { it.copy(stream = it.stream.copy(tlsMinVersion = intent.version)) }
            is EditIntent.UpdateTlsAlpn -> _state.update { it.copy(stream = it.stream.copy(tlsAlpn = intent.alpn)) }
            is EditIntent.UpdateTlsFingerprint -> _state.update { it.copy(stream = it.stream.copy(tlsFingerprint = intent.fingerprint)) }
            is EditIntent.UpdateRealityPublicKey -> _state.update { it.copy(stream = it.stream.copy(realityPublicKey = intent.key)) }
            is EditIntent.UpdateRealityPrivateKey -> _state.update { it.copy(stream = it.stream.copy(realityPrivateKey = intent.key)) }
            is EditIntent.UpdateRealityShortId -> _state.update { it.copy(stream = it.stream.copy(realityShortId = intent.id)) }
            is EditIntent.UpdateRealitySpiderX -> _state.update { it.copy(stream = it.stream.copy(realitySpiderX = intent.spiderX)) }

            // Outbound Updates
            is EditIntent.UpdateOutboundType -> _state.update { it.copy(outbound = it.outbound.copy(outboundType = intent.type)) }
            is EditIntent.UpdateDomainStrategy -> _state.update { it.copy(outbound = it.outbound.copy(domainStrategy = intent.strategy)) }
            is EditIntent.UpdateNextHopAddress -> _state.update { it.copy(outbound = it.outbound.copy(nextHopAddress = intent.address)) }
            is EditIntent.UpdateNextHopPort -> _state.update { it.copy(outbound = it.outbound.copy(nextHopPort = intent.port)) }
            is EditIntent.UpdateNextHopCredentials -> _state.update { it.copy(outbound = it.outbound.copy(nextHopCredentials = intent.credentials)) }
            is EditIntent.UpdateNextHopSecurity -> _state.update { it.copy(outbound = it.outbound.copy(nextHopSecurity = intent.security)) }
            is EditIntent.UpdateNextHopSni -> _state.update { it.copy(outbound = it.outbound.copy(nextHopSni = intent.sni)) }
            is EditIntent.UpdateWgSecretKey -> _state.update { it.copy(outbound = it.outbound.copy(wgSecretKey = intent.key)) }
            is EditIntent.UpdateWgLocalAddress -> _state.update { it.copy(outbound = it.outbound.copy(wgLocalAddress = intent.address)) }
            is EditIntent.UpdateWgPeerPublicKey -> _state.update { it.copy(outbound = it.outbound.copy(wgPeerPublicKey = intent.key)) }
            is EditIntent.UpdateWgEndpoint -> _state.update { it.copy(outbound = it.outbound.copy(wgEndpoint = intent.endpoint)) }

            // Pro Updates
            is EditIntent.UpdateBlockAds -> _state.update { it.copy(pro = it.pro.copy(blockAds = intent.enabled)) }
            is EditIntent.UpdateBypassLocalGeo -> _state.update { it.copy(pro = it.pro.copy(bypassLocalGeo = intent.enabled)) }
            is EditIntent.UpdateDnsPrimary -> _state.update { it.copy(pro = it.pro.copy(dnsPrimary = intent.dns)) }
            is EditIntent.UpdateEnableFakedns -> _state.update { it.copy(pro = it.pro.copy(enableFakedns = intent.enabled)) }
            is EditIntent.UpdateMuxEnabled -> _state.update { it.copy(pro = it.pro.copy(muxEnabled = intent.enabled)) }
            is EditIntent.UpdateMuxConcurrency -> _state.update { it.copy(pro = it.pro.copy(muxConcurrency = intent.concurrency)) }
            is EditIntent.UpdateSniffingEnabled -> _state.update { it.copy(pro = it.pro.copy(sniffingEnabled = intent.enabled)) }
            is EditIntent.UpdateLogLevel -> _state.update { it.copy(pro = it.pro.copy(logLevel = intent.level)) }
            is EditIntent.UpdateEnableIp -> _state.update { it.copy(pro = it.pro.copy(enableIp = intent.enabled)) }
            is EditIntent.UpdateEnableMetrics -> _state.update { it.copy(pro = it.pro.copy(enableMetrics = intent.enabled)) }
        }
    }

    suspend fun saveServer() {
        val currentState = state.value
        if (currentState.inbound.inboundPort !in 1..65535) return
        
        val configId = currentState.configId.ifEmpty { cypherService.generateUuid() }

        val inboundSettings = when (currentState.inbound.inboundProtocol) {
            Configurations.protocol.VLESS -> XrayConfigBuilder.vlessInboundSettings(
                uuid = if (currentState.inbound.inboundId.isNullOrBlank()) cypherService.generateUuid() else currentState.inbound.inboundId,
                flow = currentState.inbound.flow,
                decryption = Configurations.decryption.NONE,
                fallbacks = listOf(XrayConfig.Fallback(dest = currentState.inbound.fallbackDest.toString()))
            )
            Configurations.protocol.VMESS -> XrayConfigBuilder.vmessInboundSettings(
                uuid = if (currentState.inbound.inboundId.isNullOrBlank()) cypherService.generateUuid() else currentState.inbound.inboundId,
                alterId = currentState.inbound.vmessAlterId ?: 0
            )
            Configurations.protocol.TROJAN -> XrayConfigBuilder.trojanInboundSettings(
                password = currentState.inbound.trojanPassword ?: ""
            )
            Configurations.protocol.SHADOWSOCKS -> XrayConfigBuilder.shadowsocksInboundSettings(
                method = currentState.inbound.shadowsocksMethod ?: Configurations.shadowsocksMethod.AES_256_GCM,
                password = currentState.inbound.shadowsocksPassword ?: ""
            )
            else -> XrayConfigBuilder.toSettings(null)
        }

        val streamSettings = XrayConfig.StreamSettings(
            network = currentState.stream.streamNetwork,
            security = currentState.stream.streamSecurity,
            realitySettings = if (currentState.stream.streamSecurity == Configurations.security.REALITY) {
                val serverNames = currentState.stream.sniDest?.split(":")?.firstOrNull()?.let { listOf(it) } ?: emptyList()
                XrayConfig.RealitySettings(
                    dest = currentState.stream.sniDest ?: "",
                    serverNames = serverNames,
                    privateKey = currentState.stream.realityPrivateKey ?: "",
                    shortIds = listOf(currentState.stream.realityShortId ?: ""),
                    spiderX = currentState.stream.realitySpiderX
                )
            } else null,
            wsSettings = if (currentState.stream.streamNetwork == Configurations.network.WS) {
                XrayConfig.WsSettings(
                    path = currentState.stream.wsPath ?: "/",
                    headers = currentState.stream.wsHost?.let { mapOf("Host" to it) }
                )
            } else null,
            grpcSettings = if (currentState.stream.streamNetwork == Configurations.network.GRPC) {
                XrayConfig.GrpcSettings(
                    serviceName = currentState.stream.grpcServiceName ?: ""
                )
            } else null,
            tlsSettings = if (currentState.stream.streamSecurity == Configurations.security.TLS) {
                XrayConfig.TlsSettings(
                    serverName = currentState.stream.wsHost,
                    fingerprint = currentState.stream.tlsFingerprint,
                    alpn = currentState.stream.tlsAlpn,
                    minVersion = currentState.stream.tlsMinVersion
                )
            } else null
        )

        val xrayConfig = XrayConfig(
            inbounds = listOf(
                XrayConfig.InboundConfig(
                    tag = "proxy",
                    listen = currentState.inbound.inboundListen,
                    port = currentState.inbound.inboundPort,
                    protocol = currentState.inbound.inboundProtocol,
                    settings = inboundSettings,
                    streamSettings = streamSettings,
                    sniffing = XrayConfig.SniffingConfig(enabled = currentState.pro.sniffingEnabled)
                )
            ),
            outbounds = listOf(
                XrayConfig.OutboundConfig(
                    protocol = currentState.outbound.outboundType,
                    tag = "direct"
                ),
                XrayConfig.OutboundConfig(
                    protocol = Configurations.protocol.BLACKHOLE,
                    tag = "block",
                    settings = XrayConfigBuilder.blackholeOutboundSettings()
                )
            ),
            routing = XrayConfigBuilder.defaultRoutingConfig(
                blockAds = currentState.pro.blockAds
            ),
            log = XrayConfig.LogConfig(loglevel = currentState.pro.logLevel)
        )

        // Generate shared link (using dummy outbound for generator)
        val dummyOutbound = XrayConfig.OutboundConfig(
            tag = currentState.connectionName,
            protocol = currentState.inbound.inboundProtocol,
            settings = when (currentState.inbound.inboundProtocol) {
                Configurations.protocol.VLESS -> XrayConfigBuilder.vlessOutboundSettings(
                    address = currentState.serverAddress,
                    port = currentState.inbound.inboundPort,
                    uuid = currentState.inbound.inboundId ?: ""
                )
                Configurations.protocol.VMESS -> XrayConfigBuilder.vmessOutboundSettings(
                    address = currentState.serverAddress,
                    port = currentState.inbound.inboundPort,
                    uuid = currentState.inbound.inboundId ?: ""
                )
                Configurations.protocol.TROJAN -> XrayConfigBuilder.trojanOutboundSettings(
                    address = currentState.serverAddress,
                    port = currentState.inbound.inboundPort,
                    password = currentState.inbound.trojanPassword ?: ""
                )
                Configurations.protocol.SHADOWSOCKS -> XrayConfigBuilder.shadowsocksOutboundSettings(
                    address = currentState.serverAddress,
                    port = currentState.inbound.inboundPort,
                    method = currentState.inbound.shadowsocksMethod?.name?.lowercase()?.replace("_", "-") ?: "aes-256-gcm",
                    password = currentState.inbound.shadowsocksPassword ?: ""
                )
                else -> null
            },
            streamSettings = streamSettings.copy(
                realitySettings = streamSettings.realitySettings?.copy(privateKey = currentState.stream.realityPublicKey ?: "")
            )
        )

        val serverState = ServerState(
            configId = configId,
            serverId = currentState.serverId,
            connectionName = currentState.connectionName,
            serverAddress = currentState.serverAddress,
            sharedLink = ShareLinkGenerator().generateLink(dummyOutbound),
            protocol = currentState.inbound.inboundProtocol.name,
            jsonSettings = XrayConfigBuilder.buildJson(xrayConfig)
        )
        serverDao.insertServerState(serverState)
        _state.update { it.copy(isSaved = true, configId = configId) }
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