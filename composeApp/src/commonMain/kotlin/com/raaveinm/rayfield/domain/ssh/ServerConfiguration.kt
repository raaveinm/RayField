package com.raaveinm.rayfield.domain.ssh

import com.raaveinm.rayfield.data.ssh.CommandResult
import com.raaveinm.rayfield.domain.helpers.Logger

class ServerConfiguration {
    private val sshClient = SshClient()
    private var serverSshPassword: String? = null
    private var serverSshPrivateKey: String? = null
    private var serverIp: String? = null
    private var serverPort: Int = 22
    private var sshUsername: String? = null

    suspend fun setupConnection (
        serverIp: String,
        serverPort: Int? = null,
        sshUsername: String,
        sshPassword: String? = null,
        sshPrivateKey: String? = null
    ) {
        this.serverIp = serverIp
        this.serverPort = serverPort ?: this.serverPort
        this.sshUsername = sshUsername
        this.serverSshPassword = sshPassword
        this.serverSshPrivateKey = sshPrivateKey

        if (sshClient.connect(
                host = serverIp,
                port = serverPort ?: 22,
                username = sshUsername,
                password = sshPassword
            )
        ) {
            Logger.i("ServerConfig", "Connection successful")
        } else {
            Logger.e("ServerConfig", "Connection failed, checkup data & " +
                    "connection (internet permission)")
        }
    }

    private fun sudo(command: String): String {
        return if (sshUsername == "root") {
            command
        } else if (!serverSshPassword.isNullOrBlank()) {
            // Use Bash Here-String (<<<) for reliable stdin injection across all SSH clients
            "sudo -S $command <<< '$serverSshPassword'"
        } else {
            "sudo $command"
        }
    }

    suspend fun runInstallation () : CommandResult {
        val command = """
            export DEBIAN_FRONTEND=noninteractive
            ${sudo("apt-get update -y -q")}
            ${sudo("apt-get upgrade -y -q")}
            ${sudo("apt-get install -y -q docker.io docker-compose-v2 ufw curl")}
            ${sudo("systemctl enable --now docker")}
            ${sudo("ufw allow 22/tcp")}
            ${sudo("ufw allow 443/tcp")}
            ${sudo("ufw --force enable")}
        """.trimIndent()

        return sshClient.execute(command)
    }

    suspend fun runConfiguration(json: String) : CommandResult {
        val command = """
            |mkdir -p ~/xray-docker
            |
            |cat << 'EOF' > ~/xray-docker/config.json
            |$json
            |EOF
            |
            |${sudo("mkdir -p /etc/xray")}
            |${sudo("mv ~/xray-docker/config.json /etc/xray/config.json")}
            |
            |cat << 'EOF' > ~/xray-docker/docker-compose.yml
            |services:
            |  xray:
            |    image: teddysun/xray:latest
            |    container_name: xray
            |    restart: unless-stopped
            |    network_mode: "host"
            |    volumes:
            |      - /etc/xray:/etc/xray
            |      - /etc/localtime:/etc/localtime:ro
            |EOF
        """.trimMargin()
        return sshClient.execute(command)
    }

    suspend fun reassembleConfiguration(json: String) : CommandResult {
        val command = """
            |# 1. Forcefully kill and remove the old container to prevent naming conflicts
            |${sudo("docker stop xray || true")}
            |${sudo("docker rm xray || true")}
            |
            |# 2. Write the new JSON config to the user's home directory (avoids sudo stdin clashes)
            |cat << 'EOF' > ~/xray-docker/config.json
            |$json
            |EOF
            |
            |# 3. Move the configuration file to the protected directory
            |${sudo("mv ~/xray-docker/config.json /etc/xray/config.json")}
        """.trimMargin()

        return sshClient.execute(command)
    }

    suspend fun startConfiguration() : CommandResult {
        val command = "cd ~/xray-docker && ${sudo("docker compose pull")} && ${sudo("docker compose up -d")}"
        return sshClient.execute(command)
    }

    suspend fun verifyInstallation() : Boolean {
        return sshClient.execute(sudo("docker ps | grep xray")).exitCode == 0
    }

    suspend fun terminateSession() {
        sshClient.disconnect()
    }
}