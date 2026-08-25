package com.example.ui.screens.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.data.model.Playlist
import com.example.data.model.Track
import com.example.ui.screens.player.MiniPlayer
import com.example.ui.screens.player.PlayerScreen
import com.example.ui.screens.playlist.PlaylistDetailScreen
import com.example.ui.screens.setup.FirebaseSetupDialog
import com.example.ui.theme.SpotifyCard
import com.example.ui.theme.SpotifyCardHover
import com.example.ui.theme.SpotifyDark
import com.example.ui.theme.SpotifyDivider
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.SpotifyGreenBright
import com.example.ui.theme.SpotifySubtext
import com.example.ui.theme.SpotifyText
import com.example.ui.viewmodel.AuthUiState
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.LibraryTab
import com.example.ui.viewmodel.PlayerViewModel
import com.example.ui.viewmodel.ThemeViewModel

@Composable
fun HomeScreen(
    playerViewModel: PlayerViewModel,
    authViewModel: AuthViewModel,
    themeViewModel: ThemeViewModel? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // State collections
    val authState by authViewModel.uiState.collectAsState()
    val selectedTab by playerViewModel.selectedTab.collectAsState()
    val selectedCategory by playerViewModel.selectedCategory.collectAsState()
    val searchQuery by playerViewModel.searchQuery.collectAsState()
    val displayedTracks by playerViewModel.displayedTracks.collectAsState()
    val cloudPlaylists by playerViewModel.cloudPlaylists.collectAsState()
    val localTracks by playerViewModel.localTracks.collectAsState()
    val isLocalScanning by playerViewModel.isLocalScanning.collectAsState()
    val hasStoragePermission by playerViewModel.hasStoragePermission.collectAsState()
    val favoriteIds by playerViewModel.favoriteIds.collectAsState()
    val isFullScreenPlayerOpen by playerViewModel.isFullScreenPlayerOpen.collectAsState()
    val isSetupDialogOpen by playerViewModel.isSetupDialogOpen.collectAsState()
    val seedStatusMessage by playerViewModel.seedStatusMessage.collectAsState()
    val selectedPlaylistDetail by playerViewModel.selectedPlaylistDetail.collectAsState()

    // Playback state
    val currentTrack by playerViewModel.currentTrack.collectAsState()
    val isPlaying by playerViewModel.isPlaying.collectAsState()
    val isBuffering by playerViewModel.isBuffering.collectAsState()
    val currentPositionMs by playerViewModel.currentPositionMs.collectAsState()
    val durationMs by playerViewModel.durationMs.collectAsState()
    val isShuffleEnabled by playerViewModel.isShuffleEnabled.collectAsState()
    val repeatMode by playerViewModel.repeatMode.collectAsState()

    // Permission launcher for audio scanning
    val permissionToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        playerViewModel.setStoragePermissionGranted(isGranted)
    }

    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(
            context,
            permissionToRequest
        ) == PackageManager.PERMISSION_GRANTED
        playerViewModel.setStoragePermissionGranted(granted)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SpotifyDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header Top Bar
            HomeTopBar(
                authState = authState,
                onOpenThemeSettings = { themeViewModel?.showThemeDialog() },
                onOpenSetupGuide = { playerViewModel.openSetupDialog() },
                onSignOut = { authViewModel.signOut() }
            )

            // Search Bar
            SearchBar(
                query = searchQuery,
                onQueryChange = { playerViewModel.setSearchQuery(it) }
            )

            // Tab Selector: Cloud Streaming vs Local Device Storage
            TabRow(
                selectedTabIndex = selectedTab.ordinal,
                containerColor = SpotifyDark,
                contentColor = SpotifyGreen,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                        color = SpotifyGreen,
                        height = 3.dp
                    )
                },
                divider = { Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(SpotifyDivider)) }
            ) {
                Tab(
                    selected = selectedTab == LibraryTab.CLOUD_STREAMING,
                    onClick = { playerViewModel.setSelectedTab(LibraryTab.CLOUD_STREAMING) },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CloudQueue,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Cloud Streaming",
                                fontWeight = if (selectedTab == LibraryTab.CLOUD_STREAMING) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == LibraryTab.CLOUD_STREAMING) SpotifyGreen else SpotifySubtext
                            )
                        }
                    },
                    modifier = Modifier.testTag("cloud_streaming_tab")
                )

                Tab(
                    selected = selectedTab == LibraryTab.LOCAL_DEVICE,
                    onClick = { playerViewModel.setSelectedTab(LibraryTab.LOCAL_DEVICE) },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Storage,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Local Storage",
                                fontWeight = if (selectedTab == LibraryTab.LOCAL_DEVICE) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == LibraryTab.LOCAL_DEVICE) SpotifyGreen else SpotifySubtext
                            )
                        }
                    },
                    modifier = Modifier.testTag("local_storage_tab")
                )
            }

            // Category Chips Row
            CategoryChipsRow(
                selectedTab = selectedTab,
                selectedCategory = selectedCategory,
                onSelectCategory = { playerViewModel.setSelectedCategory(it) }
            )

            // Main Content Area
            if (selectedTab == LibraryTab.CLOUD_STREAMING) {
                CloudStreamingContent(
                    playlists = cloudPlaylists,
                    tracks = displayedTracks,
                    currentPlayingTrack = currentTrack,
                    isPlaying = isPlaying,
                    favoriteIds = favoriteIds,
                    onPlayPlaylist = { playlist -> playerViewModel.selectPlaylistDetail(playlist) },
                    onPlayTrack = { track -> playerViewModel.playTrack(track) },
                    onToggleFavorite = { trackId -> playerViewModel.toggleFavorite(trackId) }
                )
            } else {
                LocalStorageContent(
                    hasPermission = hasStoragePermission,
                    isScanning = isLocalScanning,
                    tracks = displayedTracks,
                    currentPlayingTrack = currentTrack,
                    isPlaying = isPlaying,
                    favoriteIds = favoriteIds,
                    onRequestPermission = { permissionLauncher.launch(permissionToRequest) },
                    onRescan = { playerViewModel.scanLocalMedia() },
                    onPlayTrack = { track -> playerViewModel.playTrack(track) },
                    onToggleFavorite = { trackId -> playerViewModel.toggleFavorite(trackId) }
                )
            }
        }

        // Persistent Mini Player docked at the bottom
        MiniPlayer(
            currentTrack = currentTrack,
            isPlaying = isPlaying,
            isBuffering = isBuffering,
            currentPositionMs = currentPositionMs,
            durationMs = durationMs,
            isFavorite = currentTrack?.let { favoriteIds.contains(it.id) } ?: false,
            onTogglePlayPause = { playerViewModel.togglePlayPause() },
            onSkipNext = { playerViewModel.skipNext() },
            onToggleFavorite = { currentTrack?.let { playerViewModel.toggleFavorite(it.id) } },
            onExpand = { playerViewModel.openFullScreenPlayer() },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
        )

        // Full Screen Player Modal / Sheet
        AnimatedVisibility(
            visible = isFullScreenPlayerOpen,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            PlayerScreen(
                currentTrack = currentTrack,
                isPlaying = isPlaying,
                isBuffering = isBuffering,
                currentPositionMs = currentPositionMs,
                durationMs = durationMs,
                isShuffleEnabled = isShuffleEnabled,
                repeatMode = repeatMode,
                isFavorite = currentTrack?.let { favoriteIds.contains(it.id) } ?: false,
                onCollapse = { playerViewModel.closeFullScreenPlayer() },
                onTogglePlayPause = { playerViewModel.togglePlayPause() },
                onSeekTo = { playerViewModel.seekTo(it) },
                onSkipNext = { playerViewModel.skipNext() },
                onSkipPrevious = { playerViewModel.skipPrevious() },
                onToggleShuffle = { playerViewModel.toggleShuffle() },
                onToggleRepeat = { playerViewModel.toggleRepeat() },
                onToggleFavorite = { currentTrack?.let { playerViewModel.toggleFavorite(it.id) } }
            )
        }

        // Playlist Detail Screen
        selectedPlaylistDetail?.let { playlist ->
            PlaylistDetailScreen(
                playlist = playlist,
                currentPlayingTrack = currentTrack,
                isPlaying = isPlaying,
                onBack = { playerViewModel.selectPlaylistDetail(null) },
                onPlayTrack = { track, list -> playerViewModel.playTrack(track, list) }
            )
        }

        // Firebase Setup & Seed Dialog
        if (isSetupDialogOpen) {
            FirebaseSetupDialog(
                seedStatusMessage = seedStatusMessage,
                onDismiss = {
                    playerViewModel.closeSetupDialog()
                    playerViewModel.clearSeedStatus()
                },
                onSeedData = { playerViewModel.seedFirestoreData() }
            )
        }
    }
}

@Composable
private fun HomeTopBar(
    authState: AuthUiState,
    onOpenThemeSettings: () -> Unit = {},
    onOpenSetupGuide: () -> Unit,
    onSignOut: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
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
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "aveonlabs MUSIC",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = SpotifyText
                    )
                )
                val userLabel = when (authState) {
                    is AuthUiState.Authenticated -> authState.user?.displayName?.takeIf { it.isNotBlank() } ?: authState.user?.email ?: "User"
                    is AuthUiState.Guest -> "Guest Listener"
                    else -> "Offline"
                }
                Text(
                    text = userLabel,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SpotifyGreenBright,
                        fontSize = 11.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onOpenThemeSettings,
                modifier = Modifier.testTag("home_theme_settings_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = "Theme Settings",
                    tint = SpotifySubtext
                )
            }

            IconButton(
                onClick = onOpenSetupGuide,
                modifier = Modifier.testTag("home_setup_guide_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                    contentDescription = "Backend Guide",
                    tint = SpotifySubtext
                )
            }

            IconButton(
                onClick = onSignOut,
                modifier = Modifier.testTag("home_sign_out_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Sign Out",
                    tint = SpotifySubtext
                )
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("What do you want to listen to?", color = SpotifySubtext, fontSize = 13.sp) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = SpotifySubtext,
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear search",
                        tint = SpotifySubtext,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = SpotifyGreen,
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = SpotifyCard,
            unfocusedContainerColor = SpotifyCard,
            focusedTextColor = SpotifyText,
            unfocusedTextColor = SpotifyText
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .height(50.dp)
            .testTag("home_search_bar")
    )
}

@Composable
private fun CategoryChipsRow(
    selectedTab: LibraryTab,
    selectedCategory: String,
    onSelectCategory: (String) -> Unit
) {
    val categories = if (selectedTab == LibraryTab.CLOUD_STREAMING) {
        listOf("All", "Bollywood", "South Music", "Lo-Fi", "Synthwave", "Electronic", "Acoustic", "Liked")
    } else {
        listOf("All", "Liked")
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { cat ->
            val isSelected = selectedCategory == cat
            FilterChip(
                selected = isSelected,
                onClick = { onSelectCategory(cat) },
                label = {
                    Text(
                        text = cat,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.Black else SpotifyText
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = SpotifyGreen,
                    containerColor = SpotifyCard
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = if (isSelected) SpotifyGreen else SpotifyDivider
                ),
                shape = RoundedCornerShape(50)
            )
        }
    }
}

@Composable
private fun CloudStreamingContent(
    playlists: List<Playlist>,
    tracks: List<Track>,
    currentPlayingTrack: Track?,
    isPlaying: Boolean,
    favoriteIds: Set<String>,
    onPlayPlaylist: (Playlist) -> Unit,
    onPlayTrack: (Track) -> Unit,
    onToggleFavorite: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("cloud_tracks_list"),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // Featured Playlists Section
        item {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                Text(
                    text = "Featured Playlists",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = SpotifyText
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(playlists) { playlist ->
                        PlaylistCard(
                            playlist = playlist,
                            onClick = { onPlayPlaylist(playlist) }
                        )
                    }
                }
            }
        }

        // Tracks Section Header
        item {
            Text(
                text = "Cloud Tracks & Hits",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = SpotifyText
                ),
                modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 8.dp)
            )
        }

        // Tracks list
        itemsIndexed(tracks) { index, track ->
            TrackListItem(
                index = index,
                track = track,
                isCurrent = currentPlayingTrack?.id == track.id,
                isPlaying = isPlaying,
                isFavorite = favoriteIds.contains(track.id),
                onClick = { onPlayTrack(track) },
                onToggleFavorite = { onToggleFavorite(track.id) }
            )
        }
    }
}

@Composable
private fun LocalStorageContent(
    hasPermission: Boolean,
    isScanning: Boolean,
    tracks: List<Track>,
    currentPlayingTrack: Track?,
    isPlaying: Boolean,
    favoriteIds: Set<String>,
    onRequestPermission: () -> Unit,
    onRescan: () -> Unit,
    onPlayTrack: (Track) -> Unit,
    onToggleFavorite: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("local_storage_content_list"),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        if (!hasPermission) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SpotifyCard),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.FolderSpecial,
                            contentDescription = null,
                            tint = SpotifyGreen,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Audio Permission Required",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SpotifyText
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "To play your on-device music files (.mp3, .m4a, .flac), grant permission to scan local audio storage.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SpotifySubtext,
                                textAlign = TextAlign.Center
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onRequestPermission,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SpotifyGreen,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.testTag("request_storage_permission_button")
                        ) {
                            Text("Grant Storage Access", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            // Status & Scan Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${tracks.size} local audio files found",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = SpotifySubtext,
                            fontWeight = FontWeight.Medium
                        )
                    )

                    Button(
                        onClick = onRescan,
                        enabled = !isScanning,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SpotifyCard,
                            contentColor = SpotifyGreen
                        ),
                        shape = RoundedCornerShape(50),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("rescan_local_audio_button")
                    ) {
                        if (isScanning) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = SpotifyGreen,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Rescan",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Rescan Device", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            if (tracks.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 60.dp, start = 24.dp, end = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = SpotifySubtext,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Audio Files Found",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SpotifyText
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Copy .mp3 or audio tracks to your device's Music/Download folder, then tap Rescan Device.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SpotifySubtext,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }
            } else {
                itemsIndexed(tracks) { index, track ->
                    TrackListItem(
                        index = index,
                        track = track,
                        isCurrent = currentPlayingTrack?.id == track.id,
                        isPlaying = isPlaying,
                        isFavorite = favoriteIds.contains(track.id),
                        onClick = { onPlayTrack(track) },
                        onToggleFavorite = { onToggleFavorite(track.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PlaylistCard(
    playlist: Playlist,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SpotifyCard),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .width(140.dp)
            .clickable { onClick() }
            .testTag("playlist_card_${playlist.id}")
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF282828)),
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
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = playlist.title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = SpotifyText
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = playlist.description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = SpotifySubtext,
                    fontSize = 11.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun TrackListItem(
    index: Int,
    track: Track,
    isCurrent: Boolean,
    isPlaying: Boolean,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("track_list_item_${track.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Track Thumbnail
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF222222)),
            contentAlignment = Alignment.Center
        ) {
            if (!track.artworkUri.isNullOrBlank()) {
                AsyncImage(
                    model = track.artworkUri,
                    contentDescription = track.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = SpotifyGreen,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Title and Artist
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
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${track.artist} • ${if (track.isLocal) "Device" else track.category}",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = SpotifySubtext,
                    fontSize = 12.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Equalizer indicator if current playing
        if (isCurrent && isPlaying) {
            Icon(
                imageVector = Icons.Default.GraphicEq,
                contentDescription = "Playing",
                tint = SpotifyGreenBright,
                modifier = Modifier
                    .size(20.dp)
                    .padding(end = 4.dp)
            )
        }

        // Favorite Toggle Button
        IconButton(
            onClick = onToggleFavorite,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = if (isFavorite) "Liked" else "Like",
                tint = if (isFavorite) SpotifyGreen else SpotifySubtext,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
