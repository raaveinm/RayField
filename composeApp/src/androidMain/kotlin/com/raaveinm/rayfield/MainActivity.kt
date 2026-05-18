package com.raaveinm.rayfield

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.raaveinm.rayfield.ui.decoration.Circles
import com.raaveinm.rayfield.ui.decoration.circlesAndroid
import com.raaveinm.rayfield.ui.state.GlobalBlurHolder
import com.raaveinm.rayfield.ui.theme.RayFieldTheme
import io.github.neilyich.glassmorphism.blurredContent
import io.github.neilyich.glassmorphism.rememberBlurHolder
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
            val blurHolder = rememberBlurHolder()
            RayFieldTheme {
                CompositionLocalProvider(GlobalBlurHolder provides blurHolder) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .blurredContent(blurHolder)
                        ) { Circles(circlesAndroid()) }
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
}