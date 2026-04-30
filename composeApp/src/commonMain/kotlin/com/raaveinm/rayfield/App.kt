package com.raaveinm.rayfield

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.raaveinm.rayfield.domain.SshClient
import kotlinx.coroutines.launch

@Composable
fun App(sshClient: SshClient) {
    var consoleOutput by remember { mutableStateOf("IDLE") }
    val scope = rememberCoroutineScope()
    var serverIp by remember { mutableStateOf("") }
    var port by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var command by remember { mutableStateOf("") }

    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .safeContentPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Row(Modifier.fillMaxWidth().padding(16.dp)) {
                OutlinedTextField(
                    value = serverIp,
                    onValueChange = { serverIp = it },
                    label = { Text("Server IP") },
                    modifier = Modifier.weight(3f),
                    keyboardActions = KeyboardActions()
                )
                Spacer(Modifier.weight(.2f))
                OutlinedTextField(
                    value = port,
                    onValueChange = { port = it },
                    label = { Text("Port") },
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth().padding(16.dp),

            )
            OutlinedTextField(
                value = command,
                onValueChange = { command = it },
                label = { Text("Command") },
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )


            Button(
                onClick = {
                    scope.launch {
                        consoleOutput = "awaiting from $serverIp"
                        val isConnected = sshClient.connect(
                            host = serverIp,
                            port = port.toInt(),
                            username = username,
                            password = password
                        )
                        if (isConnected) {
                            consoleOutput = "executing command"
                            val result = sshClient.execute(command)

                            consoleOutput = """
                                code: ${result.exitCode}
                                output: ${result.stdout}
                                ${if (result.error != "") "stderr: ${result.error}" else ""}
                            """.trimIndent()

                            sshClient.disconnect()
                        } else {
                            consoleOutput = "err"
                        }
                    }
                }
            ) {
                Text("SSH checkup")
            }

            OutlinedTextField(
                value = consoleOutput,
                onValueChange = { consoleOutput = it },
                label = { Text("Console output") },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                readOnly = true
            )
        }
    }
}