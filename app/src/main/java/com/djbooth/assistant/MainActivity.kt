package com.djbooth.assistant

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.djbooth.assistant.ui.screen.DJMainScreen
import com.djbooth.assistant.ui.theme.DJBoothAssistantTheme
import com.djbooth.assistant.ui.theme.DJDarkBackground
import com.djbooth.assistant.ui.viewmodel.DJDeckViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: DJDeckViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Forzar orientación horizontal para Tablets de DJ Booth
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        // Mantener la pantalla encendida durante la actuación en vivo
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContent {
            DJBoothAssistantTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DJDarkBackground
                ) {
                    DJMainScreen(viewModel = viewModel)
                }
            }
        }
    }
}
