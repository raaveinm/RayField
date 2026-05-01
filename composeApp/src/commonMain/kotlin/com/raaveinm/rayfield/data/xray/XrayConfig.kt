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
    val observatory: ObservatoryConfig? = null,
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
        val type: String? = null,
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
        val selector: List<String>
    )

    @Serializable
    data class InboundConfig(
        val tag: String,
        val listen: String = "0.0.0.0",
        val port: Int,
        val protocol: Configurations.protocol,
        val settings: JsonObject,
        val streamSettings: StreamSettings? = null,
        val sniffing: SniffingConfig? = null
    )

    @Serializable
    data class OutboundConfig(
        val tag: String? = null,
        val protocol: Configurations.protocol,
        val settings: JsonObject? = null,
        val streamSettings: StreamSettings? = null,
        val mux: MuxConfig? = null
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
        val decryption: String = "none",
        val fallbacks: List<Fallback>? = null
    )

    @Serializable
    data class VlessUser(
        val id: String,
        val flow: String? = null,
        val email: String? = null,
        val level: Int? = null
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
        val encryption: String = "none",
        val flow: String? = null,
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
}
