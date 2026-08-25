package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.SoundWaveApp
import com.example.data.local.LocalAudioScanner
import com.example.data.model.Playlist
import com.example.data.model.Track
import com.example.data.remote.FirebaseAudioRepository
import com.example.playback.MusicPlaybackManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class LibraryTab {
    CLOUD_STREAMING,
    LOCAL_DEVICE
}

class PlayerViewModel(
    private val playbackManager: MusicPlaybackManager = SoundWaveApp.instance.playbackManager,
    private val firebaseRepo: FirebaseAudioRepository = SoundWaveApp.instance.firebaseRepo,
    private val localScanner: LocalAudioScanner = SoundWaveApp.instance.localScanner
) : ViewModel() {

    // Playback state forwarded from manager
    val currentTrack: StateFlow<Track?> = playbackManager.currentTrack
    val isPlaying: StateFlow<Boolean> = playbackManager.isPlaying
    val isBuffering: StateFlow<Boolean> = playbackManager.isBuffering
    val currentPositionMs: StateFlow<Long> = playbackManager.currentPositionMs
    val durationMs: StateFlow<Long> = playbackManager.durationMs
    val isShuffleEnabled: StateFlow<Boolean> = playbackManager.isShuffleEnabled
    val repeatMode: StateFlow<Int> = playbackManager.repeatMode
    val currentPlaylist: StateFlow<List<Track>> = playbackManager.currentPlaylist

    // UI Navigation & Tab state
    private val _selectedTab = MutableStateFlow(LibraryTab.CLOUD_STREAMING)
    val selectedTab: StateFlow<LibraryTab> = _selectedTab.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isFullScreenPlayerOpen = MutableStateFlow(false)
    val isFullScreenPlayerOpen: StateFlow<Boolean> = _isFullScreenPlayerOpen.asStateFlow()

    private val _isSetupDialogOpen = MutableStateFlow(false)
    val isSetupDialogOpen: StateFlow<Boolean> = _isSetupDialogOpen.asStateFlow()

    private val _selectedPlaylistDetail = MutableStateFlow<Playlist?>(null)
    val selectedPlaylistDetail: StateFlow<Playlist?> = _selectedPlaylistDetail.asStateFlow()

    // Data lists
    private val _localTracks = MutableStateFlow<List<Track>>(emptyList())
    val localTracks: StateFlow<List<Track>> = _localTracks.asStateFlow()

    private val _isLocalScanning = MutableStateFlow(false)
    val isLocalScanning: StateFlow<Boolean> = _isLocalScanning.asStateFlow()

    private val _hasStoragePermission = MutableStateFlow(false)
    val hasStoragePermission: StateFlow<Boolean> = _hasStoragePermission.asStateFlow()

    private val _favoriteIds = MutableStateFlow<Set<String>>(setOf("cloud_1", "cloud_3"))
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    private val _seedStatusMessage = MutableStateFlow<String?>(null)
    val seedStatusMessage: StateFlow<String?> = _seedStatusMessage.asStateFlow()

    val cloudPlaylists: StateFlow<List<Playlist>> = firebaseRepo.fetchPlaylists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), firebaseRepo.defaultPlaylists)

    val cloudTracks: StateFlow<List<Track>> = firebaseRepo.fetchCloudTracks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), firebaseRepo.defaultCuratedTracks)

    private val currentTracksSource = combine(_selectedTab, _localTracks, cloudTracks) { tab, localList, cloudList ->
        if (tab == LibraryTab.LOCAL_DEVICE) localList else cloudList
    }

    // Filtered tracks based on tab, category, search query, and favorites
    val displayedTracks: StateFlow<List<Track>> = combine(
        currentTracksSource,
        _selectedTab,
        _selectedCategory,
        _searchQuery,
        _favoriteIds
    ) { sourceList, tab, category, query, favs ->
        var filtered = sourceList.map { track ->
            track.copy(isFavorite = favs.contains(track.id))
        }

        if (category == "Liked") {
            filtered = filtered.filter { it.isFavorite }
        } else if (category != "All" && tab == LibraryTab.CLOUD_STREAMING) {
            filtered = filtered.filter { it.category.equals(category, ignoreCase = true) }
        }

        if (query.isNotBlank()) {
            filtered = filtered.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.artist.contains(query, ignoreCase = true) ||
                it.album.contains(query, ignoreCase = true)
            }
        }

        filtered
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSelectedTab(tab: LibraryTab) {
        _selectedTab.value = tab
    }

    fun setSelectedCategory(cat: String) {
        _selectedCategory.value = cat
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun openFullScreenPlayer() {
        _isFullScreenPlayerOpen.value = true
    }

    fun closeFullScreenPlayer() {
        _isFullScreenPlayerOpen.value = false
    }

    fun openSetupDialog() {
        _isSetupDialogOpen.value = true
    }

    fun closeSetupDialog() {
        _isSetupDialogOpen.value = false
    }

    fun selectPlaylistDetail(playlist: Playlist?) {
        _selectedPlaylistDetail.value = playlist
    }

    fun setStoragePermissionGranted(granted: Boolean) {
        _hasStoragePermission.value = granted
        if (granted) {
            scanLocalMedia()
        }
    }

    fun scanLocalMedia() {
        viewModelScope.launch {
            _isLocalScanning.value = true
            val scanned = localScanner.scanLocalAudioFiles()
            _localTracks.value = scanned
            _isLocalScanning.value = false
        }
    }

    fun playTrack(track: Track, playlist: List<Track>? = null) {
        val targetPlaylist = playlist ?: displayedTracks.value.ifEmpty { listOf(track) }
        playbackManager.playTrack(track, targetPlaylist)
    }

    fun togglePlayPause() {
        playbackManager.togglePlayPause()
    }

    fun seekTo(positionMs: Long) {
        playbackManager.seekTo(positionMs)
    }

    fun skipNext() {
        playbackManager.skipToNext()
    }

    fun skipPrevious() {
        playbackManager.skipToPrevious()
    }

    fun toggleShuffle() {
        playbackManager.toggleShuffle()
    }

    fun toggleRepeat() {
        playbackManager.toggleRepeat()
    }

    fun toggleFavorite(trackId: String) {
        val current = _favoriteIds.value.toMutableSet()
        if (current.contains(trackId)) {
            current.remove(trackId)
        } else {
            current.add(trackId)
        }
        _favoriteIds.value = current
    }

    fun seedFirestoreData() {
        viewModelScope.launch {
            _seedStatusMessage.value = "Seeding data to Firestore..."
            val result = firebaseRepo.seedFirestoreData()
            result.fold(
                onSuccess = {
                    _seedStatusMessage.value = "Successfully seeded Cloud Playlists & Tracks!"
                },
                onFailure = {
                    _seedStatusMessage.value = "Seeding completed with local fallback: ${it.localizedMessage}"
                }
            )
        }
    }

    fun clearSeedStatus() {
        _seedStatusMessage.value = null
    }
}
