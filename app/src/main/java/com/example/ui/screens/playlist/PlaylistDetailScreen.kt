package com.example.ui.screens.playlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Playlist
import com.example.data.model.Track
import com.example.ui.theme.SpotifyCard
import com.example.ui.theme.SpotifyDark
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.SpotifyGreenBright
import com.example.ui.theme.SpotifySubtext
import com.example.ui.theme.SpotifyText

@Composable
fun PlaylistDetailScreen(
    playlist: Playlist,
    currentPlayingTrack: Track?,
    isPlaying: Boolean,
    onBack: () -> Unit,
    onPlayTrack: (Track, List<Track>) -> Unit,
    modifier: Modifier = Modifier
) {
    val gradientColor = try {
        Color(android.graphics.Color.parseColor(playlist.gradientColorHex))
    } catch (e: Exception) {
        SpotifyGreen
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SpotifyDark)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("playlist_detail_list")
        ) {
            // Header with Gradient and Large Artwork
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    gradientColor.copy(alpha = 0.8f),
                                    gradientColor.copy(alpha = 0.3f),
                                    SpotifyDark
                                )
                            )
                        )
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Column {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("playlist_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = SpotifyText
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Cover Artwork
                            Box(
                                modifier = Modifier
                                    .size(130.dp)
                                    .aspectRatio(1f)
                                    .shadow(12.dp, RoundedCornerShape(12.dp))
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF242424)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (playlist.coverArtUrl.isNotBlank()) {
                                    AsyncImage(
                                        model = playlist.coverArtUrl,
                                        contentDescription = playlist.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.MusicNote,
                                        contentDescription = null,
                                        tint = SpotifyGreen,
                                        modifier = Modifier.size(48.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "PLAYLIST",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = SpotifySubtext
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = playlist.title,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SpotifyText
                                    ),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = playlist.description,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = SpotifySubtext,
                                        lineHeight = 16.sp
                                    ),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Controls Row (Play all button)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${playlist.tracks.size} songs • SoundWave Cloud",
                                style = MaterialTheme.typography.bodySmall.copy(color = SpotifySubtext)
                            )

                            FloatingActionButton(
                                onClick = {
                                    if (playlist.tracks.isNotEmpty()) {
                                        onPlayTrack(playlist.tracks.first(), playlist.tracks)
                                    }
                                },
                                shape = CircleShape,
                                containerColor = SpotifyGreen,
                                contentColor = Color.Black,
                                modifier = Modifier
                                    .size(52.dp)
                                    .testTag("play_all_playlist_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play All",
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Track Items List
            itemsIndexed(playlist.tracks) { index, track ->
                val isCurrent = currentPlayingTrack?.id == track.id

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPlayTrack(track, playlist.tracks) }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                        .testTag("playlist_track_item_$index"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${index + 1}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (isCurrent) SpotifyGreenBright else SpotifySubtext,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.width(28.dp)
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = track.title,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = if (isCurrent) SpotifyGreenBright else SpotifyText
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = track.artist,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SpotifySubtext,
                                fontSize = 12.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    if (isCurrent && isPlaying) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "Playing",
                            tint = SpotifyGreenBright,
                            modifier = Modifier
                                .size(20.dp)
                                .padding(end = 8.dp)
                        )
                    }

                    Text(
                        text = track.getFormattedDuration(),
                        style = MaterialTheme.typography.bodySmall.copy(color = SpotifySubtext)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}
