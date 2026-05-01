@file:Suppress("ClassName")
package com.raaveinm.rayfield.data.xray

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


//
// Created by Kirill "Raaveinm" on 4/29/26.
//

object Configurations {

    @Serializable
    enum class loglevel {
        @SerialName("debug") DEBUG,
        @SerialName("info") INFO,
        @SerialName("warning") WARNING,
        @SerialName("error") ERROR,
        @SerialName("none") NONE
    }

    @Serializable
    enum class network {
        @SerialName("tcp") TCP,
        @SerialName("kcp") KCP,
        @SerialName("ws") WS,
        @SerialName("http") HTTP,
        @SerialName("domainsocket") DOMAIN_SOCKET,
        @SerialName("quic") QUIC,
        @SerialName("grpc") GRPC
    }

    @Serializable
    enum class security {
        @SerialName("none") NONE,
        @SerialName("tls") TLS,
        @SerialName("reality") REALITY
    }

    @Serializable
    enum class domainStrategy {
        @SerialName("AsIs") AS_IS,
        @SerialName("UseIP") USE_IP,
        @SerialName("UseIPv4") USE_IPV4,
        @SerialName("UseIPv6") USE_IPV6,
        @SerialName("IPIfNonMatch") IP_IF_NON_MATCH,
        @SerialName("IPOnDemand") IP_ON_DEMAND
    }

    @Serializable
    enum class routingNetwork {
        @SerialName("tcp") TCP,
        @SerialName("udp") UDP,
        @SerialName("tcp,udp") TCP_UDP
    }

    @Serializable
    enum class protocol {
        @SerialName("vless") VLESS,
        @SerialName("vmess") VMESS,
        @SerialName("trojan") TROJAN,
        @SerialName("shadowsocks") SHADOWSOCKS,
        @SerialName("socks") SOCKS,
        @SerialName("http") HTTP,
        @SerialName("dokodemo-door") DOKODEMO_DOOR,
        @SerialName("dns") DNS,
        @SerialName("wireguard") WIREGUARD,
        @SerialName("hysteria") HYSTERIA,
        @SerialName("blackhole") BLACKHOLE,
        @SerialName("freedom") FREEDOM,
        @SerialName("loopback") LOOPBACK
    }

    @Serializable
    enum class fingerprint {
        @SerialName("chrome") CHROME,
        @SerialName("firefox") FIREFOX,
        @SerialName("safari") SAFARI,
        @SerialName("ios") IOS,
        @SerialName("android") ANDROID,
        @SerialName("edge") EDGE,
        @SerialName("360") B360,
        @SerialName("qq") QQ,
        @SerialName("random") RANDOM,
        @SerialName("randomized") RANDOMIZED,
        @SerialName("unsafe") UNSAFE
    }
}