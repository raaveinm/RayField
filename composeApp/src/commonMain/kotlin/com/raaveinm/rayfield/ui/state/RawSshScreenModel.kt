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
    val history = mutableStateListOf<ConsoleMessage>()

    lateinit var host: String
    var port: Int = 22
    lateinit var username: String
    var password: String? = null

    fun connect(server: ServerUnit?) {
        if (server == null) {
            history.add(ConsoleMessage("server is null", ConsoleMessageType.ERROR))
            return
        }
        host = server.serverIp
        port = server.serverSshPort
        username = server.serverSshLogin
        password = server.serverSshPassword
        val privateKey = server.serverSshPrivateKey?.toByteArray()

        screenModelScope.launch(Dispatchers.IO) {
            val connected = sshClient.connect(host, port, username, password, privateKey)
            withContext(Dispatchers.Main) {
                if (connected) {
                    history.add(
                        ConsoleMessage(
                            "-- system --> Connected to $username@$host:$port",
                            ConsoleMessageType.OUTPUT
                        )
                    )
                } else {
                    history.add(
                        ConsoleMessage(
                            "-- system --> Failed to connect to $host:$port as $username",
                            ConsoleMessageType.ERROR
                        )
                    )
                }
            }
        }
    }

    fun executeCommand(command: String){
        history.add(ConsoleMessage("-- $username --> $command", ConsoleMessageType.COMMAND))
        screenModelScope.launch(Dispatchers.IO) {
            val result = sshClient.execute(command)
            withContext(Dispatchers.Main) {
                if (result.stdout.isNotBlank())
                    history.add(ConsoleMessage("-- $host --> ${ result.stdout}", ConsoleMessageType.OUTPUT))
                if (!result.error.isNullOrBlank() || result.exitCode != 0)
                    result.error?.let { history.add(ConsoleMessage("-- $host --> $it", ConsoleMessageType.ERROR)) }
            }
        }
    }
}