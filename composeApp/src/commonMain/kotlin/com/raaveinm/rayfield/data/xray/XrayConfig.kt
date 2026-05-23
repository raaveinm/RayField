@file:Suppress("PropertyName")

package com.raaveinm.rayfield.data.xray

//
// Created by Kirill "Raaveinm" on 4/29/26.
//

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

@Serializable
data class XrayConfig(
    val log: LogConfig? = null,
    val dns: DnsConfig? = null,
    val fakedns: List<FakeDnsConfig>? = null,
    val routing: RoutingConfig? = null,
    val inbounds: List<InboundConfig>? = null,
    val outbounds: List<OutboundConfig>? = null
) {
    ///////////////////////////////////////////////
    // Log Configuration
    ///////////////////////////////////////////////

    @Serializable
    data class LogConfig(
        val access: String? = null,
        val error: String? = null,
        val loglevel: Configurations.loglevel? = Configurations.loglevel.ERROR, // will be logged to std::out
        val dnsLog: Boolean? = null, // Whether to log DNS queries
        val maskAddress: String? = null //
    )

    ///////////////////////////////////////////////
    // DNS Configuration
    ///////////////////////////////////////////////

    @Serializable
    data class DnsConfig(
        val servers: JsonArray? = null, // List of DNS servers (IP strings or server objects)
        val hosts: JsonObject? = null, // Static IP mapping for domains (e.g., "google.com": "1.1.1.1")
        val queryStrategy: String? = null, // IP selection strategy: "UseIP", "UseIPv4", or "UseIPv6"
        val tag: String? = null // Internal identifier used for routing DNS traffic
    )

    @Serializable
    data class FakeDnsConfig(
        val ipPool: String,
        val poolSize: Int = 65535
    )

    ///////////////////////////////////////////////
    // Routing Configuration
    ///////////////////////////////////////////////

    @Serializable
    data class RoutingConfig(
        val domainStrategy: Configurations.routingDomainStrategy? = Configurations.routingDomainStrategy.AS_IS,
        val rules: List<RoutingRule> = emptyList()
    )

    @Serializable
    data class RoutingRule(
        val domain: List<String>? = null,
        val ip: List<String>? = null,
        val port: Int? = null,
        val network: Configurations.network = Configurations.network.TCP_UDP,
        val protocol: List<String>? = null, // "http", "tls", "bittorrent"
        val inboundTag: List<String>? = null,
        val outboundTag: String? = null,
        val type: String = "field"
    )

    ///////////////////////////////////////////////
    // Inbound Configuration
    ///////////////////////////////////////////////

    @Serializable
    data class InboundConfig(
        val listen: String = "0.0.0.0",
        val port: Int,
        val protocol: Configurations.inboundProtocol, // X-Ray config protocol
        val settings: JsonObject, // Protocol-specific settings
        val streamSettings: StreamSettings? = null,
        val tag: String? = "identifier", // tag must be same within all configuration
        val sniffing: SniffingObject? = null // intercept and extract the actual domain name from incoming traffic
    )

    @Serializable
    data class SniffingObject( // transparent proxies and similar purposes
        val enabled: Boolean = true,
        val destOverride: List<Configurations.sniffingDest> = listOf(Configurations.sniffingDest.HTTP,
            Configurations.sniffingDest.TLS, Configurations.sniffingDest.FAKE_DNS), // reset the destination of the current connection
        val metadataOnly: Boolean = false, // only the connection metadata will be used to sniff the destination address
        val domainsExcluded: List<String> = emptyList(), // which of destination addresses will not be reset
        val routeOnly: Boolean = false // proxy destination address remains the IP
    )

    ///////////////////////////////////////////////
    // Outbound Configuration
    ///////////////////////////////////////////////

    @Serializable
    data class OutboundConfig(
        val sendThrough: String? = null, // outbound proxy
        val tag: String? = null,
        val protocol: Configurations.protocol = Configurations.protocol.FREEDOM, // X-Ray outbound protocol
        val settings: JsonObject? = null, // Protocol-specific settings
        val streamSettings: StreamSettings? = null, // Protocol-specific settings
        val proxySettings: ProxySettings? = null,
        val mux: MuxConfig? = null, // Multiplexing configuration
        val targetStrategy: Configurations.targetStrategy? = Configurations.targetStrategy.AS_IS, // Domain resolution and connection routing
    )

    @Serializable
    data class ProxySettings(
        val tag: String? = null,
        val transportLayer: Boolean = false
    )
    ///////////////////////////////////////////////
    // In/Out-bound Configuration
    ///////////////////////////////////////////////

    @Serializable
    data class StreamSettings(
        val network: Configurations.transportNetwork = Configurations.transportNetwork.RAW,
        val security: Configurations.security = Configurations.security.NONE,
        val tlsSettings: TlsSettings? = null,
        val realitySettings: RealitySettings? = null,
        val xhttpSettings: XhttpSettings? = null,
    )

    @Serializable
    data class RealitySettings (
        val dest: String? = null,
        @SerialName("_rf_target")
        val target: Configurations.targetOptions? = Configurations.targetOptions.GITHUB, // target mask or ip
        @SerialName("_rf_customTarget")
        val customTarget: String? = null, // same, but with manual mask site input
        val serverNames: List<String>, // Domain names of mask servers
        val privateKey: String, // Generated UUID @XrayKeyPair
        @SerialName("_rf_publicKey")
        val password: String, // public Key (Generated UUID) @XrayKeyPair
        val shortIds: List<String> = emptyList(), // Short allowed IDs
        val show: Boolean = false, // distinguish different clients
        val fingerprint: Configurations.fingerprint? = Configurations.fingerprint.RANDOMIZED, // fingerprint
    )

    @Serializable
    data class TlsSettings(
        val serverName: String? = null, // The SNI
        val alpn: List<String>? = listOf("h2", "http/1.1"), // Default for compatibility
        val fingerprint: Configurations.fingerprint? = Configurations.fingerprint.CHROME,
        val allowInsecure: Boolean = false,
        val minVersion: String? = "1.2" // Default "1.2" is safe, "1.3" is stealthy
    )

    @Serializable
    data class XhttpSettings(
        val path: String? = null,
        val udp: Boolean = false
    )

    @Serializable
    data class MuxConfig(
        val enabled: Boolean = false,
        val concurrency: Int = 8,
        val xudpConcurrency: Int = 16,
        val xudpQuic: String = "quic"
    )
    ///////////////////////////////////////////////
    // Protocols Configuration
    ///////////////////////////////////////////////

    sealed interface InboundSettings

    // Shadowsocks

    @Serializable
    data class ShadowsocksInboundSettings(
        val network: Configurations.shadowSocksNetwork = Configurations.shadowSocksNetwork.TCP_UDP, // network type
        val method: Configurations.shadowsocksMethod = Configurations.shadowsocksMethod.AES_256_GCM, // encryption method
        val password: String, // openssl rand -base64 <length> - Uses a pre-shared key similar to WireGuard as the password
        val email: String = "love@xray.com",
        val users: List<ShadowsocksUser> = emptyList()
    ) : InboundSettings
    @Serializable
    data class ShadowsocksUser(
        val password: String,
        val method: Configurations.shadowsocksMethod = Configurations.shadowsocksMethod.AES_256_GCM, // specify "method" for each user
        val email: String = "unspecified@unkown-user.unknown"
    )

    // VLESS

    @Serializable
    data class VlessInboundSettings(
        val users: List<VlessUser> = emptyList(),
        val decryption: Configurations.vlessDecryption = Configurations.vlessDecryption.NONE,
        val fallbacks: List<VlessFallback>? = null
    ) : InboundSettings

    @Serializable
    data class VlessUser(
        val id: String, // UUID
        val email: String = "unspecified@unkown-user.unknown",
        val flow: Configurations.vlessFlow? = null
//        val reverse // reverse proxy
    )

    @Serializable
    data class VlessFallback(
        val dest: String,
        val port: Int
    )

    // SOCKS
    @Serializable
    data class SocksInboundSettings(
        val auth: String = "noauth",
        val udp: Boolean = true,
        val ip: String = "127.0.0.1"
    ) : InboundSettings

    // HTTP
    @Serializable
    data class HttpInboundSettings(
        val timeout: Int = 0
    ) : InboundSettings

    // Outbound Protocols

    @Serializable
    data class VlessOutboundSettings(
        val vnext: List<VnextServer>
    )

    @Serializable
    data class VnextServer(
        val address: String,
        val port: Int,
        val users: List<VnextUser>
    )

    @Serializable
    data class VnextUser(
        val id: String,
        val encryption: String = "none",
        val flow: Configurations.vlessFlow? = null
    )

    @Serializable
    data class ShadowsocksOutboundSettings(
        val servers: List<ShadowsocksOutboundServer>
    )

    @Serializable
    data class ShadowsocksOutboundServer(
        val address: String,
        val port: Int,
        val method: Configurations.shadowsocksMethod,
        val password: String,
        val email: String? = null,
        val level: Int? = null
    )
}