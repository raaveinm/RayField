package com.raaveinm.rayfield.ui.state.configuration

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.raaveinm.rayfield.data.database.ServerDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.raaveinm.rayfield.domain.xray.CypherService
import com.raaveinm.rayfield.data.xray.Configurations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

//
// Created by Kirill "Raaveinm" on 5/11/26.
//

class EditScreenModel(
    val serverDao: ServerDao,
    private val cypherService: CypherService,
    private val initialConfigId: String? = null
) : ScreenModel {
    private val _state = MutableStateFlow(
        EditDraftState(
            connectionName = "",
            inbound = InboundDraftState(
                inboundProtocol = Configurations.protocol.VLESS,
                inboundPort = 443,
                inboundListen = "0.0.0.0",
                inboundId = ""
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
            isLoading = initialConfigId != null
        )
    )
    val state = _state.asStateFlow()

    var uuid = MutableStateFlow("")
    var privateKey = MutableStateFlow("")
    var publicKey = MutableStateFlow("")
    var shortId = MutableStateFlow("")

    init {
        if (initialConfigId == null)
            initNewConfig()

        screenModelScope.launch {
            if (!initialConfigId.isNullOrBlank()) {
                val config = serverDao.getConfigById(initialConfigId)
                if (config != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        // TODO(deserialize config)
                    }
                }
            }
        }
    }

    fun processIntent(intent: EditIntent) {
        when (intent) {
            is EditIntent.UpdateName -> _state.update { it.copy(connectionName = intent.name) }

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

    fun initNewConfig() {
        generateUuid()
        generateRealityKeys()
        generateShortId()
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