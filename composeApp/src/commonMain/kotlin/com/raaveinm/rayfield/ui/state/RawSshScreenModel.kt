package com.raaveinm.rayfield.ui.state

import androidx.compose.runtime.mutableStateListOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.raaveinm.rayfield.data.ssh.ConsoleMessage
import com.raaveinm.rayfield.data.ssh.ConsoleMessageType
import com.raaveinm.rayfield.data.ssh.ServerUnit
import com.raaveinm.rayfield.domain.ssh.SshClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RawSshScreenModel(
    private val sshClient: SshClient
) : ScreenModel {
    // В ScreenModel
    val history = mutableStateListOf<ConsoleMessage>()

    fun connect(server: ServerUnit?) {
        if (server == null) {
            history.add(ConsoleMessage("server is null", ConsoleMessageType.ERROR))
            return
        }
        val host = server.serverIp
        val port = server.serverSshPort
        val username = server.serverSshLogin
        val password = server.serverSshPassword
        val privateKey = server.serverSshPrivateKey?.toByteArray()

        screenModelScope.launch(Dispatchers.IO) {
            val connected = sshClient.connect(host, port, username, password, privateKey)
            withContext(Dispatchers.Main) {
                if (connected) {
                    history.add(
                        ConsoleMessage(
                            "Connected to $host:$port as $username",
                            ConsoleMessageType.OUTPUT
                        )
                    )
                } else {
                    history.add(
                        ConsoleMessage(
                            "Failed to connect to $host:$port as $username",
                            ConsoleMessageType.ERROR
                        )
                    )
                }
            }
        }
    }

    fun executeCommand(command: String){
        history.add(ConsoleMessage(command, ConsoleMessageType.COMMAND))
        screenModelScope.launch(Dispatchers.IO) {
            val result = sshClient.execute(command)
            withContext(Dispatchers.Main) {
                if (result.stdout.isNotBlank())
                    history.add(ConsoleMessage(result.stdout, ConsoleMessageType.OUTPUT))
                if (!result.error.isNullOrBlank() || result.exitCode != 0)
                    result.error?.let { history.add(ConsoleMessage(it, ConsoleMessageType.ERROR)) }
            }
        }
    }
}