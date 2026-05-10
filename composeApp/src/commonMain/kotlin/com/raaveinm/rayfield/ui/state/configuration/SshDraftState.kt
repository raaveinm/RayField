package com.raaveinm.rayfield.ui.state.configuration

//
// Created by Kirill "Raaveinm" on 5/7/26.
//
data class SshDraftState(
    val serverId: String = "",
    val configId: String = "",
    val name: String = "",
    val ip: String = "",
    val login: String = "root",
    val password: String? = "",
    val pathToPkey: String? = "",
    val port: String = "22",
    val isLoading: Boolean = true,
    val isSaved: Boolean = false
)

sealed interface SshIntent {
    data class UpdateName(val name: String) : SshIntent
    data class UpdateIp(val ip: String) : SshIntent
    data class UpdateLogin(val login: String) : SshIntent
    data class UpdatePassword(val password: String) : SshIntent
    data class UpdatePort(val port: String) : SshIntent
    data class UpdatePathToPkey(val pathToPkey: String) : SshIntent
    data object Save : SshIntent
}
