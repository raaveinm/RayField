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

        val queryString = if (queryParams.isNotEmpty()) "?" + queryParams.joinToString("&") else ""
        val tag = encodeUri(user.email)
        return "vless://${user.id}@$serverIp:$port$queryString#$tag"
    }

    fun generateShadowsocksLink(
        serverIp: String,
        port: Int,
        user: XrayConfig.ShadowsocksUser
    ): String {
        val method = user.method.name.lowercase().replace("_", "-")
        val password = user.password

        val userInfo = "$method:$password".toByteArray()
        val encodedUserInfo = Base64.getUrlEncoder().withoutPadding().encodeToString(userInfo)

        val tag = encodeUri(user.email)

        return "ss://$encodedUserInfo@$serverIp:$port#$tag"
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

        val queryString = if (queryParams.isNotEmpty()) "?" + queryParams.joinToString("&") else ""
        val tag = encodeUri(outbound.tag ?: "Rayfield_VLESS")

        return "vless://$uuid@$address:$port$queryString#$tag"
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

        val tag = encodeUri(outbound.tag ?: "Rayfield_Shadowsocks")

        return "ss://$encodedUserInfo@$address:$port#$tag"
    }

    private fun encodeUri(value: String): String {
        return URLEncoder.encode(value, "UTF-8")
            .replace("+", "%20")
            .replace("%2F", "/")
    }
}
