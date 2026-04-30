package com.raaveinm.rayfield.domain

import com.raaveinm.rayfield.data.XrayKeyPair
import org.bouncycastle.crypto.generators.X25519KeyPairGenerator
import org.bouncycastle.crypto.params.X25519KeyGenerationParameters
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters
import org.bouncycastle.crypto.params.X25519PublicKeyParameters
import java.security.SecureRandom
import java.util.Base64
import java.util.UUID

//
// Created by Kirill "Raaveinm" on 4/29/26.
//

class AndroidCypherService : CypherService {
    private val secureRandom = SecureRandom()

    override fun generateUuid(): String {
        return UUID.randomUUID().toString()
    }

    override fun generateShortId(): String {
        val bytes = ByteArray(8)
        secureRandom.nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    override fun generateKeyPair(): XrayKeyPair {
        val generator = X25519KeyPairGenerator()
        generator.init(X25519KeyGenerationParameters(secureRandom))
        val keyPair = generator.generateKeyPair()

        val privateBytes = (keyPair.private as X25519PrivateKeyParameters).encoded
        val pubBytes = (keyPair.public as X25519PublicKeyParameters).encoded
        val encoder = Base64.getUrlEncoder().withoutPadding()

        return XrayKeyPair(
            publicKey = encoder.encodeToString(pubBytes),
            privateKey = encoder.encodeToString(privateBytes)
        )
    }
}