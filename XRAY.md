```json
{
  // ==========================================================
  // 1. LOGGING SETTINGS
  // Controls output access and error logs.
  // ==========================================================
  "log": {
    "access": "/var/log/xray/access.log", // CONF: Path to access log. DOCS: file path or "none". DEFAULT: "none"
    "error": "/var/log/xray/error.log",   // CONF: Path to error log. DOCS: file path or "none". DEFAULT: "none"
    "loglevel": "debug",                  // DOCS: debug, info, warning, error, none. DEFAULT: "warning"
    "dnsLog": true,                       // DOCS: true/false. DEFAULT: false
    "maskAddress": "half"                 // DOCS: none, half, full. DEFAULT: "none"
  },

  // ==========================================================
  // 2. API SETTINGS
  // Enables gRPC API for remote management of the core.
  // ==========================================================
  "api": {
    "tag": "api",                         // META: Unique routing tag used to map traffic to this API inbound.
    "services": [
      "HandlerService",                   // DOCS: Add/remove inbounds/outbounds dynamically
      "LoggerService",                    // DOCS: Restart internal logger
      "StatsService",                     // DOCS: Query traffic stats (requires "stats" object)
      "ObservatoryService",               // DOCS: Query connection node statuses
      "RoutingService",                   // DOCS: Manipulate routing rules dynamically
      "ReflectionService"                 // DOCS: gRPC reflection for clients
    ]
  },

  // ==========================================================
  // 3. FAKEDNS SETTINGS
  // Returns fake IPs for domains to intercept traffic later.
  // ==========================================================
  "fakedns": [
    {
      "ipPool": "198.18.0.0/15",          // CONF: Reserved CIDR block for fake IPv4s. DOCS: Valid CIDR network.
      "poolSize": 65535                   // DOCS: Max number of IPs to cache. DEFAULT: 65535
    },
    {
      "ipPool": "fc00::/18",              // CONF: Reserved CIDR block for fake IPv6s.
      "poolSize": 65535                   // DOCS: Max number of IPs to cache. DEFAULT: 65535
    }
  ],

  // ==========================================================
  // 4. DNS SETTINGS
  // Internal DNS resolver configuration.
  // ==========================================================
  "dns": {
    "clientIp": "1.2.3.4",                // CONF: Your local public IP, sent to EDNS to optimize regional IP resolution.
    "queryStrategy": "UseIPv4",           // DOCS: UseIP, UseIPv4, UseIPv6. DEFAULT: "UseIP"
    "disableCache": false,                // DOCS: true/false. DEFAULT: false
    "disableFallback": false,             // DOCS: true/false. DEFAULT: false
    "disableFallbackIfMatch": true,       // DOCS: true/false. DEFAULT: false
    "tag": "dns-inbound",                 // META: Tag used by routing to intercept DNS
    "hosts": {
      "domain:example.com": "127.0.0.1",            // DOCS: Static mapping [domain] -> [IP]
      "domain:v2fly.org": "v2ray.com",              // DOCS: Domain alias [domain] -> [domain]
      "geosite:category-ads": ["127.0.0.1", "::1"]  // DOCS: Geosite routing [category] -> [IPs]
    },
    "servers": [
      {
        "address": "1.1.1.1",             // CONF: Primary DNS Server IP
        "port": 53,                       // CONF: Standard DNS port. DEFAULT: 53
        "domains": ["geosite:geolocation-!cn", "geosite:google"], // DOCS: Only use this server for these domains
        "expectIPs": ["geoip:us"],        // DOCS: Only accept IPs belonging to this geoip code
        "skipFallback": true              // DOCS: true/false. DEFAULT: false
      },
      {
        "address": "https://dns.google/dns-query", // CONF: DoH (DNS over HTTPS) endpoint
        "domains": ["geosite:geolocation-!cn"]
      },
      "8.8.8.8",                          // CONF: Fallback UDP DNS
      "localhost"                         // CONF: System's default DNS
    ]
  },

  // ==========================================================
  // 5. ROUTING SETTINGS
  // Determines which outbound is used for which traffic.
  // ==========================================================
  "routing": {
    "domainStrategy": "AsIs",             // DOCS: AsIs, IPIfNonMatch, IPOnDemand. DEFAULT: "AsIs"
    "domainMatcher": "mph",               // DOCS: linear, mph (mph uses less RAM). DEFAULT: "linear"
    "rules": [
      {
        "type": "field",                  // DOCS: field, logical. DEFAULT: "field"
        "inboundTag": ["api-in"],         // DOCS: Array of tags matching the `inbounds` section
        "outboundTag": "api-out"          // DOCS: Tag matching the `outbounds` section
      },
      {
        "type": "field",
        "inboundTag": ["proxy-in", "socks-in"],
        "port": 53,                       // DOCS: Match by port or port range (e.g., "53,443", "1000-2000")
        "network": "udp",                 // DOCS: tcp, udp, tcp,udp
        "outboundTag": "dns-out"
      },
      {
        "type": "field",
        "domain": ["geosite:category-ads"],
        "outboundTag": "block-out"
      },
      {
        "type": "field",
        "domain": ["geosite:netflix"],
        "balancerTag": "netflix-balancer" // META: Connects to the "balancers" section below
      },
      {
        "type": "field",
        "protocol": ["bittorrent"],       // DOCS: http, tls, bittorrent
        "outboundTag": "direct-out"
      },
      {
        "type": "logical",                // DOCS: Evaluates boolean logic (Xray specific)
        "pattern": "fake-dns-ip",
        "outboundTag": "proxy-out"
      }
    ],
    "balancers": [
      {
        "tag": "netflix-balancer",        // META: Must match the `balancerTag` in rules
        "selector": ["us-node-1", "us-node-2"], // DOCS: Array of prefixes matching outbound tags
        "strategy": {
          "type": "leastPing"             // DOCS: random, roundRobin, leastPing. DEFAULT: "random"
        }
      }
    ]
  },

  // ==========================================================
  // 6. POLICY SETTINGS
  // User limits and system traffic stats.
  // ==========================================================
  "policy": {
    "levels": {
      "0": {                              // META: Matches "level" inside inbound clients
        "handshake": 4,                   // DOCS: Max connection handshake time in seconds. DEFAULT: 4
        "connIdle": 300,                  // DOCS: Max idle connection time in seconds. DEFAULT: 300
        "uplinkOnly": 2,                  // DOCS: Seconds to keep uplink alive after downlink closes. DEFAULT: 2
        "downlinkOnly": 5,                // DOCS: Seconds to keep downlink alive after uplink closes. DEFAULT: 5
        "statsUserUplink": true,          // DOCS: true/false. DEFAULT: false
        "statsUserDownlink": true,        // DOCS: true/false. DEFAULT: false
        "bufferSize": 10240               // DOCS: Connection buffer size in KB. DEFAULT: 10240
      },
      "1": {
        "handshake": 10,
        "connIdle": 3600
      }
    },
    "system": {
      "statsInboundUplink": true,         // DOCS: Track system upload (in). DEFAULT: false
      "statsInboundDownlink": true,       // DOCS: Track system download (in). DEFAULT: false
      "statsOutboundUplink": true,        // DOCS: Track system upload (out). DEFAULT: false
      "statsOutboundDownlink": true       // DOCS: Track system download (out). DEFAULT: false
    }
  },

  // ==========================================================
  // 7. INBOUNDS (Listeners)
  // How devices connect to your Xray server.
  // ==========================================================
  "inbounds": [
    {
      "tag": "api-in",                    // META: API Inbound identifier
      "listen": "127.0.0.1",              // CONF: Local interface. DEFAULT: "0.0.0.0"
      "port": 10085,                      // CONF: Port to listen on.
      "protocol": "dokodemo-door",        // DOCS: vless, vmess, trojan, shadowsocks, dokodemo-door, socks, http
      "settings": {
        "address": "127.0.0.1"            // DOCS: Internal routing for dokodemo-door
      }
    },
    {
      "tag": "proxy-in",
      "listen": "0.0.0.0",
      "port": 443,
      "protocol": "vless",                // DOCS: Flagship Xray protocol
      "settings": {
        "clients": [
          {
            "id": "b831381d-6324-4d53-ad4f-8cda48b30811", // UUID: Generated standard UUID
            "flow": "xtls-rprx-vision",                   // DOCS: xtls-rprx-vision, none. DEFAULT: ""
            "level": 0,                                   // DOCS: Links to Policy > Levels. DEFAULT: 0
            "email": "user@example.com"                   // META: Used for stats tracking
          }
        ],
        "decryption": "none",             // DOCS: none. DEFAULT: "none" (Mandatory for VLESS)
        "fallbacks": [
          { 
            "dest": 8080,                 // CONF: Port of fallback webserver (anti-probing)
            "xver": 1                     // DOCS: PROXY protocol version (0, 1, 2). DEFAULT: 0
          }
        ]
      },
      "streamSettings": {
        "network": "tcp",                 // DOCS: tcp, kcp, ws, http, domainsocket, quic, grpc. DEFAULT: "tcp"
        "security": "reality",            // DOCS: none, tls, reality. DEFAULT: "none"
        "realitySettings": {
          "show": false,                  // DOCS: true/false. Outputs reality debug info. DEFAULT: false
          "dest": "www.microsoft.com:443",// CONF: The SNI target to camouflage as.
          "xver": 0,                      // DOCS: PROXY protocol to dest. DEFAULT: 0
          "serverNames": ["www.microsoft.com"], // CONF: Allowed SNIs for clients
          "privateKey": "YOUR_PRIVATE_KEY",     // SOURCE: Generated via `xray x25519`
          "shortIds": ["shortId123", "a1b2c3d4"]// SOURCE: Generated via `openssl rand -hex 8`
        }
      },
      "sniffing": {
        "enabled": true,                  // DOCS: true/false. DEFAULT: false
        "destOverride": ["http", "tls", "quic", "fakedns"], // DOCS: Protocols to sniff.
        "metadataOnly": false,            // DOCS: true/false. DEFAULT: false
        "routeOnly": false                // DOCS: true/false. DEFAULT: false
      },
      "allocate": {
        "strategy": "always",             // DOCS: always, random. DEFAULT: "always"
        "refresh": 5,                     // DOCS: Minutes to refresh IP mapping
        "concurrency": 3                  // DOCS: Threads to allocate. DEFAULT: 3
      }
    },
    {
      "tag": "ws-in",
      "listen": "0.0.0.0",
      "port": 8443,
      "protocol": "vmess",                // DOCS: Legacy V2Ray protocol
      "settings": {
        "clients": [
          { 
            "id": "b831381d-6324-4d53-ad4f-8cda48b30811", // UUID: Generated
            "alterId": 0                  // DOCS: VMess AEAD requires 0. DEFAULT: 0
          }
        ]
      },
      "streamSettings": {
        "network": "ws",                  // DOCS: ws (WebSocket)
        "security": "tls",                // DOCS: tls
        "tlsSettings": {
          "certificates": [
            {
              "certificateFile": "/etc/xray/cert.crt", // CONF: Path to public cert
              "keyFile": "/etc/xray/key.key"           // CONF: Path to private key
            }
          ]
        },
        "wsSettings": {
          "path": "/xray-ws",             // CONF: WebSocket URI path
          "headers": { "Host": "my.cdn-domain.com" } // CONF: HTTP headers for WS upgrade
        }
      }
    }
  ],

  // ==========================================================
  // 8. OUTBOUNDS (Dialers)
  // How Xray connects to the internet or next-hop nodes.
  // ==========================================================
  "outbounds": [
    {
      "tag": "direct-out",                // META: Standard internet access
      "protocol": "freedom",              // DOCS: freedom, blackhole, vless, vmess, trojan, wireguard, shadowsocks
      "settings": {
        "domainStrategy": "UseIPv4",      // DOCS: AsIs, UseIP, UseIPv4, UseIPv6. DEFAULT: "AsIs"
        "noMatch": "drop"                 // DOCS: drop, "" (empty). DEFAULT: ""
      },
      "streamSettings": {
        "sockopt": { "mark": 255 }        // CONF: iptables fwmark (Linux specific)
      }
    },
    {
      "tag": "block-out",                 // META: Drop connections (e.g. ad blocking)
      "protocol": "blackhole",
      "settings": {
        "response": { "type": "http" }    // DOCS: none, http. DEFAULT: "none"
      }
    },
    {
      "tag": "dns-out",                   // META: DNS Hijacking outbound
      "protocol": "dns",
      "proxySettings": {
        "tag": "direct-out"               // CONF: Force DNS resolution through a specific outbound
      }
    },
    {
      "tag": "us-node-1",                 // META: WireGuard client implementation
      "protocol": "wireguard",
      "settings": {
        "secretKey": "YOUR_WG_PRIVATE_KEY", // CONF: Base64 WireGuard Private Key
        "address": ["10.0.0.2/32", "fd00::2/128"], // CONF: Internal WG IP
        "peers": [
          {
            "publicKey": "WG_SERVER_PUBLIC_KEY", // CONF: Base64 WireGuard Public Key
            "endpoint": "wg-server.com:51820",   // CONF: Server address:port
            "keepAlive": 25                      // DOCS: Keep-alive interval in seconds. DEFAULT: 0
          }
        ],
        "mtu": 1420                       // DOCS: Maximum Transmission Unit. DEFAULT: 1420
      }
    },
    {
      "tag": "us-node-2",                 // META: Outbound Xray Client (chaining nodes)
      "protocol": "vless",
      "settings": {
        "vnext": [
          {
            "address": "remote-server.com", // CONF: Target server IP/Domain
            "port": 443,                    // CONF: Target port
            "users": [
              {
                "id": "remote-uuid-here",   // UUID: Target server UUID
                "encryption": "none",       // DOCS: none. DEFAULT: "none"
                "flow": "xtls-rprx-vision"  // DOCS: xtls-rprx-vision, none.
              }
            ]
          }
        ]
      },
      "streamSettings": {
        "network": "tcp",
        "security": "tls",
        "tlsSettings": {
          "serverName": "remote-server.com", // CONF: SNI domain for TLS handshake
          "fingerprint": "chrome"            // DOCS: chrome, firefox, safari, randomized, etc. (uTLS)
        }
      },
      "mux": {
        "enabled": true,                  // DOCS: Enable connection multiplexing. DEFAULT: false
        "concurrency": 8,                 // DOCS: Max multiplexed streams per connection. DEFAULT: 8
        "xudpConcurrency": 16,            // DOCS: XUDP multiplexing for UDP traffic. DEFAULT: 8
        "xudpQuic": "quic"                // DOCS: inner transport for XUDP. quic, none.
      }
    }
  ],

  // ==========================================================
  // 9. TRANSPORT SETTINGS
  // Global defaults for stream settings.
  // ==========================================================
  "transport": {
    "tcpSettings": {
      "header": { "type": "none" },       // DOCS: none, http. DEFAULT: "none"
      "acceptProxyProtocol": false        // DOCS: true/false. DEFAULT: false
    },
    "kcpSettings": {
      "mtu": 1350,                        // DOCS: Maximum Transmission Unit. DEFAULT: 1350
      "tti": 50,                          // DOCS: Transmission Time Interval. DEFAULT: 50
      "uplinkCapacity": 100,              // DOCS: MB/s uplink. DEFAULT: 5
      "downlinkCapacity": 100,            // DOCS: MB/s downlink. DEFAULT: 20
      "congestion": true,                 // DOCS: true/false. DEFAULT: false
      "readBufferSize": 2,                // DOCS: MB. DEFAULT: 2
      "writeBufferSize": 2,               // DOCS: MB. DEFAULT: 2
      "header": { "type": "none" },       // DOCS: none, srtp, utp, wechat-video, dtls, wireguard. DEFAULT: "none"
      "seed": "optional_seed_string"      // CONF: Obfuscation password
    },
    "wsSettings": {
      "path": "/",                        // CONF: WebSocket Path. DEFAULT: "/"
      "headers": { "Host": "default.com" }, // CONF: WS Headers
      "acceptProxyProtocol": false        // DOCS: true/false. DEFAULT: false
    },
    "grpcSettings": {
      "serviceName": "default_service",   // CONF: gRPC Service name
      "multiMode": false,                 // DOCS: true/false. DEFAULT: false
      "idle_timeout": 60,                 // DOCS: timeout in seconds. DEFAULT: 60
      "health_check_timeout": 20,         // DOCS: timeout in seconds. DEFAULT: 20
      "permit_without_stream": false,     // DOCS: true/false. DEFAULT: false
      "initial_windows_size": 0           // DOCS: buffer size. DEFAULT: 0
    },
    "httpSettings": {
      "host": ["default.com"],            // CONF: HTTP/2 Hosts array
      "path": "/"                         // CONF: HTTP/2 Path
    },
    "dsSettings": {
      "path": "/var/run/xray.sock",       // CONF: Domain Socket path
      "abstract": false                   // DOCS: true/false (Linux abstract socket). DEFAULT: false
    },
    "quicSettings": {
      "security": "none",                 // DOCS: none, aes-128-gcm, chacha20-poly1305. DEFAULT: "none"
      "key": "quic_key",                  // CONF: Encryption key
      "header": { "type": "none" }        // DOCS: none, srtp, utp, wechat-video, dtls, wireguard. DEFAULT: "none"
    }
  },

  // ==========================================================
  // 10. REVERSE PROXY SETTINGS
  // Used to expose internal servers to the public via NAT.
  // ==========================================================
  "reverse": {
    "bridges": [
      {
        "tag": "bridge-out",              // META: Connects to inbound
        "domain": "internal.service.com"  // CONF: The domain mapping for the reverse proxy
      }
    ],
    "portals": [
      {
        "tag": "portal-in",               // META: Connects to outbound
        "domain": "internal.service.com"  // CONF: The domain mapping to accept connections from
      }
    ]
  },

  // ==========================================================
  // 11. OBSERVATORY SETTINGS
  // Actively probes connection quality of outbounds.
  // ==========================================================
  "observatory": {
    "subjectSelector": ["us-node-1", "us-node-2"], // CONF: Array of outbound tags to monitor
    "probeUrl": "https://www.google.com/generate_204", // CONF: The URL to test latency against
    "probeInterval": "1m",                // DOCS: Test frequency (e.g. "1m", "30s"). DEFAULT: "2h"
    "enableConcurrent": true              // DOCS: true/false. Ping all targets at once. DEFAULT: false
  },

  // ==========================================================
  // 12. METRICS SETTINGS
  // Exposes internal Xray states to Prometheus scrapers.
  // ==========================================================
  "metrics": {
    "tag": "metrics-out"                  // META: The tag of the outbound used to serve metrics
  },

  // ==========================================================
  // 13. STATS
  // Empty object required to initialize the internal stats engine.
  // ==========================================================
  "stats": {}                             // DOCS: Required to be present `{}` to enable policy > stats.
}
```