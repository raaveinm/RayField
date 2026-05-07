@file:Suppress("PropertyName")

package com.raaveinm.rayfield.data.xray

//
// Created by Kirill "Raaveinm" on 4/29/26.
//

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

@Serializable
data class XrayConfig(
    val log: LogConfig? = null,
    val api: ApiConfig? = null,
    val dns: DnsConfig? = null,
    val routing: RoutingConfig? = null,
    val policy: PolicyConfig? = null,
    val inbounds: List<InboundConfig> = emptyList(),
    val outbounds: List<OutboundConfig> = emptyList(),
    val transport: TransportConfig? = null,
    val fakedns: List<FakeDnsConfig>? = null,
    val reverse: ReverseConfig? = null,
    val observatory: ObservatoryConfig? = null,
    val metrics: MetricsConfig? = null,
    val stats: JsonObject? = null
) {
    @Serializable
    data class LogConfig(
        val access: String? = null,
        val error: String? = null,
        val loglevel: Configurations.loglevel? = null,
        val dnsLog: Boolean? = null,
        val maskAddress: String? = null
    )

    @Serializable
    data class ApiConfig(
        val tag: String = "api",
        val services: List<String> = listOf("HandlerService", "LoggerService", "StatsService")
    )

    @Serializable
    data class RoutingConfig(
        val domainStrategy: Configurations.domainStrategy? = null,
        val domainMatcher: String? = null, // "linear" or "mph"
        val rules: List<RoutingRule> = emptyList(),
        val balancers: List<RoutingBalancer>? = null
    )

    @Serializable
    data class RoutingRule(
        val type: Configurations.ruleType? = null,
        val inboundTag: List<String>? = null,
        val outboundTag: String? = null,
        val balancerTag: String? = null,
        val domain: List<String>? = null,
        val ip: List<String>? = null,
        val port: String? = null,
        val sourcePort: String? = null,
        val network: Configurations.routingNetwork? = null,
        val protocol: List<String>? = null,
        val user: List<String>? = null,
        val attrs: String? = null
    )

    @Serializable
    data class RoutingBalancer(
        val tag: String,
        val selector: List<String>,
        val strategy: BalancerStrategy? = null
    )

    @Serializable
    data class BalancerStrategy(
        val type: String = "random" // "random", "roundRobin", "leastPing"
    )

    @Serializable
    data class InboundConfig(
        val tag: String,
        val listen: String = "0.0.0.0",
        val port: Int,
        val protocol: Configurations.protocol,
        val settings: JsonObject,
        val streamSettings: StreamSettings? = null,
        val sniffing: SniffingConfig? = null,
        val allocate: AllocateConfig? = null
    )

    @Serializable
    data class AllocateConfig(
        val strategy: String = "always", // "always", "random"
        val refresh: Int = 5,
        val concurrency: Int = 3
    )

    @Serializable
    data class OutboundConfig(
        val tag: String? = null,
        val protocol: Configurations.protocol,
        val settings: JsonObject? = null,
        val streamSettings: StreamSettings? = null,
        val proxySettings: ProxySettings? = null,
        val mux: MuxConfig? = null
    )

    @Serializable
    data class ProxySettings(
        val tag: String? = null,
        val transportLayer: Boolean = false
    )

    @Serializable
    data class StreamSettings(
        val network: Configurations.network? = null,
        val security: Configurations.security? = null,
        val realitySettings: RealitySettings? = null,
        val tcpSettings: TcpSettings? = null,
        val kcpSettings: KcpSettings? = null,
        val wsSettings: WsSettings? = null,
        val httpSettings: HttpSettings? = null,
        val quicSettings: QuicSettings? = null,
        val grpcSettings: GrpcSettings? = null,
        val tlsSettings: TlsSettings? = null,
        val sockopt: Sockopt? = null
    )

    @Serializable
    data class RealitySettings(
        val show: Boolean? = null,
        val dest: String,
        val xver: Int? = null,
        val serverNames: List<String>,
        val privateKey: String,
        val shortIds: List<String>,
        val spiderX: String? = null,
        val minClientVer: String? = null,
        val maxClientVer: String? = null,
        val maxTimeDiff: Int? = null
    )

    @Serializable
    data class TcpSettings(
        val acceptProxyProtocol: Boolean? = null,
        val header: JsonObject? = null
    )

    @Serializable
    data class KcpSettings(
        val mtu: Int? = null,
        val tti: Int? = null,
        val uplinkCapacity: Int? = null,
        val downlinkCapacity: Int? = null,
        val congestion: Boolean? = null,
        val readBufferSize: Int? = null,
        val writeBufferSize: Int? = null,
        val header: JsonObject? = null,
        val seed: String? = null
    )

    @Serializable
    data class WsSettings(
        val path: String = "/",
        val headers: Map<String, String>? = null,
        val acceptProxyProtocol: Boolean = false
    )

    @Serializable
    data class HttpSettings(
        val host: List<String>? = null,
        val path: String? = null,
        val method: String? = null,
        val headers: Map<String, String>? = null
    )

    @Serializable
    data class QuicSettings(
        val security: String? = null,
        val key: String? = null,
        val header: JsonObject? = null
    )

    @Serializable
    data class GrpcSettings(
        val serviceName: String,
        val multiMode: Boolean? = null,
        val idle_timeout: Int? = null,
        val health_check_timeout: Int? = null,
        val permit_without_stream: Boolean? = null,
        val initial_windows_size: Int? = null
    )

    @Serializable
    data class TlsSettings(
        val serverName: String? = null,
        val certificates: List<TlsCertificate>? = null,
        val fingerprint: Configurations.fingerprint? = null,
        val alpn: List<String>? = null,
        val minVersion: String? = null,
        val maxVersion: String? = null,
        val cipherSuites: String? = null,
        val allowInsecure: Boolean? = null,
        val disableSystemRoot: Boolean? = null
    )

    @Serializable
    data class Sockopt @JvmOverloads constructor(
        val mark: Int? = null,
        val tproxy: String? = null, // "redirect", "tproxy", or "off"
        val tcpFastOpen: Boolean? = null,
        val tcpKeepAliveInterval: Int? = null,
        val tcpMptcp: Boolean? = null,
        val tcpNoDelay: Boolean? = null,
        val domainStrategy: Configurations.domainStrategy? = null,
        val interface_name: String? = null
    )

    @Serializable
    data class Fallback(
        val name: String? = null,
        val alpn: String? = null,
        val path: String? = null,
        val dest: String, // String or Int (port), but usually string like "127.0.0.1:80" or "80"
        val xver: Int? = null
    )

    @Serializable
    data class VlessInboundSettings(
        val clients: List<VlessUser>,
        val decryption: Configurations.decryption = Configurations.decryption.NONE,
        val fallbacks: List<Fallback>? = null
    )

    @Serializable
    data class VlessUser(
        val id: String,
        val flow: Configurations.flow? = null,
        val email: String? = null,
        val level: Int? = null
    )

    @Serializable
    data class VMessInboundSettings(
        val clients: List<VMessUser>,
        val default: VMessDefault? = null,
        val detours: VMessDetour? = null
    )

    @Serializable
    data class VMessUser(
        val id: String,
        val alterId: Int = 0,
        val email: String? = null,
        val level: Int? = null
    )

    @Serializable
    data class VMessDefault(
        val level: Int? = null,
        val alterId: Int? = null
    )

    @Serializable
    data class VMessDetour(
        val to: String? = null
    )

    @Serializable
    data class TrojanInboundSettings(
        val clients: List<TrojanUser>,
        val fallbacks: List<Fallback>? = null
    )

    @Serializable
    data class TrojanUser(
        val password: String,
        val email: String? = null,
        val level: Int? = null
    )

    @Serializable
    data class ShadowsocksInboundSettings(
        val method: String,
        val password: String,
        val network: String? = null
    )

    @Serializable
    data class SocksInboundSettings(
        val auth: String = "noauth", // "noauth" or "password"
        val accounts: List<SocksAccount>? = null,
        val udp: Boolean = false,
        val ip: String? = null,
        val userLevel: Int = 0
    )

    @Serializable
    data class SocksAccount(
        val user: String,
        val pass: String
    )

    @Serializable
    data class DokodemoInboundSettings(
        val address: String? = null,
        val port: Int? = null,
        val network: String? = "tcp,udp",
        val followRedirect: Boolean = false,
        val userLevel: Int = 0
    )

    @Serializable
    data class DnsInboundSettings(
        val network: String? = "tcp,udp",
        val address: String? = null,
        val port: Int? = null,
        val userLevel: Int = 0
    )

    @Serializable
    data class VlessOutboundSettings(
        val vnext: List<VlessOutboundVnext>
    )

    @Serializable
    data class VlessOutboundVnext(
        val address: String,
        val port: Int,
        val users: List<VlessOutboundUser>
    )

    @Serializable
    data class VlessOutboundUser(
        val id: String,
        val encryption: Configurations.encryption = Configurations.encryption.NONE,
        val flow: Configurations.flow? = null,
        val level: Int? = null
    )

    @Serializable
    data class VMessOutboundSettings(
        val vnext: List<VMessOutboundVnext>
    )

    @Serializable
    data class VMessOutboundVnext(
        val address: String,
        val port: Int,
        val users: List<VMessOutboundUser>
    )

    @Serializable
    data class VMessOutboundUser(
        val id: String,
        val alterId: Int = 0,
        val security: String = "auto",
        val level: Int? = null
    )

    @Serializable
    data class TrojanOutboundSettings(
        val servers: List<TrojanOutboundServer>
    )

    @Serializable
    data class TrojanOutboundServer(
        val address: String,
        val port: Int,
        val password: String,
        val email: String? = null,
        val level: Int? = null
    )

    @Serializable
    data class ShadowsocksOutboundSettings(
        val servers: List<ShadowsocksOutboundServer>
    )

    @Serializable
    data class ShadowsocksOutboundServer(
        val address: String,
        val port: Int,
        val method: String,
        val password: String,
        val email: String? = null,
        val level: Int? = null
    )

    @Serializable
    data class SocksOutboundSettings(
        val servers: List<SocksOutboundServer>
    )

    @Serializable
    data class SocksOutboundServer(
        val address: String,
        val port: Int,
        val users: List<SocksOutboundUser>? = null
    )

    @Serializable
    data class SocksOutboundUser(
        val user: String,
        val pass: String,
        val level: Int? = null
    )

    @Serializable
    data class WireguardOutboundSettings(
        val secretKey: String,
        val address: List<String>,
        val peers: List<WireguardPeer>,
        val mtu: Int = 1420,
        val reserved: List<Int>? = null,
        val kernelMode: Boolean = false
    )

    @Serializable
    data class WireguardPeer(
        val publicKey: String,
        val endpoint: String? = null,
        val keepAlive: Int = 0,
        val preSharedKey: String? = null
    )

    @Serializable
    data class HysteriaOutboundSettings(
        val servers: List<HysteriaServer>,
        val auth: String? = null,
        val up_mbps: Int? = null,
        val down_mbps: Int? = null,
        val obfs: String? = null,
        val recv_window: Int? = null,
        val recv_window_conn: Int? = null
    )

    @Serializable
    data class HysteriaServer(
        val address: String,
        val port: Int
    )

    @Serializable
    data class BlackholeOutboundSettings(
        val response: BlackholeResponse? = null
    )

    @Serializable
    data class BlackholeResponse(
        val type: String = "none" // "none" or "http"
    )

    @Serializable
    data class FreedomOutboundSettings(
        val domainStrategy: Configurations.domainStrategy = Configurations.domainStrategy.AS_IS,
        val redirect: String? = null,
        val userLevel: Int = 0
    )

    @Serializable
    data class LoopbackOutboundSettings(
        val inboundTag: String
    )

    @Serializable
    data class TlsCertificate(
        val certificateFile: String,
        val keyFile: String
    )

    @Serializable
    data class SniffingConfig(
        val enabled: Boolean = true,
        val destOverride: List<String> = listOf("http", "tls", "quic", "fakedns"),
        val metadataOnly: Boolean = false,
        val routeOnly: Boolean = false
    )

    @Serializable
    data class MuxConfig(
        val enabled: Boolean = false,
        val concurrency: Int = 8,
        val xudpConcurrency: Int = 16,
        val xudpQuic: String = "quic"
    )

    @Serializable
    data class DnsConfig(
        val hosts: JsonObject? = null,
        val servers: JsonArray? = null,
        val clientIp: String? = null,
        val queryStrategy: String? = null,
        val disableCache: Boolean? = null,
        val disableFallback: Boolean? = null,
        val tag: String? = null
    )

    @Serializable
    data class PolicyConfig(
        val levels: JsonObject? = null,
        val system: JsonObject? = null
    )

    @Serializable
    data class ObservatoryConfig(
        val outboundTag: String? = null,
        val subjectSelector: List<String>? = null
    )

    @Serializable
    data class TransportConfig(
        val tcpSettings: TcpSettings? = null,
        val kcpSettings: KcpSettings? = null,
        val wsSettings: WsSettings? = null,
        val httpSettings: HttpSettings? = null,
        val quicSettings: QuicSettings? = null,
        val grpcSettings: GrpcSettings? = null
    )

    @Serializable
    data class FakeDnsConfig(
        val ipPool: String,
        val poolSize: Int = 65535
    )

    @Serializable
    data class ReverseConfig(
        val bridges: List<ReverseBridge>? = null,
        val portals: List<ReversePortal>? = null
    )

    @Serializable
    data class ReverseBridge(
        val tag: String,
        val domain: String
    )

    @Serializable
    data class ReversePortal(
        val tag: String,
        val domain: String
    )

    @Serializable
    data class MetricsConfig(
        val tag: String
    )
}
