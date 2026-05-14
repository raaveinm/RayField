@file:Suppress("unused")

package com.raaveinm.rayfield.domain.xray

import com.raaveinm.rayfield.data.xray.Configurations
import com.raaveinm.rayfield.data.xray.XrayConfig
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object XrayConfigBuilder {

    @PublishedApi
    internal val jsonFormatter = Json {
        prettyPrint = true
        encodeDefaults = false
        explicitNulls = false
    }

    fun buildJson(config: XrayConfig): String {
        return jsonFormatter.encodeToString(config)
    }

    fun parseJson(json: String): XrayConfig {
        return jsonFormatter.decodeFromString(json)
    }

    inline fun <reified T> toSettings(settings: T): JsonObject {
        return jsonFormatter.encodeToJsonElement(settings).jsonObject
    }

    // --- STREAM SETTINGS FACTORIES ---

    fun realityStreamSettings(
        dest: String,
        serverNames: List<String>,
        privateKey: String,
        shortIds: List<String>,
        show: Boolean = false
    ): XrayConfig.StreamSettings {
        return XrayConfig.StreamSettings(
            network = Configurations.network.TCP,
            security = Configurations.security.REALITY,
            realitySettings = XrayConfig.RealitySettings(
                show = show,
                dest = dest,
                serverNames = serverNames,
                privateKey = privateKey,
                shortIds = shortIds
            )
        )
    }

    // --- PROTOCOL SETTINGS FACTORIES ---

    fun vlessInboundSettings(
        uuid: String,
        flow: Configurations.flow? = null,
        email: String? = null,
        level: Int? = null,
        decryption: Configurations.decryption = Configurations.decryption.NONE,
        fallbacks: List<XrayConfig.Fallback>? = null
    ): JsonObject {
        val settings = XrayConfig.VlessInboundSettings(
            clients = listOf(XrayConfig.VlessUser(id = uuid, flow = flow, email = email)),
            decryption = decryption,
            fallbacks = fallbacks
        )
        return toSettings(settings)
    }

    fun vmessInboundSettings(
        uuid: String,
        alterId: Int = 0,
        email: String? = null,
        level: Int? = null
    ): JsonObject {
        val settings = XrayConfig.VMessInboundSettings(
            clients = listOf(XrayConfig.VMessUser(id = uuid, alterId = alterId, email = email, level = level))
        )
        return toSettings(settings)
    }

    fun trojanInboundSettings(
        password: String,
        email: String? = null,
        level: Int? = null,
        fallbacks: List<XrayConfig.Fallback>? = null
    ): JsonObject {
        val settings = XrayConfig.TrojanInboundSettings(
            clients = listOf(XrayConfig.TrojanUser(password = password, email = email, level = level)),
            fallbacks = fallbacks
        )
        return toSettings(settings)
    }

    fun shadowsocksInboundSettings(
        method: String,
        password: String,
        network: String? = "tcp,udp"
    ): JsonObject {
        val settings = XrayConfig.ShadowsocksInboundSettings(
            method = method,
            password = password,
            network = network
        )
        return toSettings(settings)
    }

    fun socksInboundSettings(
        auth: String = "noauth",
        udp: Boolean = false,
        ip: String? = null,
        userLevel: Int = 0
    ): JsonObject {
        val settings = XrayConfig.SocksInboundSettings(
            auth = auth,
            udp = udp,
            ip = ip,
            userLevel = userLevel
        )
        return toSettings(settings)
    }

    fun dokodemoInboundSettings(
        address: String? = null,
        port: Int? = null,
        network: String? = "tcp,udp",
        followRedirect: Boolean = false,
        userLevel: Int = 0
    ): JsonObject {
        val settings = XrayConfig.DokodemoInboundSettings(
            address = address,
            port = port,
            network = network,
            followRedirect = followRedirect,
            userLevel = userLevel
        )
        return toSettings(settings)
    }

    fun dnsInboundSettings(
        network: String? = "tcp,udp",
        address: String? = null,
        port: Int? = null,
        userLevel: Int = 0
    ): JsonObject {
        val settings = XrayConfig.DnsInboundSettings(
            network = network,
            address = address,
            port = port,
            userLevel = userLevel
        )
        return toSettings(settings)
    }

    fun freedomOutboundSettings(
        domainStrategy: Configurations.domainStrategy = Configurations.domainStrategy.AS_IS,
        redirect: String? = null,
        userLevel: Int = 0
    ): JsonObject {
        val settings = XrayConfig.FreedomOutboundSettings(
            domainStrategy = domainStrategy,
            redirect = redirect,
            userLevel = userLevel
        )
        return toSettings(settings)
    }

    fun vlessOutboundSettings(
        address: String,
        port: Int,
        uuid: String,
        encryption: Configurations.encryption = Configurations.encryption.NONE,
        flow: Configurations.flow? = null,
        level: Int? = null
    ): JsonObject {
        val settings = XrayConfig.VlessOutboundSettings(
            vnext = listOf(
                XrayConfig.VlessOutboundVnext(
                    address = address,
                    port = port,
                    users = listOf(
                        XrayConfig.VlessOutboundUser(id = uuid, encryption = encryption, flow = flow, level = level)
                    )
                )
            )
        )
        return toSettings(settings)
    }

    fun vmessOutboundSettings(
        address: String,
        port: Int,
        uuid: String,
        alterId: Int = 0,
        security: String = "auto",
        level: Int? = null
    ): JsonObject {
        val settings = XrayConfig.VMessOutboundSettings(
            vnext = listOf(
                XrayConfig.VMessOutboundVnext(
                    address = address,
                    port = port,
                    users = listOf(
                        XrayConfig.VMessOutboundUser(id = uuid, alterId = alterId, security = security, level = level)
                    )
                )
            )
        )
        return toSettings(settings)
    }

    fun trojanOutboundSettings(
        address: String,
        port: Int,
        password: String,
        email: String? = null,
        level: Int? = null
    ): JsonObject {
        val settings = XrayConfig.TrojanOutboundSettings(
            servers = listOf(
                XrayConfig.TrojanOutboundServer(
                    address = address,
                    port = port,
                    password = password,
                    email = email,
                    level = level
                )
            )
        )
        return toSettings(settings)
    }

    fun shadowsocksOutboundSettings(
        address: String,
        port: Int,
        method: String,
        password: String,
        email: String? = null,
        level: Int? = null
    ): JsonObject {
        val settings = XrayConfig.ShadowsocksOutboundSettings(
            servers = listOf(
                XrayConfig.ShadowsocksOutboundServer(
                    address = address,
                    port = port,
                    method = method,
                    password = password,
                    email = email,
                    level = level
                )
            )
        )
        return toSettings(settings)
    }

    fun socksOutboundSettings(
        address: String,
        port: Int,
        user: String? = null,
        pass: String? = null,
        level: Int? = null
    ): JsonObject {
        val users = if (user != null && pass != null) {
            listOf(XrayConfig.SocksOutboundUser(user = user, pass = pass, level = level))
        } else null

        val settings = XrayConfig.SocksOutboundSettings(
            servers = listOf(
                XrayConfig.SocksOutboundServer(
                    address = address,
                    port = port,
                    users = users
                )
            )
        )
        return toSettings(settings)
    }

    fun wireguardOutboundSettings(
        secretKey: String,
        address: List<String>,
        publicKey: String,
        endpoint: String? = null,
        keepAlive: Int = 0,
        mtu: Int = 1420,
        reserved: List<Int>? = null
    ): JsonObject {
        val settings = XrayConfig.WireguardOutboundSettings(
            secretKey = secretKey,
            address = address,
            peers = listOf(XrayConfig.WireguardPeer(publicKey = publicKey, endpoint = endpoint, keepAlive = keepAlive)),
            mtu = mtu,
            reserved = reserved
        )
        return toSettings(settings)
    }

    fun hysteriaOutboundSettings(
        address: String,
        port: Int,
        auth: String? = null,
        up_mbps: Int? = null,
        down_mbps: Int? = null,
        obfs: String? = null
    ): JsonObject {
        val settings = XrayConfig.HysteriaOutboundSettings(
            servers = listOf(XrayConfig.HysteriaServer(address = address, port = port)),
            auth = auth,
            up_mbps = up_mbps,
            down_mbps = down_mbps,
            obfs = obfs
        )
        return toSettings(settings)
    }

    fun blackholeOutboundSettings(
        type: String = "none"
    ): JsonObject {
        val settings = XrayConfig.BlackholeOutboundSettings(
            response = XrayConfig.BlackholeResponse(type = type)
        )
        return toSettings(settings)
    }

    fun loopbackOutboundSettings(
        inboundTag: String
    ): JsonObject {
        val settings = XrayConfig.LoopbackOutboundSettings(
            inboundTag = inboundTag
        )
        return toSettings(settings)
    }

    fun defaultRoutingConfig(
        blockAds: Boolean = true,
        blockMalicious: Boolean = true,
        directPrivateIps: Boolean = true
    ): XrayConfig.RoutingConfig {
        val rules = mutableListOf<XrayConfig.RoutingRule>()

        if (blockAds) {
            rules.add(
                XrayConfig.RoutingRule(
                    type = Configurations.ruleType.FIELD,
                    domain = listOf("geosite:category-ads-all"),
                    outboundTag = "block"
                )
            )
        }

        if (blockMalicious) {
            rules.add(
                XrayConfig.RoutingRule(
                    type = Configurations.ruleType.FIELD,
                    domain = listOf("geosite:malware", "geosite:phishing"),
                    outboundTag = "block"
                )
            )
        }

        if (directPrivateIps) {
            rules.add(
                XrayConfig.RoutingRule(
                    type = Configurations.ruleType.FIELD,
                    ip = listOf("geoip:private"),
                    outboundTag = "block"
                )
            )
        }

        return XrayConfig.RoutingConfig(
            domainStrategy = Configurations.domainStrategy.IP_IF_NON_MATCH,
            domainMatcher = "mph",
            rules = rules
        )
    }

    fun shadowsocksInboundSettings(
        method: Configurations.shadowsocksMethod = Configurations.shadowsocksMethod.AES_256_GCM,
        password: String,
        network: String? = "tcp,udp"
    ): JsonObject {
        val settings = XrayConfig.ShadowsocksInboundSettings(
            method = jsonFormatter.encodeToJsonElement(method).jsonPrimitive.content,
            password = password,
            network = network
        )
        return toSettings(settings)
    }
}