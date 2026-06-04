@file:Suppress("ClassName", "unused")
package com.raaveinm.rayfield.data.xray

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


//
// Created by Kirill "Raaveinm" on 4/29/26.
//

object Configurations {
    ///////////////////////////////////////////////
    // Common
    ///////////////////////////////////////////////

    @Serializable
    enum class network {
        @SerialName("tcp") TCP,
        @SerialName("udp") UDP,
        @SerialName("tcp,udp") TCP_UDP
    }

    @Serializable
    enum class protocol {
        @SerialName("vless") VLESS,
        @SerialName("shadowsocks") SHADOWSOCKS,
        @SerialName("socks") SOCKS,
        @SerialName("http") HTTP,
        //        @SerialName("hysteria") HYSTERIA,
        @SerialName("freedom") FREEDOM,
        @SerialName("blackhole") BLACKHOLE
    }

    ///////////////////////////////////////////////
    // Log Configuration
    ///////////////////////////////////////////////

    @Serializable
    enum class loglevel {
        @SerialName("debug")
        DEBUG,
        @SerialName("info")
        INFO,
        @SerialName("warning")
        WARNING,
        @SerialName("error")
        ERROR,
        @SerialName("none")
        NONE
    }

    ///////////////////////////////////////////////
    // DNS Configuration
    ///////////////////////////////////////////////

    @Serializable
    enum class queryStrategy {
        @SerialName("UseIP") USE_IP,
        @SerialName("UseIPv4") USE_IPV4,
        @SerialName("UseIPv6") USE_IPV6,
        @SerialName("UseSystem") USE_SYSTEM
    }

    @Serializable
    enum class targetOptions(val domain: String){
        @SerialName("api.github.com") GITHUB("api.github.com"),
        @SerialName("dl.google.com") GOOGLE("dl.google.com"),
        @SerialName("api.cloudflare.com") CLOUDFLARE("api.cloudflare.com"),
        @SerialName("www.yahoo.com") YAHOO("www.yahoo.com"),
        @SerialName("update.microsoft.com") MICROSOFT("update.microsoft.com")
    }

    ///////////////////////////////////////////////
    // Routing
    ///////////////////////////////////////////////

    @Serializable
    enum class routingDomainStrategy {
        @SerialName("AsIs") AS_IS,
        @SerialName("IPIfNonMatch") IP_IF_NON_MATCH,
        @SerialName("IPOnDemand") IP_ON_DEMAND
    }

    ///////////////////////////////////////////////
    // Inbound Configuration
    ///////////////////////////////////////////////

    @Serializable
    enum class inboundProtocol {
        @SerialName("vless") VLESS,
        @SerialName("shadowsocks") SHADOWSOCKS,
        //        @SerialName("hysteria") HYSTERIA,
    }

    fun inboundProtocol.toProtocol(): protocol = when (this) {
        inboundProtocol.VLESS -> protocol.VLESS
        inboundProtocol.SHADOWSOCKS -> protocol.SHADOWSOCKS
    }

    @Serializable
    enum class sniffingDest {
        @SerialName("http") HTTP,
        @SerialName("tls") TLS,
        @SerialName("quic") QUIC,
        @SerialName("fakedns") FAKE_DNS
    }

    ///////////////////////////////////////////////
    // Outbound Configuration
    ///////////////////////////////////////////////

    @Serializable
    enum class transportNetwork {
        @SerialName("tcp") TCP,// "tcp" for Reality/SS, "xhttp" or "grpc" for stealth
        @SerialName("raw") RAW,
        @SerialName("xhttp") XHTTP,
        @SerialName("mkcp") MKCP,
        //@SerialName("grpc") GRPC,  // DEPRECATED
        @SerialName("hysteria") HYSTERIA
    }

    @Serializable
    enum class security {
        @SerialName("none") NONE,
        @SerialName("tls") TLS,
        @SerialName("reality") REALITY
    }

    @Serializable
    enum class targetStrategy {
        @SerialName("AsIs") AS_IS,
        @SerialName("UseIP") USE_IP,
        @SerialName("UseIPv6v4") USE_IPV6V4,
        @SerialName("UseIPv4v6") USE_IPV4V6,
        @SerialName("UseIPv4") USE_IPV4,
        @SerialName("ForceIP") FORCE_IP,
        @SerialName("ForceIPv6v4") FORCE_IPV6V4,
        @SerialName("ForceIPv4v6") FORCE_IPV4V6,
        @SerialName("ForceIPv4") FORCE_IPV4
    }

    ///////////////////////////////////////////////
    // Protocol Configuration
    ///////////////////////////////////////////////

    // Shadowsocks

    @Serializable
    enum class shadowSocksNetwork {
        @SerialName("tcp") TCP,
        @SerialName("udp") UDP,
        @SerialName("tcp,udp") TCP_UDP
    }

    @Serializable
    enum class shadowsocksMethod {
        @SerialName("aes-128-gcm") AES_128_GCM,
        @SerialName("aes-192-gcm") AES_192_GCM,
        @SerialName("aes-256-gcm") AES_256_GCM,
        @SerialName("chacha20-ietf-poly1305") CHACHA20_IETF_POLY1305
    }

    // VLESS

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

    @Serializable
    enum class vlessFlow{
        @SerialName("none") NONE,
        @SerialName("xtls-rprx-vision") XTLS_RPRX_VISION
    }

    @Serializable
    enum class vlessDecryption {
        @SerialName("none") NONE,
        @SerialName("rc4-md5") RC4_MD5,
        @SerialName("chacha20-ietf-poly1305") CHACHA20_IETF_POLY1305,
        @SerialName("aes-128-gcm") AES_128_GCM,
        @SerialName("aes-128-ctr") AES_128_CTR,
        @SerialName("aes-192-gcm") AES_192_GCM,
        @SerialName("aes-256-gcm") AES_256_GCM
    }

    @Serializable
    enum class vlessTarget {
        @SerialName("tcp") TCP,
        @SerialName("udp") UDP,
        @SerialName("tcp,udp") TCP_UDP
    }
}
