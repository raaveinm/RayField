package com.raaveinm.rayfield.data.xray.types

//
// Created by Kirill "Raaveinm" on 4/29/26.
//

import kotlinx.serialization.Serializable

@Serializable
data class XrayKeyPair(
    val publicKey: String,
    val privateKey: String
)
