package com.raaveinm.rayfield

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.raaveinm.rayfield.domain.AndroidCypherService
import com.raaveinm.rayfield.domain.SshClientAndroid
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        Security.removeProvider("BC")
        Security.insertProviderAt(BouncyCastleProvider(), 1)
        val client = SshClientAndroid()

        val cryptoService = AndroidCypherService()
        val keys = cryptoService.generateKeyPair()

        setContent {
            Column {
                Spacer(Modifier.padding(top = 32.dp))
                Text(
                    text = " UUID: ${cryptoService.generateUuid()} \n ShortID: ${cryptoService.generateShortId()} \n Private Key: ${keys.privateKey} \n Public Key: ${keys.publicKey}",
                    modifier = Modifier.padding(16.dp),
                    color = androidx.compose.ui.graphics.Color.Blue
                )
                App(client)
            }
        }
    }
}