package com.example.playback

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.data.model.Track
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MusicPlaybackManager(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null

    private val _currentTrack = MutableStateFlow<Track?>(null)
    val currentTrack: StateFlow<Track?> = _currentTrack.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isBuffering = MutableStateFlow(false)
    val isBuffering: StateFlow<Boolean> = _isBuffering.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0L)
    val currentPositionMs: StateFlow<Long> = _currentPositionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(0L)
    val durationMs: StateFlow<Long> = _durationMs.asStateFlow()

    private val _isShuffleEnabled = MutableStateFlow(false)
    val isShuffleEnabled: StateFlow<Boolean> = _isShuffleEnabled.asStateFlow()

    private val _repeatMode = MutableStateFlow(Player.REPEAT_MODE_OFF)
    val repeatMode: StateFlow<Int> = _repeatMode.asStateFlow()

    private val _currentPlaylist = MutableStateFlow<List<Track>>(emptyList())
    val currentPlaylist: StateFlow<List<Track>> = _currentPlaylist.asStateFlow()

    private var progressJob: Job? = null

    init {
        initializeController()
    }

    private fun initializeController() {
        val sessionToken = SessionToken(
            context,
            ComponentName(context, PlaybackService::class.java)
        )
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture?.addListener({
            try {
                mediaController = controllerFuture?.get()
                setupPlayerListener()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, MoreExecutors.directExecutor())
    }

    private fun setupPlayerListener() {
        val controller = mediaController ?: return
        controller.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
                if (isPlaying) {
                    startProgressTracker()
                } else {
                    stopProgressTracker()
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                _isBuffering.value = playbackState == Player.STATE_BUFFERING
                if (playbackState == Player.STATE_READY) {
                    _durationMs.value = controller.duration.coerceAtLeast(0L)
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                val mediaId = mediaItem?.mediaId
                val track = _currentPlaylist.value.find { it.id == mediaId }
                if (track != null) {
                    _currentTrack.value = track
                }
                _durationMs.value = controller.duration.coerceAtLeast(0L)
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                _isShuffleEnabled.value = shuffleModeEnabled
            }

            override fun onRepeatModeChanged(newRepeatMode: Int) {
                _repeatMode.value = newRepeatMode
            }
        })

        _isPlaying.value = controller.isPlaying
        _isShuffleEnabled.value = controller.shuffleModeEnabled
        _repeatMode.value = controller.repeatMode
    }

    fun playTrack(track: Track, playlist: List<Track> = listOf(track)) {
        val controller = mediaController ?: return
        _currentPlaylist.value = playlist
        _currentTrack.value = track

        val mediaItems = playlist.map { item ->
            val metadata = MediaMetadata.Builder()
                .setTitle(item.title)
                .setArtist(item.artist)
                .setAlbumTitle(item.album)
                .setArtworkUri(item.artworkUri?.let { Uri.parse(it) })
                .build()

            MediaItem.Builder()
                .setMediaId(item.id)
                .setUri(Uri.parse(item.mediaUri))
                .setMediaMetadata(metadata)
                .build()
        }

        val targetIndex = playlist.indexOfFirst { it.id == track.id }.coerceAtLeast(0)

        controller.setMediaItems(mediaItems, targetIndex, 0L)
        controller.prepare()
        controller.play()
    }

    fun togglePlayPause() {
        val controller = mediaController ?: return
        if (controller.isPlaying) {
            controller.pause()
        } else {
            if (controller.mediaItemCount == 0 && _currentTrack.value != null) {
                _currentTrack.value?.let { playTrack(it, _currentPlaylist.value) }
            } else {
                controller.play()
            }
        }
    }

    fun seekTo(positionMs: Long) {
        val controller = mediaController ?: return
        controller.seekTo(positionMs)
        _currentPositionMs.value = positionMs
    }

    fun skipToNext() {
        val controller = mediaController ?: return
        if (controller.hasNextMediaItem()) {
            controller.seekToNextMediaItem()
        } else if (_currentPlaylist.value.isNotEmpty()) {
            val currentIndex = _currentPlaylist.value.indexOfFirst { it.id == _currentTrack.value?.id }
            val nextIndex = (currentIndex + 1) % _currentPlaylist.value.size
            playTrack(_currentPlaylist.value[nextIndex], _currentPlaylist.value)
        }
    }

    fun skipToPrevious() {
        val controller = mediaController ?: return
        if (controller.currentPosition > 3000L) {
            controller.seekTo(0L)
            _currentPositionMs.value = 0L
        } else if (controller.hasPreviousMediaItem()) {
            controller.seekToPreviousMediaItem()
        } else if (_currentPlaylist.value.isNotEmpty()) {
            val currentIndex = _currentPlaylist.value.indexOfFirst { it.id == _currentTrack.value?.id }
            val prevIndex = if (currentIndex - 1 < 0) _currentPlaylist.value.size - 1 else currentIndex - 1
            playTrack(_currentPlaylist.value[prevIndex], _currentPlaylist.value)
        }
    }

    fun toggleShuffle() {
        val controller = mediaController ?: return
        val newState = !controller.shuffleModeEnabled
        controller.shuffleModeEnabled = newState
        _isShuffleEnabled.value = newState
    }

    fun toggleRepeat() {
        val controller = mediaController ?: return
        val nextMode = when (controller.repeatMode) {
            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
            Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
            else -> Player.REPEAT_MODE_OFF
        }
        controller.repeatMode = nextMode
        _repeatMode.value = nextMode
    }

    private fun startProgressTracker() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                mediaController?.let { controller ->
                    _currentPositionMs.value = controller.currentPosition.coerceAtLeast(0L)
                    if (controller.duration > 0) {
                        _durationMs.value = controller.duration
                    }
                }
                delay(400L)
            }
        }
    }

    private fun stopProgressTracker() {
        progressJob?.cancel()
        progressJob = null
    }

    fun release() {
        stopProgressTracker()
        controllerFuture?.let { MediaController.releaseFuture(it) }
    }
}
