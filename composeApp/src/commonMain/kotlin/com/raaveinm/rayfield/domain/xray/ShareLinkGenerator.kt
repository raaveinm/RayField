package com.raaveinm.rayfield.domain.xray

import com.raaveinm.rayfield.data.xray.Configurations
import com.raaveinm.rayfield.data.xray.XrayConfig
import kotlinx.serialization.json.*
import java.net.URLEncoder
import java.util.Base64

class ShareLinkGenerator {

    fun generateVlessLink(
        serverIp: String,
        port: Int,
        user: XrayConfig.VlessUser,
        stream: XrayConfig.StreamSettings
    ): String {
        val type = stream.network.name.lowercase()
        val security = stream.security.name.lowercase()
        val reality = stream.realitySettings

        val queryParams = mutableListOf<String>()
        queryParams.add("type=$type")
        if (security != "none") queryParams.add("security=$security")
        if (user.flow != null && user.flow != Configurations.vlessFlow.NONE) {
            queryParams.add("flow=${user.flow.name.lowercase().replace("_", "-")}")
        }

        if (security == "reality" && reality != null) {
            val sni = reality.serverNames.firstOrNull() ?: ""
            if (sni.isNotEmpty()) queryParams.add("sni=${encodeUri(sni)}")
            queryParams.add("pbk=${reality.password}") // password field stores public key
            reality.shortIds.firstOrNull()?.let { if (it.isNotEmpty()) queryParams.add("sid=$it") }
            reality.fingerprint?.let { queryParams.add("fp=${it.name.lowercase()}") }
        }

        // 1. Force the required encryption parameter for VLESS
        queryParams.add("encryption=none")

        val queryString = if (queryParams.isNotEmpty()) "?" + queryParams.joinToString("&") else ""
        
        // 2. Safely encode the email to prevent URI parsing errors and ensure ONLY ONE hashtag is used
        val safeAlias = user.email.replace("@", "%40")

        return "vless://${user.id}@$serverIp:$port$queryString#$safeAlias"
    }

    fun generateShadowsocksLink(
        serverIp: String,
        port: Int,
        user: XrayConfig.ShadowsocksUser,
        stream: XrayConfig.StreamSettings
    ): String {
        val method = user.method.name.lowercase().replace("_", "-")
        val password = user.password

        val userInfo = "$method:$password".toByteArray()
        val encodedUserInfo = Base64.getUrlEncoder().withoutPadding().encodeToString(userInfo)

        val queryParams = mutableListOf<String>()
        
        if (stream.security == Configurations.security.REALITY) {
            queryParams.add("type=${stream.network.name.lowercase()}")
            queryParams.add("security=reality")
            stream.realitySettings?.let { reality ->
                val sni = reality.serverNames.firstOrNull() ?: ""
                if (sni.isNotEmpty()) queryParams.add("sni=${encodeUri(sni)}")
                queryParams.add("fp=${reality.fingerprint?.name?.lowercase() ?: "chrome"}")
                queryParams.add("pbk=${reality.password}")
            }
        } else if (stream.security == Configurations.security.TLS) {
            queryParams.add("type=${stream.network.name.lowercase()}")
            queryParams.add("security=tls")
            stream.tlsSettings?.let { tls ->
                tls.serverName?.let { queryParams.add("sni=${encodeUri(it)}") }
                tls.fingerprint?.let { queryParams.add("fp=${it.name.lowercase()}") }
            }
        }

        val queryString = if (queryParams.isNotEmpty()) "?" + queryParams.joinToString("&") else ""
        val tag = encodeUri(user.email)

        return "ss://$encodedUserInfo@$serverIp:$port$queryString#$tag"
    }

    fun generateLink(outbound: XrayConfig.OutboundConfig): String {
        return when (outbound.protocol) {
            Configurations.protocol.VLESS -> buildVlessOutboundLink(outbound)
            Configurations.protocol.SHADOWSOCKS -> buildShadowsocksOutboundLink(outbound)
            else -> "Protocol ${outbound.protocol} does not support share links"
        }
    }

    private fun buildVlessOutboundLink(outbound: XrayConfig.OutboundConfig): String {
        val settingsJson = outbound.settings ?: return ""
        val vlessSettings = XrayConfigBuilder.jsonFormatter.decodeFromJsonElement<XrayConfig.VlessOutboundSettings>(settingsJson)
        val vnext = vlessSettings.vnext.firstOrNull() ?: return ""
        val user = vnext.users.firstOrNull() ?: return ""

        val uuid = user.id
        val address = vnext.address
        val port = vnext.port

        val queryParams = mutableListOf<String>()
        val stream = outbound.streamSettings

        if (user.flow != null && user.flow != Configurations.vlessFlow.NONE) {
            queryParams.add("flow=${user.flow.name.lowercase().replace("_", "-")}")
        }

        if (stream != null) {
            queryParams.add("type=${stream.network.name.lowercase()}")
            queryParams.add("security=${stream.security.name.lowercase()}")

            stream.realitySettings?.let { reality ->
                val sni = reality.serverNames.firstOrNull() ?: ""
                if (sni.isNotEmpty()) queryParams.add("sni=${encodeUri(sni)}")
                queryParams.add("pbk=${reality.password}")
                reality.shortIds.firstOrNull()?.let { if (it.isNotEmpty()) queryParams.add("sid=$it") }
                reality.fingerprint?.let { queryParams.add("fp=${it.name.lowercase()}") }
            }

            stream.tlsSettings?.let { tls ->
                tls.serverName?.let { queryParams.add("sni=${encodeUri(it)}") }
                tls.fingerprint?.let { queryParams.add("fp=${it.name.lowercase()}") }
            }
        }

        queryParams.add("encryption=none")

        val queryString = if (queryParams.isNotEmpty()) "?" + queryParams.joinToString("&") else ""

        val tag = outbound.tag ?: "Rayfield_VLESS"
        val safeAlias = tag.replace("@", "%40")

        return "vless://$uuid@$address:$port$queryString#$safeAlias"
    }


    private fun buildShadowsocksOutboundLink(outbound: XrayConfig.OutboundConfig): String {
        val settingsJson = outbound.settings ?: return ""
        val ssSettings = XrayConfigBuilder.jsonFormatter.decodeFromJsonElement<XrayConfig.ShadowsocksOutboundSettings>(settingsJson)
        val server = ssSettings.servers.firstOrNull() ?: return ""

        val address = server.address
        val port = server.port
        val method = server.method.name.lowercase().replace("_", "-")
        val password = server.password

        val userInfo = "$method:$password".toByteArray()
        val encodedUserInfo = Base64.getUrlEncoder().withoutPadding().encodeToString(userInfo)

        val queryParams = mutableListOf<String>()
        val stream = outbound.streamSettings

        if (stream != null && stream.security != Configurations.security.NONE) {
            queryParams.add("type=${stream.network.name.lowercase()}")
            queryParams.add("security=${stream.security.name.lowercase()}")

            stream.realitySettings?.let { reality ->
                val sni = reality.serverNames.firstOrNull() ?: ""
                if (sni.isNotEmpty()) queryParams.add("sni=${encodeUri(sni)}")
                queryParams.add("pbk=${reality.password}")
                reality.shortIds.firstOrNull()?.let { if (it.isNotEmpty()) queryParams.add("sid=$it") }
                reality.fingerprint?.let { queryParams.add("fp=${it.name.lowercase()}") }
            }

            stream.tlsSettings?.let { tls ->
                tls.serverName?.let { queryParams.add("sni=${encodeUri(it)}") }
                tls.fingerprint?.let { queryParams.add("fp=${it.name.lowercase()}") }
            }
        }

        val queryString = if (queryParams.isNotEmpty()) "?" + queryParams.joinToString("&") else ""
        val tag = encodeUri(outbound.tag ?: "Rayfield_Shadowsocks")

        return "ss://$encodedUserInfo@$address:$port$queryString#$tag"
    }

    fun generateClientJson(
        serverIp: String,
        port: Int,
        protocol: Configurations.protocol,
        vlessUser: XrayConfig.VlessUser? = null,
        ssUser: XrayConfig.ShadowsocksUser? = null,
        stream: XrayConfig.StreamSettings,
        tag: String? = "proxy"
    ): String {
        val outbounds = mutableListOf<XrayConfig.OutboundConfig>()

        // 1. Primary Proxy Outbound
        val settings = when (protocol) {
            Configurations.protocol.VLESS -> {
                XrayConfigBuilder.toSettings(
                    XrayConfig.VlessOutboundSettings(
                        vnext = listOf(
                            XrayConfig.VnextServer(
                                address = serverIp,
                                port = port,
                                users = listOf(
                                    XrayConfig.VnextUser(
                                        id = vlessUser?.id ?: "",
                                        flow = vlessUser?.flow
                                    )
                                )
                            )
                        )
                    )
                )
            }
            Configurations.protocol.SHADOWSOCKS -> {
                XrayConfigBuilder.toSettings(
                    XrayConfig.ShadowsocksOutboundSettings(
                        servers = listOf(
                            XrayConfig.ShadowsocksOutboundServer(
                                address = serverIp,
                                port = port,
                                method = ssUser?.method ?: Configurations.shadowsocksMethod.AES_256_GCM,
                                password = ssUser?.password ?: ""
                            )
                        )
                    )
                )
            }
            else -> null
        }

        outbounds.add(
            XrayConfig.OutboundConfig(
                protocol = protocol,
                settings = settings,
                streamSettings = stream,
                tag = tag ?: "proxy"
            )
        )

        // 2. Direct Outbound
        outbounds.add(
            XrayConfig.OutboundConfig(
                protocol = Configurations.protocol.FREEDOM,
                tag = "direct"
            )
        )

        // 3. Block Outbound
        outbounds.add(
            XrayConfig.OutboundConfig(
                protocol = Configurations.protocol.BLACKHOLE,
                tag = "block"
            )
        )

        // We build the JSON manually to bypass InboundConfig's inboundProtocol restriction
        return buildJsonObject {
            put("log", buildJsonObject { put("loglevel", "warning") })
            put("inbounds", buildJsonArray {
                add(buildJsonObject {
                    put("listen", "127.0.0.1")
                    put("port", 1080)
                    put("protocol", "socks")
                    put("settings", XrayConfigBuilder.toSettings(XrayConfig.SocksInboundSettings()))
                    put("sniffing", buildJsonObject {
                        put("enabled", true)
                        put("destOverride", buildJsonArray { add("http"); add("tls"); add("fakedns") })
                    })
                    put("tag", "socks-in")
                })
                add(buildJsonObject {
                    put("listen", "127.0.0.1")
                    put("port", 1081)
                    put("protocol", "http")
                    put("settings", XrayConfigBuilder.toSettings(XrayConfig.HttpInboundSettings()))
                    put("sniffing", buildJsonObject {
                        put("enabled", true)
                        put("destOverride", buildJsonArray { add("http"); add("tls"); add("fakedns") })
                    })
                    put("tag", "http-in")
                })
            })
            put("outbounds", XrayConfigBuilder.jsonFormatter.encodeToJsonElement(outbounds))
            put("routing", buildJsonObject {
                put("domainStrategy", "AsIs")
                put("rules", buildJsonArray {
                    add(buildJsonObject {
                        put("ip", buildJsonArray { add("geoip:private") })
                        put("outboundTag", "direct")
                        put("type", "field")
                    })
                    add(buildJsonObject {
                        put("ip", buildJsonArray { add("geoip:cn") })
                        put("outboundTag", "direct")
                        put("type", "field")
                    })
                    add(buildJsonObject {
                        put("domain", buildJsonArray { add("geosite:cn") })
                        put("outboundTag", "direct")
                        put("type", "field")
                    })
                })
            })
        }.let { XrayConfigBuilder.jsonFormatter.encodeToString(it) }
    }

    private fun encodeUri(value: String): String {
        return URLEncoder.encode(value, "UTF-8")
            .replace("+", "%20")
            .replace("%2F", "/")
    }
}
