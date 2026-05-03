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
            Configurations.protocol.TROJAN -> buildTrojanLink(outbound)
            Configurations.protocol.SHADOWSOCKS -> buildShadowsocksLink(outbound)
            else -> throw IllegalArgumentException("${outbound.protocol} is undefined")
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

        // Ручной парсинг, так как ShadowsocksOutboundSettings не определен в XrayConfig.kt
        val serversArray = settingsJson["servers"]?.jsonArray
        val server = serversArray?.firstOrNull()?.jsonObject ?: return ""

        val address = server["address"]?.jsonPrimitive?.content ?: return ""
        val port = server["port"]?.jsonPrimitive?.int ?: return ""
        val method = server["method"]?.jsonPrimitive?.content ?: return ""
        val password = server["password"]?.jsonPrimitive?.content ?: return ""

        // Формат SIP002 для Shadowsocks требует Base64(method:password)
        val userInfo = "$method:$password".toByteArray()
        val encodedUserInfo = Base64.getUrlEncoder().withoutPadding().encodeToString(userInfo)

        val queryString = buildStreamParams(outbound.streamSettings, null)
        val tag = encodeUri(outbound.tag ?: "Rayfield_Shadowsocks")

        return "ss://$encodedUserInfo@$address:$port$queryString#$tag"
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