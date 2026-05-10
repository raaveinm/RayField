package com.raaveinm.rayfield

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.raaveinm.rayfield.ui.theme.RayFieldTheme
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        Security.removeProvider("BC")
        Security.insertProviderAt(BouncyCastleProvider(), 1)

        val data = intent?.data
        val configId = data?.getQueryParameter("configId")
        val serverId = data?.getQueryParameter("serverId")

        setContent {
            RayFieldTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                ) { innerPadding ->
                    App(
                        modifier = Modifier.fillMaxSize().padding(innerPadding),
                        initialConfigId = configId,
                        initialServerId = serverId
                    )
                }
            }
        }
    }
}