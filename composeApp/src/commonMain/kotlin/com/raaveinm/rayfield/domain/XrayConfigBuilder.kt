@file:Suppress("unused")
package com.raaveinm.rayfield.domain

import com.raaveinm.rayfield.data.xray.Configurations
import com.raaveinm.rayfield.data.xray.XrayConfig
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject

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

    inline fun <reified T> toSettings(settings: T): JsonObject {
        return jsonFormatter.encodeToJsonElement(settings).jsonObject
    }

    // --- PROTOCOL SETTINGS FACTORIES ---

    fun vlessInboundSettings(
        uuid: String,
        email: String? = null,
        flow: String? = null,
        level: Int? = null,
        decryption: String = "none",
        fallbacks: List<XrayConfig.Fallback>? = null
    ): JsonObject {
        val settings = XrayConfig.VlessInboundSettings(
            clients = listOf(XrayConfig.VlessUser(id = uuid, flow = flow, email = email, level = level)),
            decryption = decryption,
            fallbacks = fallbacks
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
        network: String? = null
    ): JsonObject {
        val settings = XrayConfig.ShadowsocksInboundSettings(
            method = method,
            password = password,
            network = network
        )
        return toSettings(settings)
    }

    fun freedomOutboundSettings(
        domainStrategy: Configurations.domainStrategy = Configurations.domainStrategy.AS_IS
    ): JsonObject = buildJsonObject {
        // Enums can be easily converted back to their Xray string formats
        put("domainStrategy", jsonFormatter.encodeToJsonElement(domainStrategy))
    }

    fun vlessOutboundSettings(
        address: String,
        port: Int,
        uuid: String,
        encryption: String = "none",
        flow: String? = null,
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
}