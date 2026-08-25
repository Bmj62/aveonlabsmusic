package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.auth.LoginScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.settings.ThemeSettingsDialog
import com.example.ui.theme.SoundWaveTheme
import com.example.ui.viewmodel.AuthUiState
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.PlayerViewModel
import com.example.ui.viewmodel.ThemeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeViewModel: ThemeViewModel = viewModel()
            val currentMode by themeViewModel.themeMode.collectAsState()
            val isDynamic by themeViewModel.dynamicColor.collectAsState()

            SoundWaveTheme(
                themeMode = currentMode,
                dynamicColor = isDynamic
            ) {
                SoundWaveMainContent(themeViewModel = themeViewModel)
            }
        }
    }
}

@Composable
fun SoundWaveMainContent(
    authViewModel: AuthViewModel = viewModel(),
    playerViewModel: PlayerViewModel = viewModel(),
    themeViewModel: ThemeViewModel = viewModel()
) {
    val authState by authViewModel.uiState.collectAsState()
    val isThemeDialogVisible by themeViewModel.isThemeDialogVisible.collectAsState()

    if (isThemeDialogVisible) {
        ThemeSettingsDialog(
            themeViewModel = themeViewModel,
            onDismiss = { themeViewModel.hideThemeDialog() }
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Crossfade(
            targetState = authState,
            label = "auth_crossfade"
        ) { state ->
            when (state) {
                is AuthUiState.Authenticated, is AuthUiState.Guest -> {
                    HomeScreen(
                        playerViewModel = playerViewModel,
                        authViewModel = authViewModel,
                        themeViewModel = themeViewModel
                    )
                }
                else -> {
                    LoginScreen(
                        authViewModel = authViewModel,
                        onOpenSetupGuide = { playerViewModel.openSetupDialog() },
                        onOpenThemeSettings = { themeViewModel.showThemeDialog() }
                    )
                }
            }
        }
    }
}


