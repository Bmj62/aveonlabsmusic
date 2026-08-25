package com.example.ui.screens.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SpotifyCard
import com.example.ui.theme.SpotifyDark
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.SpotifySubtext
import com.example.ui.theme.SpotifyText

@Composable
fun FirebaseSetupDialog(
    seedStatusMessage: String?,
    onDismiss: () -> Unit,
    onSeedData: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier
            .fillMaxWidth()
            .testTag("firebase_setup_dialog"),
        shape = RoundedCornerShape(24.dp),
        containerColor = SpotifyCard,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Setup Guide",
                        tint = SpotifyGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Firebase & Backend Setup",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SpotifyText
                        )
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_setup_dialog_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = SpotifySubtext
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "SoundWave is built with real Firebase Auth & Firestore integration. By default, it also includes high-quality streaming fallback tracks so you can play audio immediately!",
                    style = MaterialTheme.typography.bodyMedium.copy(color = SpotifySubtext, lineHeight = 20.sp)
                )

                // Instruction Card 1: google-services.json
                Card(
                    colors = CardDefaults.cardColors(containerColor = SpotifyDark),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "1. Firebase Project & Google Sign-In",
                            fontWeight = FontWeight.SemiBold,
                            color = SpotifyText,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "In Firebase Console > Authentication > Sign-in method, enable 'Google' and 'Email/Password'. Add your SHA-1 fingerprint and download 'google-services.json' into the '/app' directory.",
                            style = MaterialTheme.typography.bodySmall.copy(color = SpotifySubtext)
                        )
                    }
                }

                // Instruction Card 2: Firestore Collections Schema
                Card(
                    colors = CardDefaults.cardColors(containerColor = SpotifyDark),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "2. Firestore Schema",
                            fontWeight = FontWeight.SemiBold,
                            color = SpotifyText,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Collection 'tracks':",
                            fontWeight = FontWeight.Medium,
                            color = SpotifyGreen,
                            fontSize = 12.sp
                        )
                        Surface(
                            color = Color(0xFF1E1E1E),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = "{\n  \"title\": String,\n  \"artist\": String,\n  \"album\": String,\n  \"artworkUri\": String,\n  \"mediaUri\": String (https://),\n  \"durationMs\": Number,\n  \"category\": String\n}",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = Color(0xFF81C784),
                                modifier = Modifier.padding(8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Collection 'playlists':",
                            fontWeight = FontWeight.Medium,
                            color = SpotifyGreen,
                            fontSize = 12.sp
                        )
                        Surface(
                            color = Color(0xFF1E1E1E),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = "{\n  \"title\": String,\n  \"description\": String,\n  \"coverArtUrl\": String,\n  \"gradientColorHex\": String\n}",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = Color(0xFF81C784),
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }

                // Seed Action Button
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2E2442)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CloudSync,
                                contentDescription = null,
                                tint = SpotifyGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Quick Seed to Firestore",
                                fontWeight = FontWeight.SemiBold,
                                color = SpotifyText,
                                fontSize = 13.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "One-click populate your connected Firestore database with initial playlists and curated music streams.",
                            style = MaterialTheme.typography.bodySmall.copy(color = SpotifySubtext)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = onSeedData,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SpotifyGreen,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("seed_firestore_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudDone,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Populate Firestore Collections", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        if (seedStatusMessage != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = seedStatusMessage,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SpotifyGreen,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(50),
                modifier = Modifier.testTag("dismiss_dialog_button")
            ) {
                Text("Got It", color = SpotifyText)
            }
        }
    )
}
