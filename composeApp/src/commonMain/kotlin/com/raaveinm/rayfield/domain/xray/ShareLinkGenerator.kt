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
            Configurations.protocol.SHADOWSOCKS -> buildShadowsocksLink(outbound)
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


    private fun buildShadowsocksLink(outbound: XrayConfig.OutboundConfig): String {
        val settingsJson = outbound.settings ?: return ""
        val ssSettings = XrayConfigBuilder.jsonFormatter.decodeFromJsonElement<XrayConfig.ShadowsocksOutboundSettings>(settingsJson)
        val server = ssSettings.servers.firstOrNull() ?: return ""

        val address = server.address
        val port = server.port
        val method = server.method.name.lowercase().replace("_", "-")
        val password = server.password

        val userInfo = "$method:$password".toByteArray()
        val encodedUserInfo = Base64.getUrlEncoder().withoutPadding().encodeToString(userInfo)

        val queryString = buildStreamParams(outbound.streamSettings, null)
        val tag = encodeUri(outbound.tag ?: "Rayfield_Shadowsocks")

        return "ss://$encodedUserInfo@$address:$port$queryString#$tag"
    }

    private fun buildStreamParams(stream: XrayConfig.StreamSettings?, flow: Configurations.vlessFlow?): String {
        val queryParams = mutableListOf<String>()

        // Flow для XTLS/Vision
        if (flow != null && flow != Configurations.vlessFlow.NONE) {
            queryParams.add("flow=${flow.name.lowercase().replace("_", "-")}")
        }

        if (stream == null) return if (queryParams.isNotEmpty()) "?" + queryParams.joinToString("&") else ""

        stream.network.let { queryParams.add("type=${it.name.lowercase()}") }
        stream.security.let { queryParams.add("security=${it.name.lowercase()}") }

        // Reality
        stream.realitySettings?.let { reality ->
            val sni = reality.serverNames.firstOrNull() ?: reality.dest?.split(":")?.firstOrNull() ?: reality.dest ?: ""
            if (sni.isNotEmpty()) queryParams.add("sni=${encodeUri(sni)}")
            queryParams.add("pbk=${reality.privateKey}")
            reality.shortIds.firstOrNull()?.let { queryParams.add("sid=$it") }
        }

        // TLS
        stream.tlsSettings?.let { tls ->
            tls.serverName?.let { queryParams.add("sni=${encodeUri(it)}") }
            tls.fingerprint?.let { queryParams.add("fp=${it.name.lowercase()}") }
            tls.alpn?.let { alpn ->
                if (alpn.isNotEmpty()) queryParams.add("alpn=${encodeUri(alpn.joinToString(","))}")
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