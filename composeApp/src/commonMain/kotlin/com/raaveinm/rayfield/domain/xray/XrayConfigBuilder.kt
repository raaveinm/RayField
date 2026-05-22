@file:Suppress("unused")

package com.raaveinm.rayfield.domain.xray

import com.raaveinm.rayfield.data.xray.Configurations
import com.raaveinm.rayfield.data.xray.XrayConfig
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject

object XrayConfigBuilder {

    @PublishedApi
    internal val jsonFormatter = Json {
        prettyPrint = true
        encodeDefaults = true
        explicitNulls = false
    }

    fun buildJson(config: XrayConfig): String = jsonFormatter.encodeToString(config)
    fun parseJson(json: String): XrayConfig = jsonFormatter.decodeFromString(json)
    inline fun <reified T> toSettings(settings: T): JsonObject = jsonFormatter
        .encodeToJsonElement(settings).jsonObject
    
    fun buildConfig(
        log: XrayConfig.LogConfig,
        dns: XrayConfig.DnsConfig,
        fakedns: List<XrayConfig.FakeDnsConfig>,
        routing: XrayConfig.RoutingConfig,
        inbounds: List<XrayConfig.InboundConfig>,
        outbounds: List<XrayConfig.OutboundConfig>
    ): XrayConfig {
        return XrayConfig(
            log = log,
            dns = dns,
            fakedns = fakedns,
            routing = routing,
            inbounds = inbounds,
            outbounds = outbounds
        )
    }

    ///////////////////////////////////////////////
    // Log Setting Factory
    ///////////////////////////////////////////////
    
    fun logSettingsBuilder(
        access: String? = null,
        error: String? = null,
        loglevel: Configurations.loglevel = Configurations.loglevel.ERROR,
        dnsLog: Boolean? = null,
        maskAddress: String? = null
    ): XrayConfig.LogConfig = 
        XrayConfig.LogConfig(access, error, loglevel, dnsLog, maskAddress)

    ///////////////////////////////////////////////
    // DNS Setting Factory
    ///////////////////////////////////////////////
    
    fun dnsSettingsBuilder(
        servers: JsonArray? = null,
        hosts: JsonObject? = null,
        queryStrategy: String? = null,
        tag: String? = null
    ) : XrayConfig.DnsConfig {
        return XrayConfig.DnsConfig(
            servers,
            hosts,
            queryStrategy,
            tag
        )
    }
    
    fun dnsHostsBuilder(
        domains: List<String>,
        ips: List<String>
    ) : JsonObject {
        return JsonObject(
            domains.zip(ips).associate { it.first to JsonPrimitive(it.second) }
        )
    }
    
    fun dnsServersBuilder(
        servers: List<String> = listOf("fakedns", "1.1.1.1", "8.8.8.8", "localhost")
    ) : JsonArray = JsonArray(servers.map { JsonPrimitive(it) })

    ///////////////////////////////////////////////
    // Routing Setting Factory
    ///////////////////////////////////////////////

    fun routingSettingsBuilder(
        domainStrategy: Configurations.routingDomainStrategy = Configurations.routingDomainStrategy.AS_IS,
        rules: List<XrayConfig.RoutingRule> = emptyList()
    ) : XrayConfig.RoutingConfig {
        return XrayConfig.RoutingConfig(
            domainStrategy,
            rules
        )
    }

    ///////////////////////////////////////////////
    // Inbound Setting Factory
    ///////////////////////////////////////////////
    
    fun inboundsSettingsBuilder(
        listen: String = "0.0.0.0",
        port: Int,
        protocol: Configurations.inboundProtocol,
        settings: JsonObject,
        streamSettings: XrayConfig.StreamSettings? = null,
        tag: String? = "identifier",
        sniffing: XrayConfig.SniffingObject? = null
    ) : XrayConfig.InboundConfig {
        return XrayConfig.InboundConfig(
            listen, port, protocol, settings, streamSettings, tag, sniffing
        )
    }

    fun vlessSettingsBuilder(
        users: List<XrayConfig.VlessUser>,
        decryption: Configurations.vlessDecryption = Configurations.vlessDecryption.NONE,
        fallbacks: List<XrayConfig.VlessFallback>? = null
    ) : JsonObject {
        val settings = XrayConfig.VlessInboundSettings(
            users = users,
            decryption = decryption,
            fallbacks = fallbacks
        )

        return toSettings(settings)
    }

    fun shadowsocksSettingBuilder(
        network: Configurations.shadowSocksNetwork = Configurations.shadowSocksNetwork.TCP_UDP,
        method: Configurations.shadowsocksMethod = Configurations.shadowsocksMethod.AES_256_GCM,
        password: String,
        email: String = "love@xray.com",
        users: List<XrayConfig.ShadowsocksUser> = emptyList()
    ) : JsonObject {
        val settings = XrayConfig.ShadowsocksInboundSettings(
            network = network,
            method = method,
            password = password,
            email = email,
            users = users
        )

        return toSettings(settings)
    }

    fun streamSettingsBuilder(
        network: Configurations.transportNetwork = Configurations.transportNetwork.RAW,
        security: Configurations.security = Configurations.security.NONE,
        tlsSettings: XrayConfig.TlsSettings? = null,
        realitySettings: XrayConfig.RealitySettings? = null,
        xhttpSettings: XrayConfig.XhttpSettings? = null,
    ) : XrayConfig.StreamSettings = XrayConfig.StreamSettings(
            network, security, tlsSettings, realitySettings, xhttpSettings)

    fun snifferSettingsBuilder(
        enabled: Boolean = true,
        destOverride: List<Configurations.sniffingDest> = listOf(Configurations.sniffingDest.HTTP,
            Configurations.sniffingDest.TLS, Configurations.sniffingDest.FAKE_DNS),
        metadataOnly: Boolean = false,
        domainsExcluded: List<String> = emptyList(),
        routeOnly: Boolean = false
    ) : XrayConfig.SniffingObject = XrayConfig.SniffingObject(
            enabled, destOverride, metadataOnly, domainsExcluded, routeOnly)

    ///////////////////////////////////////////////
    // Outbound Setting Factory
    ///////////////////////////////////////////////

    fun outboundsSettingsBuilder(
        sendThrough: String? = null,
        tag: String? = null,
        protocol: Configurations.protocol = Configurations.protocol.FREEDOM,
        settings: JsonObject? = null,
        streamSettings: XrayConfig.StreamSettings? = null,
        proxySettings: XrayConfig.ProxySettings? = null,
        mux: XrayConfig.MuxConfig? = null,
        targetStrategy: Configurations.targetStrategy? = Configurations.targetStrategy.AS_IS
    ): XrayConfig.OutboundConfig {
        return XrayConfig.OutboundConfig(
            sendThrough = sendThrough,
            tag = tag,
            protocol = protocol,
            settings = settings,
            streamSettings = streamSettings,
            proxySettings = proxySettings,
            mux = mux,
            targetStrategy = targetStrategy
        )
    }

    fun proxySettingsBuilder(
        tag: String? = null,
        transportLayer: Boolean = false
    ): XrayConfig.ProxySettings {
        return XrayConfig.ProxySettings(
            tag = tag,
            transportLayer = transportLayer
        )
    }

    fun muxConfigBuilder(
        enabled: Boolean = false,
        concurrency: Int = 8,
        xudpConcurrency: Int = 16,
        xudpQuic: String = "quic"
    ): XrayConfig.MuxConfig {
        return XrayConfig.MuxConfig(
            enabled = enabled,
            concurrency = concurrency,
            xudpConcurrency = xudpConcurrency,
            xudpQuic = xudpQuic
        )
    }
}