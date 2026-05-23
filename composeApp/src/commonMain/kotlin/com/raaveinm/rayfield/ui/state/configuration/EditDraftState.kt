package com.raaveinm.rayfield.ui.state.configuration

import com.raaveinm.rayfield.data.xray.Configurations
import com.raaveinm.rayfield.data.xray.XrayConfig
import com.raaveinm.rayfield.data.xray.types.XrayKeyPair
import kotlinx.serialization.json.JsonObject

//
// Created by Kirill "Raaveinm" on 5/11/26.
//

data class EditDraftState (
    val connectionName: String = "",
    val serverId: String = "",
    val serverAddress: String = "",
    val inbound: InboundDraftState = InboundDraftState(),
    val stream: StreamDraftState = StreamDraftState(),
    val outbound: OutboundDraftState = OutboundDraftState(),
    val pro: ProDraftState = ProDraftState(),
    val isLoading: Boolean = true,
    val isSaved: Boolean = false
)

data class InboundDraftState(
    val inboundListen: String = "0.0.0.0",
    val inboundPort: Int = 443,
    val inboundProtocol: Configurations.inboundProtocol = Configurations.inboundProtocol.VLESS,
    val inboundId: String = "",
    val shadowsocksPassword: String? = null,
    val shadowsocksMethod: Configurations.shadowsocksMethod? = null,
    val shadowsocksNetwork: Configurations.shadowSocksNetwork = Configurations.shadowSocksNetwork.TCP_UDP,
    val shadowsocksEmail: String = "love@xray.com",
    val shadowsocksUsers: List<XrayConfig.ShadowsocksUser> = emptyList(),
    val vmessAlterId: Int = 0,
    val trojanPassword: String? = null,
    val fallbackDest: Int = 0,
    val isShadowsocksFallback: Boolean = false,
    val vlessDecryption: Configurations.vlessDecryption = Configurations.vlessDecryption.NONE,
    val settings: XrayConfig.InboundSettings = XrayConfig.VlessInboundSettings(),
    val streamSettings: StreamDraftState = StreamDraftState(),
    val tag: String? = null,
    val sniffing: XrayConfig.SniffingObject? = null
)

data class StreamDraftState(
    val network: Configurations.transportNetwork = Configurations.transportNetwork.TCP,
    val security: Configurations.security = Configurations.security.REALITY,
    val tlsSettings: XrayConfig.TlsSettings? = null,
    val realitySettings: XrayConfig.RealitySettings? = null,
    val xhttpSettings: XrayConfig.XhttpSettings? = null,
    val realityKeyPair: XrayKeyPair? = null,
    val realityPrivateKey: String = "",
    val realityPublicKey: String = "",
    val realityShortId: String = "",
    val fingerprint: Configurations.fingerprint = Configurations.fingerprint.CHROME
)

data class OutboundDraftState(
    val sendThrough: String? = null,
    val tag: String? = null,
    val protocol: Configurations.protocol = Configurations.protocol.FREEDOM,
    val shadowsocksMethod: Configurations.shadowsocksMethod? = null,
    val settings: JsonObject? = null,
    val streamSettings: StreamDraftState? = null,
    val proxySettings: XrayConfig.ProxySettings? = null,
    val mux: XrayConfig.MuxConfig? = null,
    val targetStrategy: Configurations.targetStrategy? = null
)

data class ProDraftState(
    val log: XrayConfig.LogConfig = XrayConfig.LogConfig(),
    val dns: XrayConfig.DnsConfig = XrayConfig.DnsConfig(),
    val fakedns: List<XrayConfig.FakeDnsConfig> = emptyList(),
    val routing: XrayConfig.RoutingConfig = XrayConfig.RoutingConfig()
)

sealed interface EditIntent {
    data class UpdateName(val name: String) : EditIntent
    object Save : EditIntent
    object Cancel : EditIntent

    // --- Inbound Updates ---
    data class UpdateInboundPort(val port: Int) : EditIntent
    data class UpdateInboundListen(val listen: String) : EditIntent
    data class UpdateInboundProtocol(val protocol: Configurations.inboundProtocol) : EditIntent
    data class UpdateInboundId(val id: String) : EditIntent
    data class UpdateShadowsocksPassword(val password: String) : EditIntent
    data class UpdateShadowsocksMethod(val method: Configurations.shadowsocksMethod) : EditIntent
    data class UpdateShadowsocksNetwork(val network: Configurations.shadowSocksNetwork) : EditIntent
    data class UpdateShadowsocksEmail(val email: String) : EditIntent
    data class UpdateShadowsocksUsers(val users: List<XrayConfig.ShadowsocksUser>) : EditIntent
    data class UpdateVmessAlterId(val alterId: Int) : EditIntent
    data class UpdateTrojanPassword(val password: String) : EditIntent
    data class UpdateFallbackDest(val port: Int) : EditIntent
    data class UpdateShadowsocksFallback(val enabled: Boolean) : EditIntent
    data class UpdateVlessDecryption(val decryption: Configurations.vlessDecryption) : EditIntent

    // --- Outbound Updates ---
    data class UpdateOutboundProtocol(val protocol: Configurations.protocol) : EditIntent
    data class UpdateOutboundShadowsocksMethod(val method: Configurations.shadowsocksMethod) : EditIntent

    // --- Pro Updates ---
    data class UpdateLogLevel(val level: Configurations.loglevel) : EditIntent
    data class UpdateDnsLogEnabled(val enabled: Boolean) : EditIntent
    data class UpdateRoutingDomainStrategy(val strategy: Configurations.routingDomainStrategy) : EditIntent

    // --- Bulk Updates ---
    data class UpdateInbound(val inbound: InboundDraftState) : EditIntent
    data class UpdateStream(val stream: StreamDraftState) : EditIntent
    data class UpdateOutbound(val outbound: OutboundDraftState) : EditIntent
    data class UpdatePro(val pro: ProDraftState) : EditIntent

    data class UpdateStreamNetwork(val network: Configurations.transportNetwork) : EditIntent
    data class UpdateStreamSecurity(val security: Configurations.security) : EditIntent
    data class UpdateRealityFingerprint(val fingerprint: Configurations.fingerprint) : EditIntent
    data class UpdateTlsFingerprint(val fingerprint: Configurations.fingerprint) : EditIntent
    data class UpdateRealityTarget(val target: Configurations.targetOptions) : EditIntent
}
