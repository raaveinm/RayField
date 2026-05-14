package com.raaveinm.rayfield.ui.state.configuration

import com.raaveinm.rayfield.data.xray.Configurations

//
// Created by Kirill "Raaveinm" on 5/11/26.
//

data class EditDraftState (
    val configId: String,
    val connectionName: String,
    val serverId: String,
    val serverAddress: String = "",
    val inbound: InboundDraftState,
    val stream: StreamDraftState,
    val outbound: OutboundDraftState,
    val pro: ProDraftState,
    val isLoading: Boolean = true,
    val isSaved: Boolean = false
)

data class InboundDraftState(
    val inboundProtocol: Configurations.protocol,
    val inboundPort: Int,
    val inboundListen: String,
    val inboundId: String? = null,
    val flow: Configurations.flow? = null,
    val shadowsocksMethod: Configurations.shadowsocksMethod? = null,
    val shadowsocksPassword: String? = null,
    val trojanPassword: String? = null,
    val vmessAlterId: Int? = null,
    val fallbackDest: Int
)
data class StreamDraftState(
    val streamNetwork: Configurations.network,
    val streamSecurity: Configurations.security,
    val wsPath: String? = null,
    val wsHost: String? = null,
    val grpcServiceName: String? = null,
    val kcpSeed: String? = null,
    val sniDest: String? = null,
    val tlsMinVersion: String? = null,
    val tlsAlpn: List<String>? = null,
    val tlsFingerprint: Configurations.fingerprint? = null,
    val realityKeyPair: com.raaveinm.rayfield.data.xray.types.XrayKeyPair? = null,
    val realityPublicKey: String? = null,
    val realityPrivateKey: String? = null,
    val realityShortId: String? = null,
    val realitySpiderX: String? = null
)
data class OutboundDraftState(
    val outboundType: Configurations.protocol,
    val domainStrategy: Configurations.domainStrategy? = null,
    val nextHopAddress: String? = null,
    val nextHopPort: Int? = null,
    val nextHopCredentials: String? = null,
    val nextHopSecurity: Configurations.security? = null,
    val nextHopSni: String? = null,
    val wgSecretKey: String? = null,
    val wgLocalAddress: String? = null,
    val wgPeerPublicKey: String? = null,
    val wgEndpoint: String? = null
)
data class ProDraftState(
    val blockAds: Boolean,
    val bypassLocalGeo: Boolean,
    val dnsPrimary: String,
    val enableFakedns: Boolean,
    val muxEnabled: Boolean,
    val muxConcurrency: Int,
    val sniffingEnabled: Boolean,
    val logLevel: Configurations.loglevel,
    val enableIp: Boolean,
    val enableMetrics: Boolean
)

sealed interface EditIntent {
    data class UpdateName(val name: String) : EditIntent
    object Save : EditIntent

    // --- Bulk Updates ---
    data class UpdateInbound(val inbound: InboundDraftState) : EditIntent
    data class UpdateStream(val stream: StreamDraftState) : EditIntent
    data class UpdateOutbound(val outbound: OutboundDraftState) : EditIntent
    data class UpdatePro(val pro: ProDraftState) : EditIntent

    // --- Inbound Granular Updates ---
    data class UpdateInboundProtocol(val protocol: Configurations.protocol) : EditIntent
    data class UpdateInboundPort(val port: Int) : EditIntent
    data class UpdateInboundListen(val listen: String) : EditIntent
    data class UpdateInboundId(val id: String?) : EditIntent
    data class UpdateInboundFlow(val flow: Configurations.flow?) : EditIntent
    data class UpdateShadowsocksMethod(val method: Configurations.shadowsocksMethod?) : EditIntent
    data class UpdateShadowsocksPassword(val password: String?) : EditIntent
    data class UpdateTrojanPassword(val password: String?) : EditIntent
    data class UpdateVmessAlterId(val alterId: Int?) : EditIntent
    data class UpdateFallbackDest(val dest: Int) : EditIntent

    // --- Stream Granular Updates ---
    data class UpdateStreamNetwork(val network: Configurations.network) : EditIntent
    data class UpdateStreamSecurity(val security: Configurations.security) : EditIntent
    data class UpdateWsPath(val path: String?) : EditIntent
    data class UpdateWsHost(val host: String?) : EditIntent
    data class UpdateGrpcServiceName(val name: String?) : EditIntent
    data class UpdateKcpSeed(val seed: String?) : EditIntent
    data class UpdateSniDest(val dest: String?) : EditIntent
    data class UpdateTlsMinVersion(val version: String?) : EditIntent
    data class UpdateTlsAlpn(val alpn: List<String>?) : EditIntent
    data class UpdateTlsFingerprint(val fingerprint: Configurations.fingerprint?) : EditIntent
    data class UpdateRealityPublicKey(val key: String?) : EditIntent
    data class UpdateRealityPrivateKey(val key: String?) : EditIntent
    data class UpdateRealityShortId(val id: String?) : EditIntent
    data class UpdateRealitySpiderX(val spiderX: String?) : EditIntent

    // --- Outbound Granular Updates ---
    data class UpdateOutboundType(val type: Configurations.protocol) : EditIntent
    data class UpdateDomainStrategy(val strategy: Configurations.domainStrategy?) : EditIntent
    data class UpdateNextHopAddress(val address: String?) : EditIntent
    data class UpdateNextHopPort(val port: Int?) : EditIntent
    data class UpdateNextHopCredentials(val credentials: String?) : EditIntent
    data class UpdateNextHopSecurity(val security: Configurations.security?) : EditIntent
    data class UpdateNextHopSni(val sni: String?) : EditIntent
    data class UpdateWgSecretKey(val key: String?) : EditIntent
    data class UpdateWgLocalAddress(val address: String?) : EditIntent
    data class UpdateWgPeerPublicKey(val key: String?) : EditIntent
    data class UpdateWgEndpoint(val endpoint: String?) : EditIntent

    // --- Pro Granular Updates ---
    data class UpdateBlockAds(val enabled: Boolean) : EditIntent
    data class UpdateBypassLocalGeo(val enabled: Boolean) : EditIntent
    data class UpdateDnsPrimary(val dns: String) : EditIntent
    data class UpdateEnableFakedns(val enabled: Boolean) : EditIntent
    data class UpdateMuxEnabled(val enabled: Boolean) : EditIntent
    data class UpdateMuxConcurrency(val concurrency: Int) : EditIntent
    data class UpdateSniffingEnabled(val enabled: Boolean) : EditIntent
    data class UpdateLogLevel(val level: Configurations.loglevel) : EditIntent
    data class UpdateEnableIp(val enabled: Boolean) : EditIntent
    data class UpdateEnableMetrics(val enabled: Boolean) : EditIntent
}