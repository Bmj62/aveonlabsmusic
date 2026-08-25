package com.example

import android.app.Application
import android.util.Log
import com.example.data.local.LocalAudioScanner
import com.example.data.remote.FirebaseAudioRepository
import com.example.playback.MusicPlaybackManager
import com.google.firebase.FirebaseApp

class SoundWaveApp : Application() {

    lateinit var playbackManager: MusicPlaybackManager
        private set

    lateinit var firebaseRepo: FirebaseAudioRepository
        private set

    lateinit var localScanner: LocalAudioScanner
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this)
            }
        } catch (e: Exception) {
            Log.w("SoundWaveApp", "Firebase auto-initialization skipped: ${e.message}")
        }
        playbackManager = MusicPlaybackManager(this)
        firebaseRepo = FirebaseAudioRepository()
        localScanner = LocalAudioScanner(this)
    }

    companion object {
        lateinit var instance: SoundWaveApp
            private set
    }
}
