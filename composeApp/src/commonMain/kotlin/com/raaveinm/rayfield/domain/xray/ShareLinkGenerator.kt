package com.raaveinm.rayfield.domain.xray

import com.raaveinm.rayfield.data.xray.Configurations
import com.raaveinm.rayfield.data.xray.XrayConfig
import kotlinx.serialization.json.*
import java.net.URLEncoder
import java.util.Base64

class ShareLinkGenerator {

    fun generateLink(outbound: XrayConfig.OutboundConfig): String {
        return when (outbound.protocol) {
            Configurations.protocol.VLESS -> buildVlessLink(outbound)
            Configurations.protocol.VMESS -> buildVmessLink(outbound)
            Configurations.protocol.TROJAN -> buildTrojanLink(outbound)
            Configurations.protocol.SHADOWSOCKS -> buildShadowsocksLink(outbound)
            Configurations.protocol.SOCKS -> buildSocksLink(outbound)
            Configurations.protocol.WIREGUARD -> buildWireguardLink(outbound)
            else -> "Protocol ${outbound.protocol} does not support share links"
        }
    }

    private fun buildVlessLink(outbound: XrayConfig.OutboundConfig): String {
        val settingsJson = outbound.settings ?: return ""
        val vlessSettings = XrayConfigBuilder.jsonFormatter.decodeFromJsonElement<XrayConfig.VlessOutboundSettings>(settingsJson)
        val vnext = vlessSettings.vnext.firstOrNull() ?: return ""
        val user = vnext.users.firstOrNull() ?: return ""

        val uuid = user.id
        val address = vnext.address
        val port = vnext.port

        val queryString = buildStreamParams(outbound.streamSettings, user.flow)
        val tag = encodeUri(outbound.tag ?: "Rayfield_VLESS")

        return "vless://$uuid@$address:$port$queryString#$tag"
    }

    private fun buildVmessLink(outbound: XrayConfig.OutboundConfig): String {
        val settingsJson = outbound.settings ?: return ""
        val vmessSettings = XrayConfigBuilder.jsonFormatter.decodeFromJsonElement<XrayConfig.VMessOutboundSettings>(settingsJson)
        val vnext = vmessSettings.vnext.firstOrNull() ?: return ""
        val user = vnext.users.firstOrNull() ?: return ""

        val stream = outbound.streamSettings
        val vmessJson = buildJsonObject {
            put("v", "2")
            put("ps", outbound.tag ?: "Rayfield_VMess")
            put("add", vnext.address)
            put("port", vnext.port)
            put("id", user.id)
            put("aid", user.alterId)
            put("scy", user.security)
            put("net", stream?.network?.name?.lowercase() ?: "tcp")
            put("type", "none") // header type
            
            stream?.wsSettings?.let {
                put("path", it.path)
                it.headers?.get("Host")?.let { host -> put("host", host) }
            }
            stream?.httpSettings?.let {
                put("path", it.path ?: "/")
                it.host?.firstOrNull()?.let { host -> put("host", host) }
            }
            stream?.grpcSettings?.let {
                put("path", it.serviceName)
            }
            
            put("tls", if (stream?.security == Configurations.security.TLS) "tls" else "none")
            stream?.tlsSettings?.let {
                put("sni", it.serverName ?: "")
                put("fp", it.fingerprint?.name?.lowercase() ?: "")
                put("alpn", it.alpn?.joinToString(",") ?: "")
            }
        }

        val encodedJson = Base64.getEncoder().encodeToString(vmessJson.toString().toByteArray())
        return "vmess://$encodedJson"
    }

    private fun buildTrojanLink(outbound: XrayConfig.OutboundConfig): String {
        val settingsJson = outbound.settings ?: return ""
        val trojanSettings = XrayConfigBuilder.jsonFormatter.decodeFromJsonElement<XrayConfig.TrojanOutboundSettings>(settingsJson)
        val server = trojanSettings.servers.firstOrNull() ?: return ""

        val password = server.password
        val address = server.address
        val port = server.port

        val queryString = buildStreamParams(outbound.streamSettings, null)
        val tag = encodeUri(outbound.tag ?: "Rayfield_Trojan")

        return "trojan://$password@$address:$port$queryString#$tag"
    }

    private fun buildShadowsocksLink(outbound: XrayConfig.OutboundConfig): String {
        val settingsJson = outbound.settings ?: return ""
        val ssSettings = XrayConfigBuilder.jsonFormatter.decodeFromJsonElement<XrayConfig.ShadowsocksOutboundSettings>(settingsJson)
        val server = ssSettings.servers.firstOrNull() ?: return ""

        val address = server.address
        val port = server.port
        val method = server.method
        val password = server.password

        // Формат SIP002 для Shadowsocks требует Base64(method:password)
        val userInfo = "$method:$password".toByteArray()
        val encodedUserInfo = Base64.getUrlEncoder().withoutPadding().encodeToString(userInfo)

        val queryString = buildStreamParams(outbound.streamSettings, null)
        val tag = encodeUri(outbound.tag ?: "Rayfield_Shadowsocks")

        return "ss://$encodedUserInfo@$address:$port$queryString#$tag"
    }

    private fun buildSocksLink(outbound: XrayConfig.OutboundConfig): String {
        val settingsJson = outbound.settings ?: return ""
        val socksSettings = XrayConfigBuilder.jsonFormatter.decodeFromJsonElement<XrayConfig.SocksOutboundSettings>(settingsJson)
        val server = socksSettings.servers.firstOrNull() ?: return ""

        val address = server.address
        val port = server.port
        val user = server.users?.firstOrNull()

        val auth = if (user != null) {
            val userInfo = "${user.user}:${user.pass}".toByteArray()
            Base64.getUrlEncoder().withoutPadding().encodeToString(userInfo) + "@"
        } else ""

        val tag = encodeUri(outbound.tag ?: "Rayfield_Socks")
        return "socks://$auth$address:$port#$tag"
    }

    private fun buildWireguardLink(outbound: XrayConfig.OutboundConfig): String {
        // Wireguard share links are less standardized, but often use wg://
        val settingsJson = outbound.settings ?: return ""
        val wgSettings = XrayConfigBuilder.jsonFormatter.decodeFromJsonElement<XrayConfig.WireguardOutboundSettings>(settingsJson)
        val peer = wgSettings.peers.firstOrNull() ?: return ""

        val tag = encodeUri(outbound.tag ?: "Rayfield_Wireguard")
        // Minimal WG link
        return "wg://${peer.publicKey}@${peer.endpoint ?: "0.0.0.0"}#$tag"
    }

    private fun buildStreamParams(stream: XrayConfig.StreamSettings?, flow: Configurations.flow?): String {
        val queryParams = mutableListOf<String>()

        // Flow для XTLS/Vision
        flow?.let { queryParams.add("flow=${it.name.lowercase().replace("_", "-")}") }

        if (stream == null) return if (queryParams.isNotEmpty()) "?" + queryParams.joinToString("&") else ""

        stream.network?.let { queryParams.add("type=${it.name.lowercase()}") }
        stream.security?.let { queryParams.add("security=${it.name.lowercase()}") }

        // Reality
        stream.realitySettings?.let { reality ->
            val sni = reality.serverNames.firstOrNull() ?: reality.dest.split(":").firstOrNull() ?: reality.dest
            queryParams.add("sni=${encodeUri(sni)}")
            queryParams.add("pbk=${reality.privateKey}")
            reality.shortIds.firstOrNull()?.let { queryParams.add("sid=$it") }
            reality.spiderX?.let { queryParams.add("spx=${encodeUri(it)}") }
        }

        // TLS
        stream.tlsSettings?.let { tls ->
            tls.serverName?.let { queryParams.add("sni=${encodeUri(it)}") }
            tls.fingerprint?.let { queryParams.add("fp=${it.name.lowercase()}") }
            tls.alpn?.let { alpn ->
                if (alpn.isNotEmpty()) queryParams.add("alpn=${encodeUri(alpn.joinToString(","))}")
            }
        }

        // WebSocket
        stream.wsSettings?.let { ws ->
            queryParams.add("path=${encodeUri(ws.path)}")
            ws.headers?.get("Host")?.let { queryParams.add("host=${encodeUri(it)}") }
        }

        // gRPC
        stream.grpcSettings?.let { grpc ->
            queryParams.add("serviceName=${encodeUri(grpc.serviceName)}")
            grpc.multiMode?.let { if (it) queryParams.add("mode=multi") }
        }

        // HTTP/H2
        stream.httpSettings?.let { http ->
            http.path?.let { queryParams.add("path=${encodeUri(it)}") }
            http.host?.let { host ->
                if (host.isNotEmpty()) queryParams.add("host=${encodeUri(host.joinToString(","))}")
            }
        }

        return if (queryParams.isNotEmpty()) "?" + queryParams.joinToString("&") else ""
    }

    private fun encodeUri(value: String): String {
        return URLEncoder.encode(value, "UTF-8")
            .replace("+", "%20")
            .replace("%2F", "/")
    }
}