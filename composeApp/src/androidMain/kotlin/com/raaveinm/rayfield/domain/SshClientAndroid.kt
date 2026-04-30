package com.raaveinm.rayfield.domain

import com.raaveinm.rayfield.data.CommandResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.transport.verification.PromiscuousVerifier

//
// Created by Kirill "Raaveinm" on 4/29/26.
//

class SshClientAndroid : SshClient {

    private var client: SSHClient? = null

    override suspend fun connect(
        host: String,
        port: Int,
        username: String,
        password: String?,
        privateKey: ByteArray?
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            client = SSHClient().apply {
                addHostKeyVerifier(PromiscuousVerifier()) // Host key not verifiable
                connect(host, port)
                when {
                    password != null -> authPassword(username, password)
                    privateKey != null -> {
                        val keyProvider = loadKeys(String(privateKey), null, null)
                        authPublickey(username, keyProvider)
                    }
                    else -> throw IllegalArgumentException("Either password or privateKey must be provided")
                }
            }
            return@withContext (client?.isAuthenticated == true)
        } catch (e: Exception) {
            e.printStackTrace()
            disconnect()
            return@withContext false
        }
    }

    override suspend fun execute(command: String): CommandResult = withContext(Dispatchers.IO) {
        val session = client?.startSession() ?: throw IllegalStateException("Client not connected")

        try {
            val cmd = session.exec(command)

            val stdout = cmd.inputStream.bufferedReader().readText()
            val error = cmd.errorStream.bufferedReader().readText()

            cmd.join()
            val exitCode = cmd.exitStatus ?: -1

            return@withContext CommandResult(exitCode, stdout, error)
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        } finally {
            session.close()
        }
    }

    override suspend fun disconnect(): Unit = withContext(Dispatchers.IO) {
        try {
            client?.disconnect()
            client?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            client = null
        }
    }
}