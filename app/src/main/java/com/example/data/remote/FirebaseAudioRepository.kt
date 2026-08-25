package com.example.data.remote

import android.util.Log
import com.example.data.model.Playlist
import com.example.data.model.Track
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirebaseAudioRepository {

    private fun getAuth(): FirebaseAuth? {
        return try {
            val app = try {
                if (FirebaseApp.getApps(com.example.SoundWaveApp.instance).isNotEmpty()) {
                    FirebaseApp.getInstance()
                } else {
                    FirebaseApp.initializeApp(com.example.SoundWaveApp.instance)
                }
            } catch (e: Exception) {
                null
            }
            if (app != null) FirebaseAuth.getInstance(app) else null
        } catch (e: Exception) {
            null
        }
    }

    private fun getFirestore(): FirebaseFirestore? {
        return try {
            val app = try {
                if (FirebaseApp.getApps(com.example.SoundWaveApp.instance).isNotEmpty()) {
                    FirebaseApp.getInstance()
                } else {
                    FirebaseApp.initializeApp(com.example.SoundWaveApp.instance)
                }
            } catch (e: Exception) {
                null
            }
            if (app != null) FirebaseFirestore.getInstance(app) else null
        } catch (e: Exception) {
            null
        }
    }

    val currentUser: FirebaseUser?
        get() = try { getAuth()?.currentUser } catch (e: Exception) { null }

    val isUserLoggedIn: Boolean
        get() = currentUser != null

    suspend fun signIn(email: String, pass: String): Result<FirebaseUser?> = withContext(Dispatchers.IO) {
        val auth = getAuth() ?: return@withContext Result.failure(
            IllegalStateException("Firebase is not configured yet. Please configure google-services.json or continue as Guest.")
        )
        try {
            val result = auth.signInWithEmailAndPassword(email, pass).await()
            Result.success(result.user)
        } catch (e: Exception) {
            Log.e("FirebaseAudioRepo", "Sign in error: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun signUp(email: String, pass: String): Result<FirebaseUser?> = withContext(Dispatchers.IO) {
        val auth = getAuth() ?: return@withContext Result.failure(
            IllegalStateException("Firebase is not configured yet. Please configure google-services.json or continue as Guest.")
        )
        try {
            val result = auth.createUserWithEmailAndPassword(email, pass).await()
            Result.success(result.user)
        } catch (e: Exception) {
            Log.e("FirebaseAudioRepo", "Sign up error: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogleCredential(idToken: String): Result<FirebaseUser?> = withContext(Dispatchers.IO) {
        val auth = getAuth() ?: return@withContext Result.failure(
            IllegalStateException("Firebase is not configured yet. Please configure google-services.json or continue as Guest.")
        )
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            Result.success(result.user)
        } catch (e: Exception) {
            Log.e("FirebaseAudioRepo", "Google sign in error: ${e.message}")
            Result.failure(e)
        }
    }

    fun signOut() {
        try {
            getAuth()?.signOut()
        } catch (e: Exception) {
            Log.w("FirebaseAudioRepo", "Sign out info: ${e.message}")
        }
    }

    // Default curated tracks for initial experience or when Firestore is offline/empty
    val defaultCuratedTracks: List<Track> = listOf(
        Track(
            id = "cloud_1",
            title = "Midnight Horizon",
            artist = "Neon Skyline",
            album = "Synthwave Odyssey",
            artworkUri = "https://images.unsplash.com/photo-1614613535308-eb5fbd3d2c17?w=600&auto=format&fit=crop&q=80",
            mediaUri = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
            durationMs = 372000L,
            isLocal = false,
            category = "Synthwave"
        ),
        Track(
            id = "cloud_2",
            title = "Golden Hour Reverie",
            artist = "Aura Bloom",
            album = "Chill & Relax",
            artworkUri = "https://images.unsplash.com/photo-1518609878373-06d740f60d8b?w=600&auto=format&fit=crop&q=80",
            mediaUri = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
            durationMs = 423000L,
            isLocal = false,
            category = "Lo-Fi"
        ),
        Track(
            id = "cloud_3",
            title = "Electric Pulse",
            artist = "Cyber Pulse",
            album = "Night City Drive",
            artworkUri = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop&q=80",
            mediaUri = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
            durationMs = 345000L,
            isLocal = false,
            category = "Electronic"
        ),
        Track(
            id = "cloud_4",
            title = "Velvet Rain",
            artist = "Luna Waves",
            album = "Atmospheric Sessions",
            artworkUri = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=600&auto=format&fit=crop&q=80",
            mediaUri = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
            durationMs = 502000L,
            isLocal = false,
            category = "Chillout"
        ),
        Track(
            id = "cloud_5",
            title = "Starlight Symphony",
            artist = "Solaris Echo",
            album = "Celestial Sounds",
            artworkUri = "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=600&auto=format&fit=crop&q=80",
            mediaUri = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3",
            durationMs = 318000L,
            isLocal = false,
            category = "Acoustic"
        ),
        Track(
            id = "cloud_6",
            title = "Urban Beats 2026",
            artist = "Metro Flow",
            album = "Underground Vol. 4",
            artworkUri = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600&auto=format&fit=crop&q=80",
            mediaUri = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-9.mp3",
            durationMs = 405000L,
            isLocal = false,
            category = "Trending"
        ),
        Track(
            id = "cloud_7",
            title = "Kesariya Sufi Breeze",
            artist = "Arijit & Pritam Ensemble",
            album = "Bollywood Romance 2026",
            artworkUri = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=600&auto=format&fit=crop&q=80",
            mediaUri = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3",
            durationMs = 380000L,
            isLocal = false,
            category = "Bollywood"
        ),
        Track(
            id = "cloud_8",
            title = "Dil Diyan Gallan (Acoustic Reprise)",
            artist = "Vishal & Shekhar Beats",
            album = "Bollywood Nights",
            artworkUri = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop&q=80",
            mediaUri = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3",
            durationMs = 340000L,
            isLocal = false,
            category = "Bollywood"
        ),
        Track(
            id = "cloud_9",
            title = "Naatu Energetic Rhythm",
            artist = "M.M. Keeravaani Tribute",
            album = "South Blockbusters",
            artworkUri = "https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=600&auto=format&fit=crop&q=80",
            mediaUri = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-7.mp3",
            durationMs = 295000L,
            isLocal = false,
            category = "South Music"
        ),
        Track(
            id = "cloud_10",
            title = "Arabic Kuthu Beats & Dappu",
            artist = "Anirudh Groove Wave",
            album = "Kollywood Fusion",
            artworkUri = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=600&auto=format&fit=crop&q=80",
            mediaUri = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-10.mp3",
            durationMs = 330000L,
            isLocal = false,
            category = "South Music"
        )
    )

    val defaultPlaylists: List<Playlist> = listOf(
        Playlist(
            id = "pl_today_top_hits",
            title = "Today's Top Hits",
            description = "The hottest tracks right now on SoundWave.",
            coverArtUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop&q=80",
            tracks = defaultCuratedTracks.take(4),
            gradientColorHex = "#1DB954"
        ),
        Playlist(
            id = "pl_bollywood_blockbusters",
            title = "Bollywood Melodies & Hits",
            description = "Soulful melodies, romantic ballads and Bollywood beats.",
            coverArtUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=600&auto=format&fit=crop&q=80",
            tracks = defaultCuratedTracks.filter { it.category == "Bollywood" || it.category == "Trending" },
            gradientColorHex = "#E1306C"
        ),
        Playlist(
            id = "pl_south_music_express",
            title = "South Music Power Hits",
            description = "Electrifying Kollywood, Tollywood and Carnatic fusion.",
            coverArtUrl = "https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=600&auto=format&fit=crop&q=80",
            tracks = defaultCuratedTracks.filter { it.category == "South Music" },
            gradientColorHex = "#FF5722"
        ),
        Playlist(
            id = "pl_chill_lofi",
            title = "Chill & Study Lo-Fi",
            description = "Gentle rhythms and soft beats for deep focus.",
            coverArtUrl = "https://images.unsplash.com/photo-1518609878373-06d740f60d8b?w=600&auto=format&fit=crop&q=80",
            tracks = defaultCuratedTracks.filter { it.category == "Lo-Fi" || it.category == "Chillout" },
            gradientColorHex = "#8C52FF"
        ),
        Playlist(
            id = "pl_synthwave_night",
            title = "Night Rider Synthwave",
            description = "Retrofuturistic synth basslines and neon melodies.",
            coverArtUrl = "https://images.unsplash.com/photo-1614613535308-eb5fbd3d2c17?w=600&auto=format&fit=crop&q=80",
            tracks = defaultCuratedTracks.filter { it.category == "Synthwave" || it.category == "Electronic" },
            gradientColorHex = "#2E77D0"
        ),
        Playlist(
            id = "pl_acoustic_sunset",
            title = "Acoustic Evening",
            description = "Warm acoustic instruments and intimate vocals.",
            coverArtUrl = "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=600&auto=format&fit=crop&q=80",
            tracks = defaultCuratedTracks.filter { it.category == "Acoustic" || it.category == "Lo-Fi" },
            gradientColorHex = "#FF7A00"
        )
    )

    fun fetchPlaylists(): Flow<List<Playlist>> = callbackFlow {
        val db = getFirestore()
        if (db == null) {
            trySend(defaultPlaylists)
            awaitClose { }
            return@callbackFlow
        }
        try {
            val listener = db.collection("playlists")
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null || snapshot.isEmpty) {
                        trySend(defaultPlaylists)
                        return@addSnapshotListener
                    }

                    val list = snapshot.documents.mapNotNull { doc ->
                        val id = doc.id
                        val title = doc.getString("title") ?: "Playlist"
                        val description = doc.getString("description") ?: ""
                        val coverArtUrl = doc.getString("coverArtUrl") ?: ""
                        val gradientHex = doc.getString("gradientColorHex") ?: "#1DB954"

                        Playlist(
                            id = id,
                            title = title,
                            description = description,
                            coverArtUrl = coverArtUrl,
                            tracks = defaultCuratedTracks,
                            gradientColorHex = gradientHex
                        )
                    }
                    trySend(if (list.isNotEmpty()) list else defaultPlaylists)
                }
            awaitClose { listener.remove() }
        } catch (e: Exception) {
            Log.w("FirebaseAudioRepo", "Firestore playlists error: ${e.message}, using defaults")
            trySend(defaultPlaylists)
            awaitClose { }
        }
    }.flowOn(Dispatchers.IO)

    fun fetchCloudTracks(): Flow<List<Track>> = callbackFlow {
        val db = getFirestore()
        if (db == null) {
            trySend(defaultCuratedTracks)
            awaitClose { }
            return@callbackFlow
        }
        try {
            val listener = db.collection("tracks")
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null || snapshot.isEmpty) {
                        trySend(defaultCuratedTracks)
                        return@addSnapshotListener
                    }

                    val list = snapshot.documents.mapNotNull { doc ->
                        val id = doc.id
                        val title = doc.getString("title") ?: "Unknown Title"
                        val artist = doc.getString("artist") ?: "Unknown Artist"
                        val album = doc.getString("album") ?: ""
                        val artworkUri = doc.getString("artworkUri")
                        val mediaUri = doc.getString("mediaUri") ?: return@mapNotNull null
                        val durationMs = doc.getLong("durationMs") ?: 0L
                        val category = doc.getString("category") ?: "Cloud"

                        Track(
                            id = id,
                            title = title,
                            artist = artist,
                            album = album,
                            artworkUri = artworkUri,
                            mediaUri = mediaUri,
                            durationMs = durationMs,
                            isLocal = false,
                            category = category
                        )
                    }
                    trySend(if (list.isNotEmpty()) list else defaultCuratedTracks)
                }
            awaitClose { listener.remove() }
        } catch (e: Exception) {
            Log.w("FirebaseAudioRepo", "Firestore tracks error: ${e.message}, using defaults")
            trySend(defaultCuratedTracks)
            awaitClose { }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun seedFirestoreData(): Result<Boolean> = withContext(Dispatchers.IO) {
        val db = getFirestore() ?: return@withContext Result.failure(
            IllegalStateException("Firebase is not configured yet. Please configure google-services.json first.")
        )
        try {
            val batch = db.batch()

            defaultCuratedTracks.forEach { track ->
                val trackDoc = db.collection("tracks").document(track.id)
                val trackData = mapOf(
                    "title" to track.title,
                    "artist" to track.artist,
                    "album" to track.album,
                    "artworkUri" to track.artworkUri,
                    "mediaUri" to track.mediaUri,
                    "durationMs" to track.durationMs,
                    "category" to track.category
                )
                batch.set(trackDoc, trackData)
            }

            defaultPlaylists.forEach { playlist ->
                val plDoc = db.collection("playlists").document(playlist.id)
                val plData = mapOf(
                    "title" to playlist.title,
                    "description" to playlist.description,
                    "coverArtUrl" to playlist.coverArtUrl,
                    "gradientColorHex" to playlist.gradientColorHex
                )
                batch.set(plDoc, plData)
            }

            batch.commit().await()
            Result.success(true)
        } catch (e: Exception) {
            Log.e("FirebaseAudioRepo", "Failed to seed Firestore: ${e.message}")
            Result.failure(e)
        }
    }
}
