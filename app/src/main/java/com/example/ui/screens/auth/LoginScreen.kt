package com.example.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.SpotifyCard
import com.example.ui.theme.SpotifyDark
import com.example.ui.theme.SpotifyDivider
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.SpotifyGreenBright
import com.example.ui.theme.SpotifySubtext
import com.example.ui.theme.SpotifyText
import com.example.ui.viewmodel.AuthUiState
import com.example.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onOpenSetupGuide: () -> Unit,
    onOpenThemeSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by authViewModel.uiState.collectAsState()
    val email by authViewModel.email.collectAsState()
    val password by authViewModel.password.collectAsState()
    val isSignUpMode by authViewModel.isSignUpMode.collectAsState()
    val showGoogleConfigDialog by authViewModel.showGoogleConfigDialog.collectAsState()
    val googleClientIdInput by authViewModel.googleClientIdInput.collectAsState()

    var passwordVisible by remember { mutableStateOf(false) }
    var tempClientId by remember(googleClientIdInput) { mutableStateOf(googleClientIdInput) }

    if (showGoogleConfigDialog) {
        AlertDialog(
            onDismissRequest = { authViewModel.closeGoogleConfigDialog() },
            modifier = Modifier.testTag("google_config_dialog"),
            shape = RoundedCornerShape(24.dp),
            containerColor = SpotifyCard,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GoogleLogo(modifier = Modifier.size(22.dp))
                    Text(
                        text = "Google Sign-In Setup",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SpotifyText
                        )
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "To enable Google Sign-In, please enter your Firebase Web Client ID or download your 'google-services.json' into the app.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = SpotifySubtext)
                    )

                    OutlinedTextField(
                        value = tempClientId,
                        onValueChange = { tempClientId = it },
                        label = { Text("Web Client ID (e.g. 1234...apps.googleusercontent.com)", fontSize = 12.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SpotifyGreen,
                            unfocusedBorderColor = SpotifyDivider,
                            focusedTextColor = SpotifyText,
                            unfocusedTextColor = SpotifyText,
                            focusedContainerColor = SpotifyDark,
                            unfocusedContainerColor = SpotifyDark
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("google_client_id_input")
                    )

                    Surface(
                        color = SpotifyDark,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Where to find your Client ID:",
                                fontWeight = FontWeight.SemiBold,
                                color = SpotifyText,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "1. Open Firebase Console > Authentication > Sign-in method\n2. Open 'Google' provider > Expand 'Web SDK configuration'\n3. Copy the 'Web client ID' and paste it above.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SpotifySubtext,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        authViewModel.saveGoogleClientId(context, tempClientId)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SpotifyGreen),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.testTag("save_google_client_id_button")
                ) {
                    Text(
                        text = if (tempClientId.isNotBlank()) "Save & Sign In" else "Save",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(
                        onClick = {
                            authViewModel.closeGoogleConfigDialog()
                            authViewModel.continueAsGuest()
                        },
                        modifier = Modifier.testTag("dialog_continue_guest_button")
                    ) {
                        Text("Use Guest", color = SpotifySubtext)
                    }
                    TextButton(
                        onClick = { authViewModel.closeGoogleConfigDialog() },
                        modifier = Modifier.testTag("close_google_config_button")
                    ) {
                        Text("Cancel", color = SpotifySubtext)
                    }
                }
            }
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF261C38),
                        SpotifyDark,
                        SpotifyDark
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        // Action buttons at Top Right
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onOpenThemeSettings,
                modifier = Modifier.testTag("auth_theme_settings_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = "Theme Settings",
                    tint = SpotifySubtext
                )
            }

            IconButton(
                onClick = onOpenSetupGuide,
                modifier = Modifier.testTag("auth_setup_guide_button")
            ) {
                Icon(
                    imageVector = Icons.Default.HelpOutline,
                    contentDescription = "Backend Guide",
                    tint = SpotifySubtext
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // App Brand Logo
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0xFF1E1B2E)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_aveonlabs_logo),
                    contentDescription = "aveonlabs MUSIC Logo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "aveonlabs MUSIC",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = SpotifyText,
                    letterSpacing = (-0.5).sp
                )
            )

            Text(
                text = if (isSignUpMode) "Create an account to sync your playlists" else "Discover and stream next-gen music everywhere.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = SpotifySubtext,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Error banner
            AnimatedVisibility(
                visible = uiState is AuthUiState.Error,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                val errorMsg = (uiState as? AuthUiState.Error)?.message ?: ""
                Surface(
                    color = Color(0xFF4A1A1A),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Text(
                        text = errorMsg,
                        color = Color(0xFFFFB4AB),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Email Input
            OutlinedTextField(
                value = email,
                onValueChange = {
                    authViewModel.email.value = it
                    authViewModel.clearError()
                },
                label = { Text("Email or username", color = SpotifySubtext) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = SpotifySubtext
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SpotifyGreen,
                    unfocusedBorderColor = SpotifyDivider,
                    focusedTextColor = SpotifyText,
                    unfocusedTextColor = SpotifyText,
                    focusedContainerColor = SpotifyCard,
                    unfocusedContainerColor = SpotifyCard
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("email_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Password Input
            OutlinedTextField(
                value = password,
                onValueChange = {
                    authViewModel.password.value = it
                    authViewModel.clearError()
                },
                label = { Text("Password", color = SpotifySubtext) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = SpotifySubtext
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = SpotifySubtext
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { authViewModel.submit() }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SpotifyGreen,
                    unfocusedBorderColor = SpotifyDivider,
                    focusedTextColor = SpotifyText,
                    unfocusedTextColor = SpotifyText,
                    focusedContainerColor = SpotifyCard,
                    unfocusedContainerColor = SpotifyCard
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("password_input")
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Primary Submit Button
            Button(
                onClick = { authViewModel.submit() },
                enabled = uiState !is AuthUiState.Loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SpotifyGreen,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_auth_button")
            ) {
                if (uiState is AuthUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.Black,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = if (isSignUpMode) "Sign Up" else "Log In",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Toggle Mode Button
            TextButton(
                onClick = {
                    authViewModel.toggleAuthMode()
                    authViewModel.clearError()
                },
                modifier = Modifier.testTag("toggle_auth_mode_button")
            ) {
                Text(
                    text = if (isSignUpMode) "Already have an account? Log In" else "Don't have an account? Sign Up Free",
                    color = SpotifyGreenBright,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Divider or separator
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(SpotifyDivider)
                )
                Text(
                    text = "OR",
                    color = SpotifySubtext,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(SpotifyDivider)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sign Up / Sign In with Google Button
            OutlinedButton(
                onClick = { authViewModel.signInWithGoogle(context) },
                enabled = uiState !is AuthUiState.Loading,
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.dp, SpotifyDivider),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = SpotifyCard,
                    contentColor = SpotifyText
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("google_auth_button")
            ) {
                GoogleLogo(modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (isSignUpMode) "Sign up with Google" else "Continue with Google",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Guest / Offline Mode Button
            OutlinedButton(
                onClick = { authViewModel.continueAsGuest() },
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.dp, Color(0xFF3B3842)),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = SpotifySubtext
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("guest_mode_button")
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = SpotifyGreen,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Continue as Guest",
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
fun GoogleLogo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        val radius = w * 0.42f
        val stroke = w * 0.20f

        val strokeStyle = Stroke(width = stroke)
        val arcSize = Size(radius * 2, radius * 2)
        val arcTopLeft = Offset(cx - radius, cy - radius)

        // Red Arc (Top)
        drawArc(
            color = Color(0xFFEA4335),
            startAngle = 215f,
            sweepAngle = 105f,
            useCenter = false,
            topLeft = arcTopLeft,
            size = arcSize,
            style = strokeStyle
        )

        // Yellow Arc (Left)
        drawArc(
            color = Color(0xFFFBBC05),
            startAngle = 135f,
            sweepAngle = 85f,
            useCenter = false,
            topLeft = arcTopLeft,
            size = arcSize,
            style = strokeStyle
        )

        // Green Arc (Bottom)
        drawArc(
            color = Color(0xFF34A853),
            startAngle = 45f,
            sweepAngle = 95f,
            useCenter = false,
            topLeft = arcTopLeft,
            size = arcSize,
            style = strokeStyle
        )

        // Blue Arc (Right)
        drawArc(
            color = Color(0xFF4285F4),
            startAngle = -40f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = arcTopLeft,
            size = arcSize,
            style = strokeStyle
        )

        // Blue horizontal crossbar
        drawRect(
            color = Color(0xFF4285F4),
            topLeft = Offset(cx - stroke * 0.2f, cy - stroke / 2),
            size = Size(radius + stroke * 0.2f, stroke)
        )
    }
}
